package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.entity.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TrainingMapper {
  private final TrainingTypeMapper trainingTypeMapper;
  private final TraineeMapper traineeMapper;
  private final TrainerMapper trainerMapper;

  public TrainingDto getTrainingDto(Training source) {
    return new TrainingDto(
        source.getId(),
        source.getName(),
        trainingTypeMapper.toTrainingTypeDto(source.getType()),
        traineeMapper.toTraineeDto(source.getTrainee()),
        trainerMapper.toTrainerDto(source.getTrainer()),
        source.getScheduledDate(),
        source.getDurationInMinutes());
  }
}
