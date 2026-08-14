package com.epam.jym.crm.cucumber.steps;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.epam.jym.crm.cucumber.ScenarioState;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class TrainerSteps {

  @Autowired private MockMvc mockMvc;
  @Autowired private ScenarioState state;

  @When("I register a trainer with first name {string} and last name {string}")
  public void registerTrainer(String firstName, String lastName) throws Exception {
    String body = """
        {"profile":{"firstName":"%s","lastName":"%s"}}
        """.formatted(firstName, lastName);

    MvcResult result = mockMvc
        .perform(post("/v1/trainers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andReturn();

    state.setResult(result);
  }

  @When("I get the trainer profile for username {string}")
  public void getTrainerProfile(String username) throws Exception {
    MvcResult result = mockMvc
        .perform(get("/v1/trainers/{username}", username))
        .andReturn();
    state.setResult(result);
  }
}
