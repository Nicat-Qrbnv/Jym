package com.epam.jym.crm.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.user.UserDto;
import com.epam.jym.crm.exception.GlobalExceptionHandler;
import com.epam.jym.crm.exception.InvalidCredentialsException;
import com.epam.jym.crm.facade.CrmFacade;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TraineeControllerTest {

  private CrmFacade crmFacade;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    crmFacade = Mockito.mock(CrmFacade.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new TraineeController(crmFacade))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void deleteProfileShouldDeleteTraineeByUsernameAndReturnOk() throws Exception {
    mockMvc
        .perform(delete("/v1/trainees/john.doe"))
        .andExpect(status().isOk());

    verify(crmFacade).deleteTrainee("john.doe");
  }

  @Test
  void getNotAssignedActiveTrainersShouldReturnOk() throws Exception {
    mockMvc
        .perform(get("/v1/trainees/john.doe/not-assigned-trainers"))
        .andExpect(status().isOk());

    verify(crmFacade).getNotAssignedActiveTrainers("john.doe");
  }

  @Test
  void getNotAssignedActiveTrainersShouldReturnUnauthorizedWhenCredentialsAreInvalid()
      throws Exception {
    when(crmFacade.getNotAssignedActiveTrainers("john.doe"))
        .thenThrow(new InvalidCredentialsException("Invalid username or password"));

    mockMvc
        .perform(get("/v1/trainees/john.doe/not-assigned-trainers"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.detail").value("Invalid username or password"));
  }

  @Test
  void updateProfileShouldUpdateTraineeProfileAndReturnOk() throws Exception {
    mockMvc
        .perform(
            put("/v1/trainees/john.doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "user": {
                        "firstName": "John",
                        "lastName": "Doe",
                        "isActive": true
                      },
                      "dateOfBirth": "2000-01-01",
                      "address": "Main Street"
                    }
                    """))
        .andExpect(status().isOk());

    verify(crmFacade)
        .updateTraineeProfile(
            "john.doe",
            new TraineeUpdateDto(
                new UserDto("John", "Doe", true), LocalDate.of(2000, 1, 1), "Main Street"));
  }

  @Test
  void updateStatusShouldUpdateTraineeStatusAndReturnOk() throws Exception {
    mockMvc
        .perform(
            patch("/v1/trainees/john.doe/change-status")
                .param("isActive", "false"))
        .andExpect(status().isOk());

    verify(crmFacade).changeUserStatus("john.doe", false);
  }

  @Test
  void updateStatusShouldReturnBadRequestWhenIsActiveIsMissing() throws Exception {
    mockMvc
        .perform(patch("/v1/trainees/john.doe/change-status"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateTrainersShouldUpdateTraineeTrainerListAndReturnOk() throws Exception {
    mockMvc
        .perform(
            put("/v1/trainees/john.doe/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    ["first.trainer", "second.trainer"]
                    """))
        .andExpect(status().isOk());

    verify(crmFacade)
        .updateTraineeTrainers("john.doe", List.of("first.trainer", "second.trainer"));
  }
}
