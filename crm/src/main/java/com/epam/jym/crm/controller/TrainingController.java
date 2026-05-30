package com.epam.jym.crm.controller;

import static com.epam.jym.crm.util.CredentialsHeaderParser.parse;

import com.epam.jym.crm.dto.training.TraineeTrainingDto;
import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.facade.CrmFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {

  private final CrmFacade crmFacade;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public void createTraining(
      @Valid @RequestBody TrainingCreateDto trainingDto,
      @RequestHeader("Authorization") String userCredentials) {
    crmFacade.createTraining(parse(userCredentials), trainingDto);
  }

  @GetMapping("/trainer")
  @ResponseStatus(HttpStatus.OK)
  public List<TrainerTrainingDto> getTrainings(
      @NotBlank @RequestParam String username,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate periodFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate periodTo,
      @RequestParam(required = false) String traineeName,
      @RequestHeader("Authorization") String userCredentials) {
    TrainerTrainingsCriteriaDto criteria =
        new TrainerTrainingsCriteriaDto(periodFrom, periodTo, traineeName);
    return crmFacade.getTrainerTrainings(parse(userCredentials), username, criteria);
  }

  @GetMapping("/trainee")
  @ResponseStatus(HttpStatus.OK)
  public List<TraineeTrainingDto> getTrainings(
      @NotBlank @RequestParam String username,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate periodFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate periodTo,
      @RequestParam(required = false) String trainerName,
      @RequestParam(required = false) String trainingType,
      @RequestHeader("Authorization") String userCredentials) {
    TraineeTrainingsCriteriaDto criteria =
        new TraineeTrainingsCriteriaDto(periodFrom, periodTo, trainerName, trainingType);
    return crmFacade.getTraineeTrainings(parse(userCredentials), username, criteria);
  }
}
