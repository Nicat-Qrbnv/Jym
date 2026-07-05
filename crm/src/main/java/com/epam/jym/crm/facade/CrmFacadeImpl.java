package com.epam.jym.crm.facade;

import com.epam.jym.crm.client.workload.TrainerWorkloadClient;
import com.epam.jym.crm.logging.LogOperation;
import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeProfileDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.trainee.UpdatedTraineeProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerSummaryDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.trainer.UpdatedTrainerProfileDto;
import com.epam.jym.crm.dto.training.TraineeTrainingDto;
import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.CreatedCredentialsDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.user.PasswordUpdateDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.service.AuthenticationService;
import com.epam.jym.crm.service.JwtService;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import com.epam.jym.crm.service.TrainingTypeService;
import com.epam.jym.crm.service.UserService;
import com.epam.jym.crm.util.mapper.core.CrmMapper;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
@LogOperation("CrmFacade")
public class CrmFacadeImpl implements CrmFacade {

  private final TraineeService traineeService;
  private final TrainerService trainerService;
  private final AuthenticationService authenticationService;
  private final JwtService jwtService;
  private final TrainingService trainingService;
  private final TrainingTypeService trainingTypeService;
  private final UserService userService;
  private final CrmMapper crmMapper;
  private final TrainerWorkloadClient trainerWorkloadClient;

  @Override
  public CreatedCredentialsDto createTrainee(TraineeCreateDto traineeDto) {
    log.debug("Facade request: create trainee");
    Trainee trainee = traineeService.createTrainee(traineeDto);
    log.info(
        "Facade completed: created trainee with id={} username={}",
        trainee.getId(),
        trainee.getUsername());
    return toCreatedCredentials(crmMapper.map(trainee.getUser(), CredentialsDto.class));
  }

  @Override
  public void login(CredentialsDto credentials) {
    log.debug("Facade request: login username={}", credentials.username());
    authenticationService.authenticate(credentials);
    log.info("Facade completed: login username={}", credentials.username());
  }

  @Override
  public void changeLogin(String username, PasswordUpdateDto passwordUpdateDto) {
    log.debug("Facade request: change login username={}",username);
    userService.changePassword(username, passwordUpdateDto);
    log.info("Facade completed: changed login username={}", username);
  }

  @Override
  public UpdatedTraineeProfileDto updateTraineeProfile(
      String username, TraineeUpdateDto traineeDto) {
    log.debug("Facade request: update trainee profile with username={}", username);
    Trainee trainee = traineeService.updateTraineeProfile(username, traineeDto);
    log.info(
        "Facade completed: updated trainee profile with id={} username={}",
        trainee.getId(),
        trainee.getUsername());
    return crmMapper.map(trainee, UpdatedTraineeProfileDto.class);
  }

  @Override
  public void changeUserStatus(String username, boolean isActive) {
    log.debug("Facade request: update user status with username={}", username);
    userService.changeUserStatus(username, isActive);
    log.info("Facade completed: updated user status with username={}", username);
  }

  @Override
  public void deleteTrainee(String username) {
    log.debug("Facade request: delete trainee with username={}", username);
    traineeService.deleteTrainee(username);
    log.info("Facade completed: deleted trainee with username={}", username);
  }

  @Override
  public TraineeProfileDto getTraineeProfile(String username) {
    log.debug("Facade request: get trainee profile by username={}", username);
    Trainee trainee = traineeService.getTraineeByUsername(username);
    log.debug("Facade completed: selected trainee profile by username={}", username);
    return crmMapper.map(trainee, TraineeProfileDto.class);
  }

  @Override
  public List<TrainerSummaryDto> updateTraineeTrainers(
      String traineeUsername, List<String> trainerUsernames) {
    log.debug("Facade request: update trainee trainers with trainee username={}", traineeUsername);
    Set<Trainer> trainers = traineeService.updateTraineeTrainers(traineeUsername, trainerUsernames);
    log.info(
        "Facade completed: updated {} trainers for trainee username={}",
        trainers.size(),
        traineeUsername);
    return crmMapper.mapCollection(trainers, TrainerSummaryDto.class).toList();
  }

  @Override
  public CreatedCredentialsDto createTrainer(TrainerCreateDto trainerDto) {
    log.debug("Facade request: create trainer");
    Trainer trainer = trainerService.createTrainer(trainerDto);
    log.info(
        "Facade completed: created trainer with id={} username={}",
        trainer.getId(),
        trainer.getUsername());
    return toCreatedCredentials(crmMapper.map(trainer.getUser(), CredentialsDto.class));
  }

  @Override
  public UpdatedTrainerProfileDto updateTrainerProfile(
      String username, TrainerUpdateDto trainerDto) {
    log.debug("Facade request: update trainer profile with username={}", username);
    Trainer trainer = trainerService.updateTrainerProfile(username, trainerDto);
    log.info(
        "Facade completed: updated trainer profile with id={} username={}",
        trainer.getId(),
        trainer.getUsername());
    return crmMapper.map(trainer, UpdatedTrainerProfileDto.class);
  }

  @Override
  public TrainerProfileDto getTrainerProfile(String username) {
    log.debug("Facade request: get trainer profile by username={}", username);
    Trainer trainer = trainerService.getTrainerByUsername(username);
    log.debug("Facade completed: selected trainer profile by username={}", username);
    return crmMapper.map(trainer, TrainerProfileDto.class);
  }

  @Override
  public List<TrainerSummaryDto> getNotAssignedActiveTrainers(String traineeUsername) {
    log.debug(
        "Facade request: get not assigned active trainers for trainee username={}",
        traineeUsername);
    List<Trainer> trainers = trainerService.getTrainersNotAssignedToTrainee(traineeUsername);
    log.debug(
        "Facade completed: selected {} not assigned active trainers for trainee username={}",
        trainers.size(),
        traineeUsername);
    return crmMapper.mapCollection(trainers, TrainerSummaryDto.class).toList();
  }

  @Override
  public List<TraineeTrainingDto> getTraineeTrainings(
      String traineeUsername, TraineeTrainingsCriteriaDto criteria) {
    log.debug("Facade request: select trainings for trainee username={}", traineeUsername);
    List<Training> trainings = trainingService.getTraineeTrainings(traineeUsername, criteria);
    log.debug(
        "Facade completed: selected {} trainings for trainee username={}",
        trainings.size(),
        traineeUsername);
    return crmMapper.mapCollection(trainings, TraineeTrainingDto.class).toList();
  }

  @Override
  public List<TrainerTrainingDto> getTrainerTrainings(
      String trainerUsername, TrainerTrainingsCriteriaDto criteria) {
    log.debug("Facade request: select trainings for trainer username={}", trainerUsername);
    List<Training> trainings = trainingService.getTrainerTrainings(trainerUsername, criteria);
    log.debug(
        "Facade completed: selected {} trainings for trainer username={}",
        trainings.size(),
        trainerUsername);
    return crmMapper.mapCollection(trainings, TrainerTrainingDto.class).toList();
  }

  @Override
  public List<TrainingTypeDto> getTrainingTypes() {
    log.debug("Facade request: select all training types");
    List<TrainingType> trainingTypes = trainingTypeService.getAllTypes();
    log.debug("Facade completed: selected {} training types", trainingTypes.size());
    return crmMapper.mapCollection(trainingTypes, TrainingTypeDto.class).toList();
  }

  @Override
  public void createTraining(TrainingCreateDto trainingDto) {
    log.debug("Facade request: create training");
    Training training = trainingService.createTraining(trainingDto);
    trainerWorkloadClient.sendAddWorkloadUpdate(training);
    log.info(
        "Facade completed: created training with id={} trainee username={}",
        training.getId(),
        training.getTrainee().getUsername());
  }

  private CreatedCredentialsDto toCreatedCredentials(CredentialsDto credentials) {
    return new CreatedCredentialsDto(
        credentials,
        new com.epam.jym.crm.dto.user.AuthTokenDto(
            jwtService.generateToken(credentials.username()), jwtService.expirationSeconds()));
  }
}
