package com.epam.jym.crm.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.epam.jym.crm.cucumber.ScenarioState;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class TraineeSteps {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ScenarioState state;

  @When("I register a trainee with first name {string} and last name {string}")
  public void registerTrainee(String firstName, String lastName) throws Exception {
    String body = """
        {"profile":{"firstName":"%s","lastName":"%s"}}
        """.formatted(firstName, lastName);

    MvcResult result = mockMvc
        .perform(post("/v1/trainees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andReturn();

    state.setResult(result);

    if (result.getResponse().getStatus() == 200) {
      JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
      state.setLastCreatedUsername(json.path("credentials").path("username").asText());
    }
  }

  @Given("a trainee exists with first name {string} and last name {string}")
  public void traineeExists(String firstName, String lastName) throws Exception {
    registerTrainee(firstName, lastName);
  }

  @When("I get the trainee profile for that trainee")
  public void getProfileForLastCreated() throws Exception {
    getTraineeProfile(state.getLastCreatedUsername());
  }

  @When("I get the trainee profile for username {string}")
  public void getTraineeProfile(String username) throws Exception {
    MvcResult result = mockMvc
        .perform(get("/v1/trainees/{username}", username))
        .andReturn();
    state.setResult(result);
  }

  @And("the response contains a non-blank username")
  public void responseContainsUsername() throws Exception {
    JsonNode json = objectMapper.readTree(state.getResult().getResponse().getContentAsString());
    assertThat(json.path("credentials").path("username").asText()).isNotBlank();
  }

  @And("the response contains a non-blank password")
  public void responseContainsPassword() throws Exception {
    JsonNode json = objectMapper.readTree(state.getResult().getResponse().getContentAsString());
    assertThat(json.path("credentials").path("password").asText()).isNotBlank();
  }

  @And("the response contains first name {string} and last name {string}")
  public void responseContainsName(String firstName, String lastName) throws Exception {
    JsonNode json = objectMapper.readTree(state.getResult().getResponse().getContentAsString());
    assertThat(json.path("firstName").asText()).isEqualTo(firstName);
    assertThat(json.path("lastName").asText()).isEqualTo(lastName);
  }
}
