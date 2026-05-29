package com.epam.jym.crm.controller;


import com.epam.jym.crm.dto.auth.CredentialsDto;
import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.facade.CrmFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
public class TraineeController {

  private final CrmFacade crmFacade;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public CredentialsDto register(@Valid @RequestBody TraineeCreateDto request) {
    return crmFacade.createTrainee(request);
  }

}
