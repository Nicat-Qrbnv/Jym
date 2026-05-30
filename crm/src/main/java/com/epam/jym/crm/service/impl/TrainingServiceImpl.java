package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.repository.TrainingRepository;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TrainingServiceImpl implements TrainingService {

  private final TrainingRepository trainingRepo;
  private final TraineeService traineeService;
  private final TrainerService trainerService;

  @Transactional
  @Override
  public Training createTraining(TrainingCreateDto trainingDto) {
    if (trainingDto == null) {
      throw new IllegalArgumentException("trainingDto must not be null");
    }
    if (Objects.equals(trainingDto.traineeUsername(), trainingDto.trainerUsername())) {
      throw new IllegalArgumentException("Trainee and trainer cannot be the same person");
    }

    Training training = new Training();
    training.setName(trainingDto.name());
    training.setTrainee(traineeService.getTraineeByUsername(trainingDto.traineeUsername()));
    training.setTrainer(trainerService.getTrainerByUsername(trainingDto.trainerUsername()));
    training.setScheduledDate(trainingDto.date());
    training.setDurationInMinutes(trainingDto.durationInMinutes());
    training.setType(training.getTrainer().getSpecialization());

    return trainingRepo.save(training);
  }

  @Override
  public List<Training> getTraineeTrainings(
      String traineeUsername, TraineeTrainingsCriteriaDto criteria) {
    if (traineeUsername == null || traineeUsername.isBlank()) {
      throw new IllegalArgumentException("traineeUsername must not be blank");
    }

    LocalDate fromDate = null;
    LocalDate toDate = null;
    String trainerName = null;
    String trainingType = null;
    if (criteria != null) {
      fromDate = criteria.fromDate();
      toDate = criteria.toDate();
      trainerName = normalize(criteria.trainerName());
      trainingType = normalize(criteria.trainingType());
    }
    List<Long> trainers = trainerService.searchTrainersByName(trainerName);

    return trainingRepo.findTraineeTrainings(
        traineeUsername, fromDate, toDate, !trainers.isEmpty(), trainers, trainingType);
  }

  @Override
  public List<Training> getTrainerTrainings(
      String trainerUsername, TrainerTrainingsCriteriaDto criteria) {
    if (trainerUsername == null || trainerUsername.isBlank()) {
      throw new IllegalArgumentException("trainerUsername must not be blank");
    }
    List<Training> trainerTrainings;
    if (criteria == null) {
      trainerTrainings = trainingRepo.findTrainerTrainings(trainerUsername, null, null, null);
    } else {
      LocalDate fromDate = criteria.fromDate();
      LocalDate toDate = criteria.toDate();
      String traineeName = normalize(criteria.traineeName());

      trainerTrainings =
          trainingRepo.findTrainerTrainings(trainerUsername, fromDate, toDate, traineeName);
    }
    return trainerTrainings;
  }

  private String normalize(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
