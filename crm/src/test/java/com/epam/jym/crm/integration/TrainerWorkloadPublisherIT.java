package com.epam.jym.crm.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.jym.crm.dto.ActionType;
import com.epam.jym.crm.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.dto.user.CreatedCredentialsDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.facade.CrmFacade;
import com.epam.jym.crm.messaging.TrainerWorkloadMessagingProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

class TrainerWorkloadPublisherIT extends AbstractIntegrationTest {

  private static final String SEEDED_TRAINEE = "oliver.bennett";
  private static final String SEEDED_TRAINER = "henry.collins";

  @Autowired private CrmFacade crmFacade;

  @Autowired private JmsTemplate jmsTemplate;

  @Autowired private TrainerWorkloadMessagingProperties properties;

  static Stream<Arguments> trainingVariants() {
    return Stream.of(
        Arguments.of("Strength Training", 30, LocalDate.of(2026, 8, 1)),
        Arguments.of("Cardio Session", 60, LocalDate.of(2026, 8, 15)),
        Arguments.of("Flexibility Class", 90, LocalDate.of(2026, 9, 1)));
  }

  static Stream<Arguments> trainingCounts() {
    return Stream.of(
        Arguments.of(1, "Alpha", "Beta"),
        Arguments.of(2, "Gamma", "Delta"),
        Arguments.of(3, "Epsilon", "Zeta"));
  }

  @BeforeEach
  void drainQueue() {
    jmsTemplate.setReceiveTimeout(200L);
    while (jmsTemplate.receive(properties.queue()) != null) {
      // discard stale messages
    }
    jmsTemplate.setReceiveTimeout(5_000L);
  }

  @ParameterizedTest
  @MethodSource("trainingVariants")
  void createTraining_publishesAddMessageWithCorrectFields(
      String name, int duration, LocalDate date) {
    crmFacade.createTraining(
        new TrainingCreateDto(name, SEEDED_TRAINEE, SEEDED_TRAINER, date, duration));

    TrainerWorkloadUpdateRequest msg =
        (TrainerWorkloadUpdateRequest) jmsTemplate.receiveAndConvert(properties.queue());

    assertThat(msg).isNotNull();
    assertThat(msg.actionType()).isEqualTo(ActionType.ADD);
    assertThat(msg.trainerUsername()).isEqualTo(SEEDED_TRAINER);
    assertThat(msg.durationInMinutes()).isEqualTo(duration);
    assertThat(msg.trainingDate()).isEqualTo(date);
  }

  @ParameterizedTest
  @MethodSource("trainingCounts")
  void deleteTrainee_publishesDeleteMessageForEachTraining(
      int count, String firstName, String lastName) {
    CreatedCredentialsDto created =
        crmFacade.createTrainee(
            new TraineeCreateDto(new UserCreateDto(firstName, lastName), null, null));
    String traineeUsername = created.credentials().username();

    for (int i = 0; i < count; i++) {
      crmFacade.createTraining(
          new TrainingCreateDto(
              "Training " + i, traineeUsername, SEEDED_TRAINER, LocalDate.of(2026, 8, i + 1), 60));
    }

    // drain ADD messages
    drainQueue();

    crmFacade.deleteTrainee(traineeUsername);

    List<TrainerWorkloadUpdateRequest> deleteMessages = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      TrainerWorkloadUpdateRequest msg =
          (TrainerWorkloadUpdateRequest) jmsTemplate.receiveAndConvert(properties.queue());
      assertThat(msg).isNotNull();
      deleteMessages.add(msg);
    }

    assertThat(deleteMessages)
        .hasSize(count)
        .allSatisfy(msg -> assertThat(msg.actionType()).isEqualTo(ActionType.DELETE));
  }
}
