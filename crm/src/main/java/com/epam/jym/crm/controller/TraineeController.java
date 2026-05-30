package com.epam.jym.crm.controller;

import static com.epam.jym.crm.util.CredentialsHeaderParser.parse;

import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeProfileDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.training.TraineeTrainingDto;
import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.facade.CrmFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public TraineeProfileDto getProfile(
      @NotBlank @RequestParam String username,
      @RequestHeader("user-credentials") String userCredentials) {
    return crmFacade.getTraineeProfile(parse(userCredentials), username);
  }

  @GetMapping("/not-assigned-trainers")
  @ResponseStatus(HttpStatus.OK)
  public List<TrainerDto> getNotAssignedActiveTrainers(
      @NotBlank @RequestParam String username,
      @RequestHeader("user-credentials") String userCredentials) {
    return crmFacade.getNotAssignedActiveTrainers(parse(userCredentials), username);
  }

  @GetMapping("/trainings")
  @ResponseStatus(HttpStatus.OK)
  public List<TraineeTrainingDto> getTrainings(
      @NotBlank @RequestParam String username,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate periodFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate periodTo,
      @RequestParam(required = false) String trainerName,
      @RequestParam(required = false) String trainingType,
      @RequestHeader("user-credentials") String userCredentials) {
    TraineeTrainingsCriteriaDto criteria =
        new TraineeTrainingsCriteriaDto(periodFrom, periodTo, trainerName, trainingType);
    return crmFacade.getTraineeTrainings(parse(userCredentials), username, criteria);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public TraineeProfileDto updateProfile(
      @RequestParam @NotBlank @Size(max = 310) String username,
      @Valid @RequestBody TraineeUpdateDto request,
      @RequestHeader("user-credentials") String userCredentials) {
    return crmFacade.updateTraineeProfile(parse(userCredentials), username, request);
  }

  @PatchMapping("/change-status")
  @ResponseStatus(HttpStatus.OK)
  public void updateStatus(
      @Valid @RequestParam String username,
      @RequestHeader("user-credentials") String userCredentials) {
    crmFacade.changeUserStatus(parse(userCredentials), username);
  }
  @DeleteMapping
  @ResponseStatus(HttpStatus.OK)
  public void deleteProfile(
      @NotBlank @RequestParam String username,
      @RequestHeader("user-credentials") String userCredentials) {
    crmFacade.deleteTrainee(parse(userCredentials), username);
  }
}
