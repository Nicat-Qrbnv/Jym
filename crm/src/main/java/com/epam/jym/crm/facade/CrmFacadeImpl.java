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
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import com.epam.jym.crm.util.mapper.TraineeMapper;
import com.epam.jym.crm.util.mapper.TrainerMapper;
import com.epam.jym.crm.util.mapper.TrainingMapper;
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
  private final TraineeMapper traineeMapper;
  private final TrainerMapper trainerMapper;
  private final TrainingMapper trainingMapper;

  @Override
  @SkipAuthentication
  public CredentialsDto createTrainee(TraineeCreateDto traineeDto) {
    log.debug("Facade request: create trainee");
    Trainee trainee = traineeService.createTrainee(traineeDto);
    log.info(
        "Facade completed: created trainee with id={} username={}",
        trainee.getId(),
        trainee.getUsername());
    return traineeMapper.toCredentialsDto(trainee);
  }

  @Override
  public TraineeDto updateTrainee(
      CredentialsDto credentials, Long traineeId, TraineeUpdateDto traineeDto) {
    log.debug("Facade request: update trainee with id={}", traineeId);
    Trainee trainee = traineeService.updateTrainee(traineeId, traineeDto);
    log.info(
        "Facade completed: updated trainee with id={} username={}",
        trainee.getId(),
        trainee.getUsername());
    return traineeMapper.toTraineeDto(trainee);
  }

  @Override
  public void deleteTrainee(CredentialsDto credentials, Long traineeId) {
    log.debug("Facade request: delete trainee with id={}", traineeId);
    traineeService.deleteTrainee(traineeId);
    log.info("Facade completed: deleted trainee with id={}", traineeId);
  }

  @Override
  public void deleteTrainee(CredentialsDto credentials, String username) {
    log.debug("Facade request: delete trainee with username={}", username);
    traineeService.deleteTrainee(username);
    log.info("Facade completed: deleted trainee with username={}", username);
  }

  @Override
  public TraineeDto getTraineeById(CredentialsDto credentials, Long traineeId) {
    log.debug("Facade request: get trainee with id={}", traineeId);
    Trainee trainee = traineeService.getTrainee(traineeId);
    log.debug("Facade completed: selected trainee with id={}", traineeId);
    return traineeMapper.toTraineeDto(trainee);
  }

  @Override
  public TraineeDto getTraineeByUsername(CredentialsDto credentials, String username) {
    log.debug("Facade request: get trainee by username={}", username);
    Trainee trainee = traineeService.getTraineeByUsername(username);
    log.debug("Facade completed: selected trainee by username={}", username);
    return traineeMapper.toTraineeDto(trainee);
  }

  @Override
  public List<TraineeDto> getAllTrainees(CredentialsDto credentials) {
    log.debug("Facade request: get all trainees");
    List<Trainee> trainees = traineeService.getAllTrainees();
    log.debug("Facade completed: selected {} trainees", trainees.size());
    return trainees.stream().map(traineeMapper::toTraineeDto).toList();
  }

  @Override
  public List<TrainerDto> updateTraineeTrainers(
      CredentialsDto credentials, Long traineeId, List<Long> trainerIds) {
    log.debug("Facade request: update trainee trainers with trainee id={}", traineeId);
    List<Trainer> trainers = traineeService.updateTraineeTrainers(traineeId, trainerIds);
    log.info("Facade completed: updated {} trainers for trainee id={}", trainers.size(), traineeId);
    return trainerMapper.toTrainerDtoList(trainers);
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
    return trainerMapper.toCredentialsDto(trainer);
  }

  @Override
  public TrainerDto updateTrainer(
      CredentialsDto credentials, Long trainerId, TrainerUpdateDto trainerDto) {
    log.debug("Facade request: update trainer with id={}", trainerId);
    Trainer trainer = trainerService.updateTrainer(trainerId, trainerDto);
    log.info(
        "Facade completed: updated trainer with id={} username={}",
        trainer.getId(),
        trainer.getUsername());
    return trainerMapper.toTrainerDto(trainer);
  }

  @Override
  public TrainerDto selectTrainer(CredentialsDto credentials, Long trainerId) {
    log.debug("Facade request: select trainer with id={}", trainerId);
    Trainer trainer = trainerService.getTrainer(trainerId);
    log.debug("Facade completed: selected trainer with id={}", trainerId);
    return trainerMapper.toTrainerDto(trainer);
  }

  @Override
  public TrainerDto selectTrainerByUsername(CredentialsDto credentials, String username) {
    log.debug("Facade request: select trainer by username={}", username);
    Trainer trainer = trainerService.getTrainerByUsername(username);
    log.debug("Facade completed: selected trainer by username={}", username);
    return trainerMapper.toTrainerDto(trainer);
  }

  @Override
  public List<TrainerDto> selectAllTrainers(CredentialsDto credentials) {
    log.debug("Facade request: select all trainers");
    List<Trainer> trainers = trainerService.getAllTrainers();
    log.debug("Facade completed: selected {} trainers", trainers.size());
    return trainerMapper.toTrainerDtoList(trainers);
  }

  @Override
  public List<TrainerDto> selectTrainersNotAssignedToTrainee(
      CredentialsDto credentials, String traineeUsername) {
    log.debug(
        "Facade request: select trainers not assigned to trainee username={}", traineeUsername);
    List<Trainer> trainers = trainerService.getTrainersNotAssignedToTrainee(traineeUsername);
    log.debug(
        "Facade completed: selected {} trainers not assigned to trainee username={}",
        trainers.size(),
        traineeUsername);
    return trainerMapper.toTrainerDtoList(trainers);
  }

  @Override
  public TrainingDto createTraining(CredentialsDto credentials, TrainingCreateDto trainingDto) {
    log.debug("Facade request: create training");
    Training training = trainingService.createTraining(trainingDto);
    log.info(
        "Facade completed: created training with id={} name={}",
        training.getId(),
        training.getName());
    return trainingMapper.getTrainingDto(training);
  }

  @Override
  public TrainingDto getTraining(CredentialsDto credentials, Long trainingId) {
    log.debug("Facade request: select training with id={}", trainingId);
    Training training = trainingService.getTraining(trainingId);
    log.debug("Facade completed: selected training with id={}", trainingId);
    return trainingMapper.getTrainingDto(training);
  }

  @Override
  public List<TrainingDto> getAllTrainings(CredentialsDto credentials) {
    log.debug("Facade request: select all trainings");
    List<Training> trainings = trainingService.getAllTrainings();
    log.debug("Facade completed: selected {} trainings", trainings.size());
    return trainings.stream().map(trainingMapper::getTrainingDto).toList();
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
    return trainings.stream().map(trainingMapper::getTrainingDto).toList();
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
    return trainings.stream().map(trainingMapper::getTrainingDto).toList();
  }
}
