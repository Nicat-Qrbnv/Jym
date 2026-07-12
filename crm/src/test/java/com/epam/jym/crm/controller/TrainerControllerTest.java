package com.epam.jym.crm.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
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
        .perform(get("/v1/trainers/john.doe"))
        .andExpect(status().isOk());

    verify(crmFacade).getTrainerProfile("john.doe");
  }

  @Test
  void getProfileShouldReturnNotFoundWhenFacadeThrowsResourceNotFound() throws Exception {
    when(crmFacade.getTrainerProfile("unknown.trainer"))
        .thenThrow(new ResourceNotFoundException("Trainer not found: unknown.trainer"));

    mockMvc
        .perform(get("/v1/trainers/unknown.trainer"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Trainer not found: unknown.trainer"));
  }

  @Test
  void updateStatusShouldUpdateTrainerStatusAndReturnOk() throws Exception {
    mockMvc
        .perform(
            patch("/v1/trainers/jane.doe/change-status")
                .param("isActive", "true"))
        .andExpect(status().isOk());

    verify(crmFacade).changeUserStatus("jane.doe", true);
  }

  @Test
  void updateStatusShouldReturnBadRequestWhenIsActiveIsMissing() throws Exception {
    mockMvc
        .perform(patch("/v1/trainers/jane.doe/change-status"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateProfileShouldUpdateTrainerProfileAndReturnOk() throws Exception {
    mockMvc
        .perform(
            put("/v1/trainers/john.doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "profile": {
                        "firstName": "John",
                        "lastName": "Doe",
                        "isActive": true
                      },
                      "specialization": {
                        "id": 1,
                        "name": "Yoga"
                      }
                    }
                    """)
                )
        .andExpect(status().isOk());

    verify(crmFacade)
        .updateTrainerProfile(
            "john.doe",
            new TrainerUpdateDto(new UserDto("John", "Doe", true), new TrainingTypeDto(1L, "Yoga")));
  }
}
