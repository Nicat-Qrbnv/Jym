package com.epam.jym.crm.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.epam.jym.crm.cucumber.ScenarioState;
import com.epam.jym.crm.service.BruteForceProtectionService;
import com.epam.jym.jwthandler.service.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class AuthenticationSteps {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtService jwtService;
  @Autowired private BruteForceProtectionService bruteForceProtectionService;
  @Autowired private ScenarioState state;

  @Given("the brute force counter is reset for user {string}")
  public void resetBruteForce(String username) {
    bruteForceProtectionService.registerSuccess(username);
  }

  @When("I log in with username {string} and password {string}")
  public void login(String username, String password) throws Exception {
    String body =
        """
        {"username":"%s","password":"%s"}
        """
            .formatted(username, password);

    MvcResult result =
        mockMvc
            .perform(post("/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
            .andReturn();

    state.setResult(result);
  }

  @Then("the response status is {int}")
  public void responseStatus(int expectedStatus) {
    assertThat(state.getResult().getResponse().getStatus()).isEqualTo(expectedStatus);
  }

  @And("the response contains a non-blank JWT token")
  public void responseContainsToken() throws Exception {
    JsonNode json = objectMapper.readTree(state.getResult().getResponse().getContentAsString());
    assertThat(json.get("token").asText()).isNotBlank();
  }

  @And("the token subject is {string}")
  public void tokenSubject(String expectedUsername) throws Exception {
    JsonNode json = objectMapper.readTree(state.getResult().getResponse().getContentAsString());
    String token = json.get("token").asText();
    assertThat(jwtService.extractUsername(token)).isEqualTo(expectedUsername);
  }

  @And("the response body contains {string}")
  public void responseBodyContains(String text) throws Exception {
    assertThat(state.getResult().getResponse().getContentAsString()).contains(text);
  }
}
