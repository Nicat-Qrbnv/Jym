package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.util.mapper.core.Mapper;
import com.epam.jym.crm.util.mapper.core.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrainingTypeMapping {

  @Bean
  Mapper<TrainingType, TrainingTypeDto> toTrainingTypeDto() {
    return new Mapper<>() {
      @Override
      public Class<TrainingType> sourceType() {
        return TrainingType.class;
      }

      @Override
      public Class<TrainingTypeDto> targetType() {
        return TrainingTypeDto.class;
      }

      @Override
      public TrainingTypeDto map(TrainingType source, MappingContext context) {
        return new TrainingTypeDto(source.getId(), source.getName());
      }
    };
  }
}
