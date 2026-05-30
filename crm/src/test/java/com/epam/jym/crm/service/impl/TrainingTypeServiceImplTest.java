package com.epam.jym.crm.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.exception.ResourceNotFoundException;
import com.epam.jym.crm.repository.TrainingTypeRepository;
import com.epam.jym.crm.service.TrainingTypeService;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

  @Mock private TrainingTypeRepository trainingTypeRepository;

  @InjectMocks private TrainingTypeServiceImpl trainingTypeServiceImpl;

  @Test
  void getTypeShouldReturnTrainingTypeWhenExists() {
    TrainingTypeService trainingTypeService = trainingTypeServiceImpl;
    TrainingType trainingType = createTrainingType();

    when(trainingTypeRepository.findById(2L)).thenReturn(Optional.of(trainingType));

    TrainingType result = trainingTypeService.getType(2L);

    Assertions.assertThat(result).isSameAs(trainingType);
    verify(trainingTypeRepository).findById(2L);
  }

  @Test
  void getTypeShouldThrowExceptionWhenTrainingTypeDoesNotExist() {
    TrainingTypeService trainingTypeService = trainingTypeServiceImpl;

    when(trainingTypeRepository.findById(404L)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainingTypeService.getType(404L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Training type not found by id: 404");
  }

  @Test
  void getTypeIfValidShouldReturnTrainingTypeWhenIdAndNameMatch() {
    TrainingTypeDto typeDto = new TrainingTypeDto(2L, "Fitness");
    TrainingType trainingType = createTrainingType();

    when(trainingTypeRepository.findMatchingType(typeDto.id(), typeDto.name()))
        .thenReturn(Optional.of(trainingType));

    TrainingType result = trainingTypeServiceImpl.getTypeIfValid(typeDto);

    Assertions.assertThat(result).isSameAs(trainingType);
    verify(trainingTypeRepository).findMatchingType(2L, "Fitness");
  }

  @Test
  void getTypeIfValidShouldThrowExceptionWhenNoMatchingTypeExists() {
    TrainingTypeDto typeDto = new TrainingTypeDto(404L, "Unknown");

    when(trainingTypeRepository.findMatchingType(typeDto.id(), typeDto.name()))
        .thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainingTypeServiceImpl.getTypeIfValid(typeDto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Training type not found by id: 404 and name: Unknown");
  }

  @Test
  void getAllTypesShouldReturnTrainingTypes() {
    TrainingType firstType = createTrainingType();
    TrainingType secondType = createTrainingType();
    secondType.setId(3L);
    secondType.setName("Yoga");

    when(trainingTypeRepository.findAll()).thenReturn(List.of(firstType, secondType));

    List<TrainingType> result = trainingTypeServiceImpl.getAllTypes();

    Assertions.assertThat(result).containsExactly(firstType, secondType);
    verify(trainingTypeRepository).findAll();
  }

  private TrainingType createTrainingType() {
    TrainingType trainingType = new TrainingType();
    trainingType.setId(2L);
    trainingType.setName("Fitness");
    return trainingType;
  }
}
