package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.repository.TrainingRepository;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import com.epam.jym.crm.service.TrainingTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements TrainingService {

  private final TrainingRepository trainingRepo;
  private final TrainingTypeService trainingTypeService;
  private final TraineeService traineeService;
  private final TrainerService trainerService;

  @Setter(onMethod_ = @Autowired)
  private ModelMapper mapper;

  @Override
  public TrainingDto createTraining(TrainingCreateDto trainingDto) {
    if (trainingDto == null) {
      throw new IllegalArgumentException("trainingDto must not be null");
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

  private @NonNull Training getTrainingById(Long trainingId) {
    return trainingRepo
        .findById(trainingId)
        .orElseThrow(
            () -> {
              log.warn("Training not found by id: {}", trainingId);
              return new IllegalArgumentException("Training not found: " + trainingId);
            });
  }
}
