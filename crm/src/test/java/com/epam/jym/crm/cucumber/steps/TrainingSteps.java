package com.epam.jym.crm.cucumber.steps;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.epam.jym.crm.cucumber.ScenarioState;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class TrainingSteps {

  @Autowired private MockMvc mockMvc;
  @Autowired private ScenarioState state;

  @When("I create a training with name {string}, trainee {string}, trainer {string}, date {string}, duration {int}")
  public void createTraining(
      String name, String traineeUsername, String trainerUsername, String date, int duration)
      throws Exception {
    String body = """
        {
          "name": "%s",
          "traineeUsername": "%s",
          "trainerUsername": "%s",
          "date": "%s",
          "durationInMinutes": %d
        }
        """.formatted(name, traineeUsername, trainerUsername, date, duration);

    MvcResult result = mockMvc
        .perform(post("/v1/trainings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andReturn();

    state.setResult(result);
  }
}
