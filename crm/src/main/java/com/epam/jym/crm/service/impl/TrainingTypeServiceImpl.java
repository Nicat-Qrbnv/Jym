package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.repository.TrainingTypeRepository;
import com.epam.jym.crm.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {
  private final TrainingTypeRepository trainingTypeRepository;

  @Override
  public TrainingType getTypeById(Long typeId) {
    return trainingTypeRepository
        .findById(typeId)
        .orElseThrow(
            () -> new IllegalArgumentException("Training type not found by id: " + typeId));
  }
}
