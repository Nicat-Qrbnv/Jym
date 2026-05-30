package com.epam.jym.crm.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.exception.GlobalExceptionHandler;
import com.epam.jym.crm.exception.ResourceNotFoundException;
import com.epam.jym.crm.facade.CrmFacade;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class TrainingTypeControllerTest {

  private CrmFacade crmFacade;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    crmFacade = Mockito.mock(CrmFacade.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new TrainingTypeController(crmFacade))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getTrainingTypesShouldReturnOk() throws Exception {
    when(crmFacade.getTrainingTypes()).thenReturn(List.of(new TrainingTypeDto(1L, "Fitness")));

    mockMvc.perform(get("/api/v1/training-types")).andExpect(status().isOk());

    verify(crmFacade).getTrainingTypes();
  }

  @Test
  void getTrainingTypesShouldReturnNotFoundWhenFacadeThrowsResourceNotFound() throws Exception {
    when(crmFacade.getTrainingTypes()).thenThrow(new ResourceNotFoundException("Types not found"));

    mockMvc
        .perform(get("/api/v1/training-types"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Types not found"));
  }
}
