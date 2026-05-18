package com.epam.jym.crm.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.repository.TrainingTypeRepository;
import com.epam.jym.crm.service.TrainingTypeService;
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
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Training type not found by id: 404");
  }

  private TrainingType createTrainingType() {
    TrainingType trainingType = new TrainingType();
    trainingType.setId(2L);
    trainingType.setName("Fitness");
    return trainingType;
  }
}
