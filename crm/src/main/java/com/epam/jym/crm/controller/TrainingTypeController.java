package com.epam.jym.crm.controller;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.facade.CrmFacade;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/training-types")
@RequiredArgsConstructor
public class TrainingTypeController {

  private final CrmFacade crmFacade;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<TrainingTypeDto> getTrainingTypes() {
    return crmFacade.getTrainingTypes();
  }
}
