package com.epam.jym.crm.facade;

import com.epam.jym.crm.auth.Authenticated;
import com.epam.jym.crm.auth.SkipAuthentication;
import com.epam.jym.crm.dto.auth.CredentialsDto;
import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
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

  @Override
  @SkipAuthentication
  public TraineeDto createTrainee(TraineeCreateDto traineeDto) {
    log.debug("Facade request: create trainee");
    return traineeService.createTrainee(traineeDto);
  }

  @Override
  public TraineeDto updateTrainee(
      CredentialsDto credentials, Long traineeId, TraineeUpdateDto traineeDto) {
    log.debug("Facade request: update trainee with id={}", traineeId);
    return traineeService.updateTrainee(traineeId, traineeDto);
  }

  @Override
  public void deleteTrainee(CredentialsDto credentials, Long traineeId) {
    log.debug("Facade request: delete trainee with id={}", traineeId);
    traineeService.deleteTrainee(traineeId);
  }

  @Override
  public void deleteTrainee(CredentialsDto credentials, String username) {
    log.debug("Facade request: delete trainee with username={}", username);
    traineeService.deleteTrainee(username);
  }

  @Override
  public TraineeDto getTraineeById(CredentialsDto credentials, Long traineeId) {
    log.debug("Facade request: get trainee with id={}", traineeId);
    return traineeService.getTraineeById(traineeId);
  }

  @Override
  public TraineeDto getTraineeByUsername(CredentialsDto credentials, String username) {
    log.debug("Facade request: get trainee by username={}", username);
    return traineeService.getTraineeByUsername(username);
  }

  @Override
  public List<TraineeDto> getAllTrainees(CredentialsDto credentials) {
    log.debug("Facade request: get all trainees");
    return traineeService.getAllTrainees();
  }

  @Override
  public List<TrainerDto> updateTraineeTrainers(
      CredentialsDto credentials, Long traineeId, List<Long> trainerIds) {
    log.debug("Facade request: update trainee trainers with trainee id={}", traineeId);
    return traineeService.updateTraineeTrainers(traineeId, trainerIds);
  }

  @Override
  @SkipAuthentication
  public TrainerDto createTrainer(TrainerCreateDto trainerDto) {
    log.debug("Facade request: create trainer");
    return trainerService.createTrainer(trainerDto);
  }

  @Override
  public TrainerDto updateTrainer(
      CredentialsDto credentials, Long trainerId, TrainerUpdateDto trainerDto) {
    log.debug("Facade request: update trainer with id={}", trainerId);
    return trainerService.updateTrainer(trainerId, trainerDto);
  }

  @Override
  public TrainerDto selectTrainer(CredentialsDto credentials, Long trainerId) {
    log.debug("Facade request: select trainer with id={}", trainerId);
    return trainerService.selectTrainer(trainerId);
  }

  @Override
  public TrainerDto selectTrainerByUsername(CredentialsDto credentials, String username) {
    log.debug("Facade request: select trainer by username={}", username);
    return trainerService.getTrainerByUsername(username);
  }

  @Override
  public List<TrainerDto> selectAllTrainers(CredentialsDto credentials) {
    log.debug("Facade request: select all trainers");
    return trainerService.getAllTrainers();
  }

  @Override
  public List<TrainerDto> selectTrainersNotAssignedToTrainee(
      CredentialsDto credentials, String traineeUsername) {
    log.debug(
        "Facade request: select trainers not assigned to trainee username={}", traineeUsername);
    return trainerService.selectTrainersNotAssignedToTrainee(traineeUsername);
  }

  @Override
  public TrainingDto createTraining(CredentialsDto credentials, TrainingCreateDto trainingDto) {
    log.debug("Facade request: create training");
    return trainingService.createTraining(trainingDto);
  }

  @Override
  public TrainingDto getTraining(CredentialsDto credentials, Long trainingId) {
    log.debug("Facade request: select training with id={}", trainingId);
    return trainingService.getTraining(trainingId);
  }

  @Override
  public List<TrainingDto> getAllTrainings(CredentialsDto credentials) {
    log.debug("Facade request: select all trainings");
    return trainingService.getAllTrainings();
  }

  @Override
  public List<TrainingDto> getTraineeTrainings(
      CredentialsDto credentials, String traineeUsername, TraineeTrainingsCriteriaDto criteria) {
    log.debug("Facade request: select trainings for trainee username={}", traineeUsername);
    return trainingService.getTraineeTrainings(traineeUsername, criteria);
  }

  @Override
  public List<TrainingDto> getTrainerTrainings(
      CredentialsDto credentials, String trainerUsername, TrainerTrainingsCriteriaDto criteria) {
    log.debug("Facade request: select trainings for trainer username={}", trainerUsername);
    return trainingService.getTrainerTrainings(trainerUsername, criteria);
  }
}
