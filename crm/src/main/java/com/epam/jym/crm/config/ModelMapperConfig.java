package com.epam.jym.crm.config;

import com.epam.jym.crm.dto.TraineeDto;
import com.epam.jym.crm.dto.TrainerDto;
import com.epam.jym.crm.dto.TrainingDto;
import com.epam.jym.crm.dto.TrainingTypeDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  private static Trainee toTrainee(TraineeDto source) {
    if (source == null) {
      return null;
    }
    return Trainee.builder()
        .id(source.id())
        .firstName(source.firstName())
        .lastName(source.lastName())
        .username(source.username())
        .password(source.password())
        .isActive(source.active())
        .dateOfBirth(source.dateOfBirth())
        .address(source.address())
        .build();
  }

  private static TraineeDto toTraineeDto(Trainee source) {
    if (source == null) {
      return null;
    }
    return new TraineeDto(
        source.getId(),
        source.getFirstName(),
        source.getLastName(),
        source.getUsername(),
        source.getPassword(),
        source.isActive(),
        source.getDateOfBirth(),
        source.getAddress());
  }

  private static Trainer toTrainer(TrainerDto source, boolean includeTraining) {
    if (source == null) {
      return null;
    }
    return Trainer.builder()
        .id(source.id())
        .firstName(source.firstName())
        .lastName(source.lastName())
        .username(source.username())
        .password(source.password())
        .isActive(source.active())
        .training(includeTraining ? toTraining(source.training(), false) : null)
        .specialization(toTrainingType(source.specialization()))
        .build();
  }

  private static TrainerDto toTrainerDto(Trainer source, boolean includeTraining) {
    if (source == null) {
      return null;
    }
    return new TrainerDto(
        source.getId(),
        source.getFirstName(),
        source.getLastName(),
        source.getUsername(),
        source.getPassword(),
        source.isActive(),
        includeTraining ? toTrainingDto(source.getTraining(), false) : null,
        toTrainingTypeDto(source.getSpecialization()));
  }

  private static Training toTraining(TrainingDto source, boolean includeTrainer) {
    if (source == null) {
      return null;
    }
    return Training.builder()
        .id(source.id())
        .name(source.name())
        .type(toTrainingType(source.type()))
        .trainee(toTrainee(source.trainee()))
        .trainer(includeTrainer ? toTrainer(source.trainer(), false) : null)
        .date(source.date())
        .durationInMinutes(source.durationInMinutes())
        .build();
  }

  private static TrainingDto toTrainingDto(Training source, boolean includeTrainer) {
    if (source == null) {
      return null;
    }
    return new TrainingDto(
        source.getId(),
        source.getName(),
        toTrainingTypeDto(source.getType()),
        toTraineeDto(source.getTrainee()),
        includeTrainer ? toTrainerDto(source.getTrainer(), false) : null,
        source.getDate(),
        source.getDurationInMinutes());
  }

  private static TrainingType toTrainingType(TrainingTypeDto source) {
    if (source == null) {
      return null;
    }
    return TrainingType.builder().id(source.id()).name(source.name()).build();
  }

  private static TrainingTypeDto toTrainingTypeDto(TrainingType source) {
    if (source == null) {
      return null;
    }
    return new TrainingTypeDto(source.getId(), source.getName());
  }

  @Bean
  ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.addConverter(
        context -> toTrainee(context.getSource()), TraineeDto.class, Trainee.class);
    modelMapper.addConverter(
        context -> toTraineeDto(context.getSource()), Trainee.class, TraineeDto.class);
    modelMapper.addConverter(
        context -> toTrainer(context.getSource(), true), TrainerDto.class, Trainer.class);
    modelMapper.addConverter(
        context -> toTrainerDto(context.getSource(), true), Trainer.class, TrainerDto.class);
    modelMapper.addConverter(
        context -> toTraining(context.getSource(), true), TrainingDto.class, Training.class);
    modelMapper.addConverter(
        context -> toTrainingDto(context.getSource(), true), Training.class, TrainingDto.class);
    modelMapper.addConverter(
        context -> toTrainingType(context.getSource()), TrainingTypeDto.class, TrainingType.class);
    modelMapper.addConverter(
        context -> toTrainingTypeDto(context.getSource()),
        TrainingType.class,
        TrainingTypeDto.class);
    return modelMapper;
  }
}
