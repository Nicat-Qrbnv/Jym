package com.epam.jym.trainerworkload.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.jym.trainerworkload.domain.TrainerWorkloadDocument;
import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexState;
import com.epam.jym.trainerworkload.dto.ActionType;
import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.trainerworkload.messaging.TrainerWorkloadDeadLetterMessage;
import com.epam.jym.trainerworkload.messaging.TrainerWorkloadMessagingProperties;
import com.epam.jym.trainerworkload.repository.TrainerWorkloadDocumentRepository;
import com.epam.jym.trainerworkload.repository.TrainingWorkloadIndexRepository;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

class TrainerWorkloadListenerIT extends AbstractIntegrationTest {

  private static final String USERNAME = "john.doe";
  private static final String FIRST = "John";
  private static final String LAST = "Doe";
  private static final long TRAINING_ID_1 = 101L;
  private static final long TRAINING_ID_2 = 102L;
  private static final LocalDate DATE_1 = LocalDate.of(2026, 8, 1);
  private static final LocalDate DATE_2 = LocalDate.of(2026, 8, 10);

  @Autowired private JmsTemplate jmsTemplate;
  @Autowired private TrainerWorkloadMessagingProperties properties;
  @Autowired private TrainerWorkloadDocumentRepository workloadRepo;
  @Autowired private TrainingWorkloadIndexRepository indexRepo;
  @Value("${messaging.trainer-workload.max-delivery-attempts}")
  private int maxDeliveryAttempts;

  static Stream<Arguments> scenarioSteps() {
    return Stream.of(
        Arguments.of(1, "first ADD creates document"),
        Arguments.of(2, "second ADD accumulates duration"),
        Arguments.of(3, "duplicate ADD is idempotent"),
        Arguments.of(4, "DELETE subtracts and marks index DELETED"));
  }

  static Stream<Arguments> dlqMaxAttempts() {
    return Stream.of(Arguments.of(3), Arguments.of(5));
  }

  private static int monthDuration(TrainerWorkloadDocument doc) {
    return doc.getYears().stream()
        .filter(y -> y.getYear() == 2026)
        .flatMap(y -> y.getMonths().stream())
        .filter(m -> m.getMonth() == 8)
        .mapToInt(TrainerWorkloadDocument.MonthSummary::getTotalDurationInMinutes)
        .sum();
  }

  private static TrainerWorkloadUpdateRequest add(long id, int duration, LocalDate date) {
    return new TrainerWorkloadUpdateRequest(
        USERNAME, FIRST, LAST, true, date, duration, ActionType.ADD, id);
  }

  private static TrainerWorkloadUpdateRequest delete(long id, int duration, LocalDate date) {
    return new TrainerWorkloadUpdateRequest(
        USERNAME, FIRST, LAST, true, date, duration, ActionType.DELETE, id);
  }

  @BeforeEach
  void cleanUp() {
    workloadRepo.deleteAll();
    indexRepo.deleteAll();
  }

  @ParameterizedTest(name = "step {0}: {1}")
  @MethodSource("scenarioSteps")
  void listenerScenario(int step, String description) throws Exception {
    switch (step) {
      case 1 -> step1_firstAdd();
      case 2 -> step2_secondAdd();
      case 3 -> step3_duplicateAdd();
      case 4 -> step4_deleteFirstAdd();
      default -> throw new IllegalArgumentException("Unknown step: " + step);
    }
  }

  private void step1_firstAdd() throws Exception {
    sendAndWait(add(TRAINING_ID_1, 30, DATE_1));

    TrainerWorkloadDocument doc = workloadRepo.findByUsername(USERNAME).orElseThrow();
    assertThat(monthDuration(doc)).isEqualTo(30);
    assertThat(indexRepo.findByTrainingId(TRAINING_ID_1).orElseThrow().state())
        .isEqualTo(TrainingWorkloadIndexState.ACTIVE);
  }

  private void step2_secondAdd() throws Exception {
    sendAndWait(add(TRAINING_ID_1, 30, DATE_1));
    sendAndWait(add(TRAINING_ID_2, 45, DATE_2));

    TrainerWorkloadDocument doc = workloadRepo.findByUsername(USERNAME).orElseThrow();
    assertThat(monthDuration(doc)).isEqualTo(75);
    assertThat(indexRepo.findByTrainingId(TRAINING_ID_2).orElseThrow().state())
        .isEqualTo(TrainingWorkloadIndexState.ACTIVE);
  }

  private void step3_duplicateAdd() throws Exception {
    sendAndWait(add(TRAINING_ID_2, 45, DATE_2));
    sendAndWait(add(TRAINING_ID_2, 45, DATE_2));

    TrainerWorkloadDocument doc = workloadRepo.findByUsername(USERNAME).orElseThrow();
    assertThat(monthDuration(doc)).isEqualTo(45);
  }

  private void step4_deleteFirstAdd() throws Exception {
    sendAndWait(add(TRAINING_ID_1, 30, DATE_1));
    sendAndWait(add(TRAINING_ID_2, 45, DATE_2));
    sendAndWait(delete(TRAINING_ID_1, 30, DATE_1));

    TrainerWorkloadDocument doc = workloadRepo.findByUsername(USERNAME).orElseThrow();
    assertThat(monthDuration(doc)).isEqualTo(45);
    assertThat(indexRepo.findByTrainingId(TRAINING_ID_1).orElseThrow().state())
        .isEqualTo(TrainingWorkloadIndexState.DELETED);
  }

  @Test
  @DisplayName(
      "routes to DLQ after "
          + "${messaging.trainer-workload.max-delivery-attempts}"
          + " failed delivery attempts")
  void listener_onRepeatedFailure_routesToDlq() {
    TrainerWorkloadUpdateRequest invalid = delete(999L, 60, LocalDate.of(2026, 8, 1));

    jmsTemplate.convertAndSend(
        properties.queue(),
        invalid,
        msg -> {
          msg.setIntProperty("JMSXDeliveryCount", maxDeliveryAttempts);
          return msg;
        });

    jmsTemplate.setReceiveTimeout(5_000L);
    TrainerWorkloadDeadLetterMessage dlqMsg =
        (TrainerWorkloadDeadLetterMessage) jmsTemplate.receiveAndConvert(properties.dlq());

    assertThat(dlqMsg).isNotNull();
    assertThat(dlqMsg.request().trainingId()).isEqualTo(999L);
    assertThat(dlqMsg.deliveryCount()).isEqualTo(maxDeliveryAttempts);
    assertThat(dlqMsg.errorMessage()).isNotBlank();
  }

  private void sendAndWait(TrainerWorkloadUpdateRequest request) throws InterruptedException {
    jmsTemplate.convertAndSend(properties.queue(), request);
    Thread.sleep(500);
  }
}
