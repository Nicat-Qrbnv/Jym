package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.trainee.TraineeProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.user.UserDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.util.mapper.core.Mapper;
import com.epam.jym.crm.util.mapper.core.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraineeMapping {

  @Bean
  Mapper<Trainee, TraineeProfileDto> toTraineeProfileDto() {
    return new Mapper<>() {
      @Override
      public Class<Trainee> sourceType() {
        return Trainee.class;
      }

      @Override
      public Class<TraineeProfileDto> targetType() {
        return TraineeProfileDto.class;
      }

      @Override
      public TraineeProfileDto map(Trainee source, MappingContext context) {
        return new TraineeProfileDto(
            context.map(source.getUser(), UserDto.class),
            source.getDateOfBirth(),
            source.getAddress(),
            context.mapCollection(source.getTrainers(), TrainerDto.class).toList());
      }
    };
  }
}
