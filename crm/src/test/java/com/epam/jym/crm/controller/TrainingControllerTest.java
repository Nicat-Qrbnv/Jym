package com.epam.jym.crm.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.exception.GlobalExceptionHandler;
import com.epam.jym.crm.facade.CrmFacade;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TrainingControllerTest {

  private CrmFacade crmFacade;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    crmFacade = Mockito.mock(CrmFacade.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new TrainingController(crmFacade))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getTrainerTrainingsShouldReturnTrainerTrainingsByCriteriaAndReturnOk() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/trainings/trainer/jane.doe")
                .param("periodFrom", "2026-05-01")
                .param("periodTo", "2026-05-31")
                .param("traineeName", "John Doe")
                .header("Authorization", "admin:password"))
        .andExpect(status().isOk());

    verify(crmFacade)
        .getTrainerTrainings(
            new CredentialsDto("admin", "password"),
            "jane.doe",
            new TrainerTrainingsCriteriaDto(
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), "John Doe"));
  }

  @Test
  void getTraineeTrainingsShouldReturnTraineeTrainingsByCriteriaAndReturnOk() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/trainings/trainee/john.doe")
                .param("periodFrom", "2026-05-01")
                .param("periodTo", "2026-05-31")
                .param("trainerName", "Jane Doe")
                .param("trainingType", "Fitness")
                .header("Authorization", "admin:password"))
        .andExpect(status().isOk());

    verify(crmFacade)
        .getTraineeTrainings(
            new CredentialsDto("admin", "password"),
            "john.doe",
            new TraineeTrainingsCriteriaDto(
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), "Jane Doe", "Fitness"));
  }
}
