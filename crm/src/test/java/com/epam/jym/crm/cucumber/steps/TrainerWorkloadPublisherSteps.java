package com.epam.jym.crm.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.jym.crm.cucumber.ScenarioState;
import com.epam.jym.crm.dto.ActionType;
import com.epam.jym.crm.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.facade.CrmFacade;
import com.epam.jym.crm.messaging.TrainerWorkloadMessagingProperties;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

public class TrainerWorkloadPublisherSteps {

  @Autowired private CrmFacade crmFacade;
  @Autowired private JmsTemplate jmsTemplate;
  @Autowired private TrainerWorkloadMessagingProperties properties;
  @Autowired private ScenarioState state;

  @Given("the JMS queue is drained")
  public void drainQueue() {
    jmsTemplate.setReceiveTimeout(200L);
    while (jmsTemplate.receive(properties.queue()) != null) {
      // discard stale messages
    }
    jmsTemplate.setReceiveTimeout(5_000L);
  }

  @And("that trainee has a training with trainer {string}, date {string}, duration {int}")
  public void addTrainingForLastTrainee(String trainerUsername, String date, int duration) {
    crmFacade.createTraining(
        new TrainingCreateDto(
            "Training " + date,
            state.getLastCreatedUsername(),
            trainerUsername,
            LocalDate.parse(date),
            duration));
  }

  @When("I delete that trainee")
  public void deleteLastTrainee() {
    crmFacade.deleteTrainee(state.getLastCreatedUsername());
  }

  @Then("one message is published to the workload queue")
  public void oneMessagePublished() {
    TrainerWorkloadUpdateRequest msg =
        (TrainerWorkloadUpdateRequest) jmsTemplate.receiveAndConvert(properties.queue());
    assertThat(msg).isNotNull();
    state.setLastWorkloadMessage(msg);
  }

  @And("the message action type is {string}")
  public void messageActionType(String expectedType) {
    assertThat(state.getLastWorkloadMessage().actionType())
        .isEqualTo(ActionType.valueOf(expectedType));
  }

  @And("the message trainer username is {string}")
  public void messageTrainerUsername(String expectedUsername) {
    assertThat(state.getLastWorkloadMessage().trainerUsername()).isEqualTo(expectedUsername);
  }

  @And("the message duration is {int}")
  public void messageDuration(int expectedDuration) {
    assertThat(state.getLastWorkloadMessage().durationInMinutes()).isEqualTo(expectedDuration);
  }

  @And("the message training date is {string}")
  public void messageTrainingDate(String expectedDate) {
    assertThat(state.getLastWorkloadMessage().trainingDate())
        .isEqualTo(LocalDate.parse(expectedDate));
  }

  @Then("exactly {int} messages are published to the workload queue")
  public void exactlyNMessagesPublished(int expectedCount) {
    List<TrainerWorkloadUpdateRequest> messages = new ArrayList<>();
    for (int i = 0; i < expectedCount; i++) {
      TrainerWorkloadUpdateRequest msg =
          (TrainerWorkloadUpdateRequest) jmsTemplate.receiveAndConvert(properties.queue());
      assertThat(msg).isNotNull();
      messages.add(msg);
    }
    state.setLastWorkloadMessages(messages);
  }

  @And("all messages have action type {string}")
  public void allMessagesHaveActionType(String expectedType) {
    ActionType expected = ActionType.valueOf(expectedType);
    assertThat(state.getLastWorkloadMessages())
        .allSatisfy(msg -> assertThat(msg.actionType()).isEqualTo(expected));
  }

  @Then("no message is published to the workload queue")
  public void noMessagePublished() {
    jmsTemplate.setReceiveTimeout(500L);
    Object msg = jmsTemplate.receiveAndConvert(properties.queue());
    assertThat(msg).isNull();
  }
}
