package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.trainee.TraineeSummaryDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerSummaryDto;
import com.epam.jym.crm.dto.trainer.UpdatedTrainerProfileDto;
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
  Mapper<Trainer, TrainerSummaryDto> toTrainerDto() {
    return new Mapper<>() {
      @Override
      public Class<Trainer> sourceType() {
        return Trainer.class;
      }

      @Override
      public Class<TrainerSummaryDto> targetType() {
        return TrainerSummaryDto.class;
      }

      @Override
      public TrainerSummaryDto map(Trainer source, MappingContext context) {
        return new TrainerSummaryDto(
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
            context.mapCollection(source.getTrainees(), TraineeSummaryDto.class).toList());
      }
    };
  }

  @Bean
  Mapper<Trainer, UpdatedTrainerProfileDto> toUpdatedTrainerProfileDto() {
    return new Mapper<>() {

      @Override
      public Class<Trainer> sourceType() {
        return Trainer.class;
      }

      @Override
      public Class<UpdatedTrainerProfileDto> targetType() {
        return UpdatedTrainerProfileDto.class;
      }

      @Override
      public UpdatedTrainerProfileDto map(Trainer source, MappingContext context) {
        return new UpdatedTrainerProfileDto(
            context.map(source.getUser(), UserProfileDto.class),
            context.map(source.getSpecialization(), TrainingTypeDto.class),
            context.mapCollection(source.getTrainees(), TraineeSummaryDto.class).toList());
      }
    };
  }
}
