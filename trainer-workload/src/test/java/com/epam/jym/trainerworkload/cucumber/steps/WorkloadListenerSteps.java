package com.epam.jym.trainerworkload.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.jym.trainerworkload.domain.TrainerWorkloadDocument;
import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexState;
import com.epam.jym.trainerworkload.dto.ActionType;
import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.trainerworkload.messaging.TrainerWorkloadDeadLetterMessage;
import com.epam.jym.trainerworkload.messaging.TrainerWorkloadMessagingProperties;
import com.epam.jym.trainerworkload.repository.TrainerWorkloadDocumentRepository;
import com.epam.jym.trainerworkload.repository.TrainingWorkloadIndexRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

public class WorkloadListenerSteps {

  private static final String FIRST_NAME = "John";
  private static final String LAST_NAME = "Doe";

  @Autowired private JmsTemplate jmsTemplate;
  @Autowired private TrainerWorkloadMessagingProperties properties;
  @Autowired private TrainerWorkloadDocumentRepository workloadRepo;
  @Autowired private TrainingWorkloadIndexRepository indexRepo;

  @Value("${messaging.trainer-workload.max-delivery-attempts}")
  private int maxDeliveryAttempts;

  @Before
  public void cleanRepositories() {
    workloadRepo.deleteAll();
    indexRepo.deleteAll();
  }

  @Given("the workload and index repositories are empty")
  public void repositoriesAreEmpty() {
    workloadRepo.deleteAll();
    indexRepo.deleteAll();
  }

  @When("I send an ADD message for trainer {string} with training id {long}, duration {int}, date {string}")
  public void sendAdd(String username, long trainingId, int duration, String date) throws InterruptedException {
    jmsTemplate.convertAndSend(properties.queue(), addRequest(username, trainingId, duration, LocalDate.parse(date)));
    Thread.sleep(500);
  }

  @When("I send a DELETE message for trainer {string} with training id {long}, duration {int}, date {string}")
  public void sendDelete(String username, long trainingId, int duration, String date) throws InterruptedException {
    jmsTemplate.convertAndSend(properties.queue(), deleteRequest(username, trainingId, duration, LocalDate.parse(date)));
    Thread.sleep(500);
  }

  @When("I send a DELETE message at max delivery attempts for training id {long}, duration {int}, date {string}")
  public void sendDeleteAtMaxAttempts(long trainingId, int duration, String date) throws InterruptedException {
    TrainerWorkloadUpdateRequest request =
        deleteRequest("john.doe", trainingId, duration, LocalDate.parse(date));
    jmsTemplate.convertAndSend(
        properties.queue(),
        request,
        msg -> {
          msg.setIntProperty("JMSXDeliveryCount", maxDeliveryAttempts);
          return msg;
        });
    Thread.sleep(500);
  }

  @Then("a workload document exists for trainer {string}")
  public void documentExists(String username) {
    assertThat(workloadRepo.findByUsername(username)).isPresent();
  }

  @And("the total duration for trainer {string} in year {int} month {int} is {int}")
  public void totalDuration(String username, int year, int month, int expectedDuration) {
    TrainerWorkloadDocument doc = workloadRepo.findByUsername(username).orElseThrow();
    int actual = doc.getYears().stream()
        .filter(y -> y.getYear() == year)
        .flatMap(y -> y.getMonths().stream())
        .filter(m -> m.getMonth() == month)
        .mapToInt(TrainerWorkloadDocument.MonthSummary::getTotalDurationInMinutes)
        .sum();
    assertThat(actual).isEqualTo(expectedDuration);
  }

  @And("the index state for training id {long} is {string}")
  public void indexState(long trainingId, String expectedState) {
    TrainingWorkloadIndexState state =
        indexRepo.findByTrainingId(trainingId).orElseThrow().state();
    assertThat(state).isEqualTo(TrainingWorkloadIndexState.valueOf(expectedState));
  }

  @Then("the DLQ contains a dead-letter message for training id {long}")
  public void dlqContainsMessage(long trainingId) {
    jmsTemplate.setReceiveTimeout(5_000L);
    TrainerWorkloadDeadLetterMessage dlqMsg =
        (TrainerWorkloadDeadLetterMessage) jmsTemplate.receiveAndConvert(properties.dlq());
    assertThat(dlqMsg).isNotNull();
    assertThat(dlqMsg.request().trainingId()).isEqualTo(trainingId);
    assertThat(dlqMsg.errorMessage()).isNotBlank();
  }

  private TrainerWorkloadUpdateRequest addRequest(
      String username, long trainingId, int duration, LocalDate date) {
    return new TrainerWorkloadUpdateRequest(
        username, FIRST_NAME, LAST_NAME, true, date, duration, ActionType.ADD, trainingId);
  }

  private TrainerWorkloadUpdateRequest deleteRequest(
      String username, long trainingId, int duration, LocalDate date) {
    return new TrainerWorkloadUpdateRequest(
        username, FIRST_NAME, LAST_NAME, true, date, duration, ActionType.DELETE, trainingId);
  }
}
