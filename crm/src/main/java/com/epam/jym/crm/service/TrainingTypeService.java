package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.entity.TrainingType;
import java.util.List;

public interface TrainingTypeService {
  TrainingType getType(Long typeId);

  TrainingType getTypeIfValid(TrainingTypeDto typeDto);

  List<TrainingType> getAllTypes();
}
