package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.entity.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeMapper {

  public TrainingTypeDto toTrainingTypeDto(TrainingType source) {
    return new TrainingTypeDto(source.getId(), source.getName());
  }
}
