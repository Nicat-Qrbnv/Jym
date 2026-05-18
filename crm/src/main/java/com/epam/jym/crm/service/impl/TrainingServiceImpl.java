package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.repository.TrainingRepository;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import com.epam.jym.crm.service.TrainingTypeService;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TrainingServiceImpl implements TrainingService {

  private final TrainingRepository trainingRepo;
  private final TrainingTypeService trainingTypeService;
  private final TraineeService traineeService;
  private final TrainerService trainerService;

  @Setter(onMethod_ = @Autowired)
  private ModelMapper mapper;

  @Transactional
  @Override
  public TrainingDto createTraining(TrainingCreateDto trainingDto) {
    if (trainingDto == null) {
      throw new IllegalArgumentException("trainingDto must not be null");
    }
    if (Objects.equals(trainingDto.traineeId(), trainingDto.trainerId())) {
      throw new IllegalArgumentException("Trainee and trainer cannot be the same person");
    }

    log.debug("Creating training");
    Training training = new Training();
    training.setName(trainingDto.name());
    training.setType(trainingTypeService.getType(trainingDto.typeId()));
    training.setTrainee(traineeService.getTrainee(trainingDto.traineeId()));
    training.setTrainer(trainerService.getTrainer(trainingDto.trainerId()));
    training.setScheduledDate(trainingDto.date());
    training.setDurationInMinutes(trainingDto.durationInMinutes());

    training = trainingRepo.save(training);
    log.info("Created training with id={} name={}", training.getId(), training.getName());

    return mapper.map(training, TrainingDto.class);
  }

  @Override
  public TrainingDto selectTraining(Long trainingId) {
    log.debug("Selecting training by id={}", trainingId);
    return mapper.map(getTrainingById(trainingId), TrainingDto.class);
  }

  @Override
  public List<TrainingDto> selectAllTrainings() {
    List<TrainingDto> trainings =
        trainingRepo.findAll().stream()
            .map(training -> mapper.map(training, TrainingDto.class))
            .toList();
    log.debug("Selected {} trainings", trainings.size());

    return trainings;
  }

  @Override
  public List<TrainingDto> getTraineeTrainings(
      String traineeUsername, TraineeTrainingsCriteriaDto criteria) {
    if (traineeUsername == null || traineeUsername.isBlank()) {
      throw new IllegalArgumentException("traineeUsername must not be blank");
    }
    List<Training> traineeTrainings;
    if (criteria == null) {
      traineeTrainings = trainingRepo.findTraineeTrainings(traineeUsername, null, null, null, null);
    } else {
      LocalDate fromDate = criteria.fromDate();
      LocalDate toDate = criteria.toDate();
      String trainerName = normalize(criteria.trainerName());
      String trainingType = normalize(criteria.trainingType());

      traineeTrainings =
          trainingRepo.findTraineeTrainings(
              traineeUsername, fromDate, toDate, trainerName, trainingType);
    }
    log.debug("Selected {} trainings for trainee = {}", traineeTrainings.size(), traineeUsername);

    return traineeTrainings.stream()
        .map(training -> mapper.map(training, TrainingDto.class))
        .toList();
  }

  @Override
  public List<TrainingDto> getTrainerTrainings(
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
    log.debug("Selected {} trainings for trainer = {}", trainerTrainings.size(), trainerUsername);

    return trainerTrainings.stream()
        .map(training -> mapper.map(training, TrainingDto.class))
        .toList();
  }

  private @NonNull Training getTrainingById(Long trainingId) {
    return trainingRepo
        .findById(trainingId)
        .orElseThrow(
            () -> {
              log.warn("Training not found by id: {}", trainingId);
              return new IllegalArgumentException("Training not found: " + trainingId);
            });
  }

  private String normalize(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
