package com.epam.jym.crm.facade;

import com.epam.jym.crm.dto.TraineeDto;
import com.epam.jym.crm.dto.TraineeUpdateDto;
import com.epam.jym.crm.dto.TrainerDto;
import com.epam.jym.crm.dto.TrainerUpdateDto;
import com.epam.jym.crm.dto.TrainingDto;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CrmFacadeImpl implements CrmFacade {

  private final TraineeService traineeService;
  private final TrainerService trainerService;
  private final TrainingService trainingService;

  public CrmFacadeImpl(
      TraineeService traineeService,
      TrainerService trainerService,
      TrainingService trainingService) {
    this.traineeService = traineeService;
    this.trainerService = trainerService;
    this.trainingService = trainingService;
  }

  @Override
  public TraineeDto createTrainee(TraineeDto traineeDto) {
    log.debug("Facade request: create trainee");
    return traineeService.createTrainee(traineeDto);
  }

  @Override
  public TraineeDto updateTrainee(Long traineeId, TraineeUpdateDto traineeDto) {
    log.debug("Facade request: update trainee with id={}", traineeId);
    return traineeService.updateTrainee(traineeId, traineeDto);
  }

  @Override
  public void deleteTrainee(Long traineeId) {
    log.debug("Facade request: delete trainee with id={}", traineeId);
    traineeService.deleteTrainee(traineeId);
  }

  @Override
  public Optional<TraineeDto> selectTrainee(Long traineeId) {
    log.debug("Facade request: select trainee with id={}", traineeId);
    return traineeService.selectTrainee(traineeId);
  }

  @Override
  public Optional<TraineeDto> selectTraineeByUsername(String username) {
    log.debug("Facade request: select trainee by username={}", username);
    return traineeService.selectTraineeByUsername(username);
  }

  @Override
  public List<TraineeDto> selectAllTrainees() {
    log.debug("Facade request: select all trainees");
    return traineeService.selectAllTrainees();
  }

  @Override
  public TrainerDto createTrainer(TrainerDto trainerDto) {
    log.debug("Facade request: create trainer");
    return trainerService.createTrainer(trainerDto);
  }

  @Override
  public TrainerDto updateTrainer(Long trainerId, TrainerUpdateDto trainerDto) {
    log.debug("Facade request: update trainer with id={}", trainerId);
    return trainerService.updateTrainer(trainerId, trainerDto);
  }

  @Override
  public Optional<TrainerDto> selectTrainer(Long trainerId) {
    log.debug("Facade request: select trainer with id={}", trainerId);
    return trainerService.selectTrainer(trainerId);
  }

  @Override
  public Optional<TrainerDto> selectTrainerByUsername(String username) {
    log.debug("Facade request: select trainer by username={}", username);
    return trainerService.selectTrainerByUsername(username);
  }

  @Override
  public List<TrainerDto> selectAllTrainers() {
    log.debug("Facade request: select all trainers");
    return trainerService.selectAllTrainers();
  }

  @Override
  public TrainingDto createTraining(TrainingDto trainingDto) {
    log.debug("Facade request: create training");
    return trainingService.createTraining(trainingDto);
  }

  @Override
  public Optional<TrainingDto> selectTraining(Long trainingId) {
    log.debug("Facade request: select training with id={}", trainingId);
    return trainingService.selectTraining(trainingId);
  }

  @Override
  public List<TrainingDto> selectAllTrainings() {
    log.debug("Facade request: select all trainings");
    return trainingService.selectAllTrainings();
  }
}

