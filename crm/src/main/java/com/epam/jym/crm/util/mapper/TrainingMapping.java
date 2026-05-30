package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.training.TraineeTrainingDto;
import com.epam.jym.crm.dto.training.TrainerTrainingDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.util.mapper.core.Mapper;
import com.epam.jym.crm.util.mapper.core.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrainingMapping {

  @Bean
  Mapper<Training, TrainingDto> toTrainingDto() {
    return new Mapper<>() {
      @Override
      public Class<Training> sourceType() {
        return Training.class;
      }

      @Override
      public Class<TrainingDto> targetType() {
        return TrainingDto.class;
      }

      public TrainingDto map(Training source, MappingContext context) {
        return new TrainingDto(
            source.getName(),
            source.getScheduledDate(),
            source.getType().getName(),
            source.getDurationInMinutes());
      }
    };
  }

  @Bean
  Mapper<Training, TraineeTrainingDto> toTraineeTrainingDto() {
    return new Mapper<>() {
      @Override
      public Class<Training> sourceType() {
        return Training.class;
      }

      @Override
      public Class<TraineeTrainingDto> targetType() {
        return TraineeTrainingDto.class;
      }

      public TraineeTrainingDto map(Training source, MappingContext context) {
        return new TraineeTrainingDto(
            source.getTrainee().getUser().getFullName(), context.map(source, TrainingDto.class));
      }
    };
  }

  @Bean
  Mapper<Training, TrainerTrainingDto> toTrainerTrainingDto() {
    return new Mapper<>() {
      @Override
      public Class<Training> sourceType() {
        return Training.class;
      }

      @Override
      public Class<TrainerTrainingDto> targetType() {
        return TrainerTrainingDto.class;
      }

      @Override
      public TrainerTrainingDto map(Training source, MappingContext context) {
        return new TrainerTrainingDto(
            source.getTrainer().getUser().getFullName(), context.map(source, TrainingDto.class));
      }
    };
  }
}
