package com.epam.jym.crm.facade;

import com.epam.jym.crm.auth.Authenticated;
import com.epam.jym.crm.auth.SkipAuthentication;
import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeProfileDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import com.epam.jym.crm.service.TrainingTypeService;
import com.epam.jym.crm.service.UserService;
import com.epam.jym.crm.util.mapper.TraineeMapper;
import com.epam.jym.crm.util.mapper.TrainerMapper;
import com.epam.jym.crm.util.mapper.TrainingMapper;
import com.epam.jym.crm.util.mapper.TrainingTypeMapper;
import com.epam.jym.crm.util.mapper.UserMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
@Authenticated
public class CrmFacadeImpl implements CrmFacade {

  private final TraineeService traineeService;
  private final TrainerService trainerService;
  private final TrainingService trainingService;
  private final TrainingTypeService trainingTypeService;
  private final UserService userService;
  private final TraineeMapper traineeMapper;
  private final TrainerMapper trainerMapper;
  private final UserMapper userMapper;
  private final TrainingMapper trainingMapper;
  private final TrainingTypeMapper trainingTypeMapper;

  @Override
  @SkipAuthentication
  public CredentialsDto createTrainee(TraineeCreateDto traineeDto) {
    log.debug("Facade request: create trainee");
    Trainee trainee = traineeService.createTrainee(traineeDto);
    log.info(
        "Facade completed: created trainee with id={} username={}",
        trainee.getId(),
        trainee.getUsername());
    return userMapper.toCredentialsDto(trainee.getUser());
  }

  @Override
  public void login(CredentialsDto credentials) {
    log.debug("Facade request: login username={}", credentials.username());
    log.info("Facade completed: login username={}", credentials.username());
  }

  @Override
  public void changeLogin(CredentialsDto credentials, String newPassword) {
    log.debug("Facade request: change login username={}", credentials.username());
    userService.changePassword(credentials.username(), newPassword);
    log.info("Facade completed: changed login username={}", credentials.username());
  }

  @Override
  public TraineeProfileDto updateTraineeProfile(
      CredentialsDto credentials, String username, TraineeUpdateDto traineeDto) {
    log.debug("Facade request: update trainee profile with username={}", username);
    Trainee trainee = traineeService.updateTraineeProfile(username, traineeDto);
    log.info(
        "Facade completed: updated trainee profile with id={} username={}",
        trainee.getId(),
        trainee.getUsername());
    return traineeMapper.toTraineeProfileDto(trainee);
  }

  @Override
  public void changeUserStatus(CredentialsDto credentials, String username) {
    log.debug("Facade request: update user status with username={}", username);
    userService.changeUserStatus(username);
    log.info("Facade completed: updated user status with username={}", username);
  }

  @Override
  public void deleteTrainee(CredentialsDto credentials, String username) {
    log.debug("Facade request: delete trainee with username={}", username);
    traineeService.deleteTrainee(username);
    log.info("Facade completed: deleted trainee with username={}", username);
  }

  @Override
  public TraineeProfileDto getTraineeProfile(CredentialsDto credentials, String username) {
    log.debug("Facade request: get trainee profile by username={}", username);
    Trainee trainee = traineeService.getTraineeByUsername(username);
    log.debug("Facade completed: selected trainee profile by username={}", username);
    return traineeMapper.toTraineeProfileDto(trainee);
  }

  @Override
  public List<TrainerDto> updateTraineeTrainers(
      CredentialsDto credentials, String traineeUsername, List<String> trainerUsernames) {
    log.debug("Facade request: update trainee trainers with trainee username={}", traineeUsername);
    List<Trainer> trainers =
        traineeService.updateTraineeTrainers(traineeUsername, trainerUsernames);
    log.info(
        "Facade completed: updated {} trainers for trainee username={}",
        trainers.size(),
        traineeUsername);
    return traineeMapper.toTrainerDtos(trainers);
  }

  @Override
  @SkipAuthentication
  public CredentialsDto createTrainer(TrainerCreateDto trainerDto) {
    log.debug("Facade request: create trainer");
    Trainer trainer = trainerService.createTrainer(trainerDto);
    log.info(
        "Facade completed: created trainer with id={} username={}",
        trainer.getId(),
        trainer.getUsername());
    return userMapper.toCredentialsDto(trainer.getUser());
  }

  @Override
  public TrainerProfileDto updateTrainerProfile(
      CredentialsDto credentials, String username, TrainerUpdateDto trainerDto) {
    log.debug("Facade request: update trainer profile with username={}", username);
    Trainer trainer = trainerService.updateTrainerProfile(username, trainerDto);
    log.info(
        "Facade completed: updated trainer profile with id={} username={}",
        trainer.getId(),
        trainer.getUsername());
    return trainerMapper.toTrainerProfileDto(trainer);
  }

  @Override
  public TrainerProfileDto getTrainerProfile(CredentialsDto credentials, String username) {
    log.debug("Facade request: get trainer profile by username={}", username);
    Trainer trainer = trainerService.getTrainerByUsername(username);
    log.debug("Facade completed: selected trainer profile by username={}", username);
    return trainerMapper.toTrainerProfileDto(trainer);
  }

  @Override
  public List<TrainerDto> getNotAssignedActiveTrainers(
      CredentialsDto credentials, String traineeUsername) {
    log.debug(
        "Facade request: get not assigned active trainers for trainee username={}",
        traineeUsername);
    List<Trainer> trainers = trainerService.getTrainersNotAssignedToTrainee(traineeUsername);
    log.debug(
        "Facade completed: selected {} not assigned active trainers for trainee username={}",
        trainers.size(),
        traineeUsername);
    return traineeMapper.toTrainerDtos(trainers);
  }

  @Override
  public List<TrainingDto> getTraineeTrainings(
      CredentialsDto credentials, String traineeUsername, TraineeTrainingsCriteriaDto criteria) {
    log.debug("Facade request: select trainings for trainee username={}", traineeUsername);
    List<Training> trainings = trainingService.getTraineeTrainings(traineeUsername, criteria);
    log.debug(
        "Facade completed: selected {} trainings for trainee username={}",
        trainings.size(),
        traineeUsername);
    return trainings.stream()
        .map(t -> trainingMapper.toTrainingDto(t, t.getTrainer().getUser()))
        .toList();
  }

  @Override
  public List<TrainingDto> getTrainerTrainings(
      CredentialsDto credentials, String trainerUsername, TrainerTrainingsCriteriaDto criteria) {
    log.debug("Facade request: select trainings for trainer username={}", trainerUsername);
    List<Training> trainings = trainingService.getTrainerTrainings(trainerUsername, criteria);
    log.debug(
        "Facade completed: selected {} trainings for trainer username={}",
        trainings.size(),
        trainerUsername);
    return trainings.stream()
        .map(t -> trainingMapper.toTrainingDto(t, t.getTrainee().getUser()))
        .toList();
  }

  @Override
  @SkipAuthentication
  public List<TrainingTypeDto> getTrainingTypes() {
    log.debug("Facade request: select all training types");
    List<TrainingType> trainingTypes = trainingTypeService.getAllTypes();
    log.debug("Facade completed: selected {} training types", trainingTypes.size());
    return trainingTypes.stream().map(trainingTypeMapper::toTrainingTypeDto).toList();
  }

  @Override
  public void createTraining(CredentialsDto credentials, TrainingCreateDto trainingDto) {
    log.debug("Facade request: create training");
    Training training = trainingService.createTraining(trainingDto);
    log.info(
        "Facade completed: created training with id={} trainee username={}",
        training.getId(),
        training.getTrainee().getUsername());
  }
}
