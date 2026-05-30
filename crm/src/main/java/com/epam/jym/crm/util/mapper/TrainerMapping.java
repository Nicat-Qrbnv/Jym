package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserDto;
import com.epam.jym.crm.dto.user.UserProfileDto;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.util.mapper.core.Mapper;
import com.epam.jym.crm.util.mapper.core.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrainerMapping {

  @Bean
  Mapper<Trainer, TrainerDto> toTrainerDto() {
    return new Mapper<>() {
      @Override
      public Class<Trainer> sourceType() {
        return Trainer.class;
      }

      @Override
      public Class<TrainerDto> targetType() {
        return TrainerDto.class;
      }

      @Override
      public TrainerDto map(Trainer source, MappingContext context) {
        return new TrainerDto(
            context.map(source.getUser(), UserProfileDto.class),
            context.map(source.getSpecialization(), TrainingTypeDto.class));
      }
    };
  }

  @Bean
  Mapper<Trainer, TrainerProfileDto> toTrainerProfileDto() {
    return new Mapper<>() {

      @Override
      public Class<Trainer> sourceType() {
        return Trainer.class;
      }

      @Override
      public Class<TrainerProfileDto> targetType() {
        return TrainerProfileDto.class;
      }

      @Override
      public TrainerProfileDto map(Trainer source, MappingContext context) {
        return new TrainerProfileDto(
            context.map(source.getUser(), UserDto.class),
            context.map(source.getSpecialization(), TrainingTypeDto.class),
            context.mapCollection(source.getTrainees(), UserProfileDto.class).toList());
      }
    };
  }
}
