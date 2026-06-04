package com.epam.jym.crm.service.impl;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.exception.ResourceNotFoundException;
import com.epam.jym.crm.repository.TrainingTypeRepository;
import com.epam.jym.crm.service.TrainingTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class TrainingTypeServiceImpl implements TrainingTypeService {
  private final TrainingTypeRepository trainingTypeRepository;

  @Override
  public TrainingType getType(Long typeId) {
    return trainingTypeRepository
        .findById(typeId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Training type not found by id: " + typeId));
  }

  @Override
  public TrainingType getTypeIfValid(TrainingTypeDto typeDto) {
    return trainingTypeRepository
        .findMatchingType(typeDto.id(), typeDto.name())
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Training type not found by id: "
                        + typeDto.id()
                        + " and name: "
                        + typeDto.name()));
  }

  @Override
  public List<TrainingType> getAllTypes() {
    return trainingTypeRepository.findAll();
  }
}
