package com.epam.jym.crm.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.user.UserDto;
import com.epam.jym.crm.exception.GlobalExceptionHandler;
import com.epam.jym.crm.exception.ResourceNotFoundException;
import com.epam.jym.crm.facade.CrmFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TrainerControllerTest {

  private CrmFacade crmFacade;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    crmFacade = Mockito.mock(CrmFacade.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new TrainerController(crmFacade))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getProfileShouldReturnTrainerProfileByUsernameAndReturnOk() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/trainers")
                .param("username", "john.doe")
                .header("Authorization", "admin:password"))
        .andExpect(status().isOk());

    verify(crmFacade).getTrainerProfile(new CredentialsDto("admin", "password"), "john.doe");
  }

  @Test
  void getProfileShouldReturnNotFoundWhenFacadeThrowsResourceNotFound() throws Exception {
    when(crmFacade.getTrainerProfile(new CredentialsDto("admin", "password"), "unknown.trainer"))
        .thenThrow(new ResourceNotFoundException("Trainer not found: unknown.trainer"));

    mockMvc
        .perform(
            get("/api/v1/trainers")
                .param("username", "unknown.trainer")
                .header("Authorization", "admin:password"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Trainer not found: unknown.trainer"));
  }

  @Test
  void getProfileShouldReturnUnauthorizedWhenAuthorizationHeaderIsMissing() throws Exception {
    mockMvc
        .perform(get("/api/v1/trainers").param("username", "john.doe"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.detail").value(containsString("Authorization")));
  }

  @Test
  void updateStatusShouldUpdateTrainerStatusAndReturnOk() throws Exception {
    mockMvc
        .perform(
            patch("/api/v1/trainers/change-status")
                .param("username", "jane.doe")
                .header("Authorization", "admin:password"))
        .andExpect(status().isOk());

    verify(crmFacade).changeUserStatus(new CredentialsDto("admin", "password"), "jane.doe");
  }

  @Test
  void updateProfileShouldUpdateTrainerProfileAndReturnOk() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/trainers")
                .param("username", "john.doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "profile": {
                        "firstName": "John",
                        "lastName": "Doe",
                        "isActive": true
                      }
                    }
                    """)
                .header("Authorization", "admin:password"))
        .andExpect(status().isOk());

    verify(crmFacade)
        .updateTrainerProfile(
            new CredentialsDto("admin", "password"),
            "john.doe",
            new TrainerUpdateDto(new UserDto("John", "Doe", true), null));
  }
}
