package com.epam.jym.crm.config;

import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.RegisteredUserDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  @Bean
  ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.addConverter(getRegisteredUserDto(), User.class, RegisteredUserDto.class);
    mapper.addConverter(getTraineeDto(), Trainee.class, TraineeDto.class);
    mapper.addConverter(getTrainerDto(), Trainer.class, TrainerDto.class);
    mapper.addConverter(getTrainingDto(), Training.class, TrainingDto.class);
    return mapper;
  }

  private Converter<User, RegisteredUserDto> getRegisteredUserDto() {
    return ctx -> {
      User source = ctx.getSource();
      if (source == null) {
        return null;
      }
      return new RegisteredUserDto(
          source.getId(),
          source.getFirstName(),
          source.getLastName(),
          source.getUsername(),
          source.getPassword());
    };
  }

  private Converter<Trainee, TraineeDto> getTraineeDto() {
    return ctx -> toTraineeDto(ctx.getSource());
  }

  private Converter<Trainer, TrainerDto> getTrainerDto() {
    return ctx -> toTrainerDto(ctx.getSource());
  }

  private Converter<Training, TrainingDto> getTrainingDto() {
    return ctx -> {
      Training source = ctx.getSource();
      if (source == null) {
        return null;
      }
      return new TrainingDto(
          source.getId(),
          source.getName(),
          toTrainingTypeDto(source.getType()),
          toTraineeDto(source.getTrainee()),
          toTrainerDto(source.getTrainer()),
          source.getScheduledDate(),
          source.getDurationInMinutes());
    };
  }

  private TraineeDto toTraineeDto(Trainee source) {
    if (source == null) {
      return null;
    }

    User user = source.getUser();
    return new TraineeDto(
        source.getId(),
        user == null ? null : user.getId(),
        user == null ? null : user.getUsername(),
        source.getDateOfBirth(),
        source.getAddress());
  }

  private TrainerDto toTrainerDto(Trainer source) {
    if (source == null) {
      return null;
    }

    User user = source.getUser();
    TrainingType specialization = source.getSpecialization();
    return new TrainerDto(
        source.getId(),
        user == null ? null : user.getId(),
        user == null ? null : user.getUsername(),
        specialization == null
            ? null
            : new TrainingTypeDto(specialization.getId(), specialization.getName()));
  }

  private TrainingTypeDto toTrainingTypeDto(TrainingType source) {
    if (source == null) {
      return null;
    }
    return new TrainingTypeDto(source.getId(), source.getName());
  }
}
