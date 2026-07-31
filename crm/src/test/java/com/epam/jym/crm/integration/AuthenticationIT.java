package com.epam.jym.crm.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.jym.crm.service.BruteForceProtectionService;
import com.epam.jym.jwthandler.service.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

class AuthenticationIT extends AbstractIntegrationTest {

  private static final String USERNAME = "oliver.bennett";
  private static final String VALID_PASSWORD = "A7mQ2zLp9R";
  private static final String WRONG_PASSWORD = "wrong-password";

  @Autowired private MockMvc mockMvc;

  @Autowired private JwtService jwtService;

  @Autowired private BruteForceProtectionService bruteForceProtectionService;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void resetBruteForceState() {
    bruteForceProtectionService.registerSuccess(USERNAME);
  }

  @Test
  void login_withValidCredentials_returnsJwtWithCorrectSubject() throws Exception {
    String body =
        """
        {"username":"%s","password":"%s"}
        """
            .formatted(USERNAME, VALID_PASSWORD);

    MvcResult result =
        mockMvc
            .perform(post("/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andReturn();

    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    String token = json.get("token").asText();
    long expiresIn = json.get("expiresIn").asLong();

    assertThat(token).isNotBlank();
    assertThat(jwtService.extractUsername(token)).isEqualTo(USERNAME);
    assertThat(expiresIn).isPositive();
  }

  @Test
  void login_afterThreeFailures_blocksUser() throws Exception {
    String badBody =
        """
        {"username":"%s","password":"%s"}
        """
            .formatted(USERNAME, WRONG_PASSWORD);

    for (int i = 0; i < 3; i++) {
      MvcResult result =
          mockMvc
              .perform(
                  post("/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(badBody))
              .andExpect(status().isUnauthorized())
              .andReturn();

      String detail = result.getResponse().getContentAsString();
      assertThat(detail).doesNotContain("temporarily blocked");
    }

    MvcResult blocked =
        mockMvc
            .perform(
                post("/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(badBody))
            .andExpect(status().isUnauthorized())
            .andReturn();

    assertThat(blocked.getResponse().getContentAsString()).contains("temporarily blocked");
  }
}
