package com.epam.jym.crm.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.TrainingDto;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.repository.TrainingRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

  @Mock
  private TrainingRepository trainingRepository;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private TrainingServiceImpl trainingService;

  @Test
  void createTrainingShouldSaveMappedEntityAndReturnDto() {
    TrainingDto trainingDto = createTrainingDto(1L, "Java Basics");
    Training trainingEntity = createTraining(null, "Java Basics");
    Training savedTraining = createTraining(10L, "Java Basics");
    TrainingDto savedTrainingDto = createTrainingDto(10L, "Java Basics");

    when(modelMapper.map(trainingDto, Training.class)).thenReturn(trainingEntity);
    when(trainingRepository.save(trainingEntity)).thenReturn(savedTraining);
    when(modelMapper.map(savedTraining, TrainingDto.class)).thenReturn(savedTrainingDto);

    TrainingDto result = trainingService.createTraining(trainingDto);

    Assertions.assertThat(result).isSameAs(savedTrainingDto);
    verify(trainingRepository).save(trainingEntity);
  }

  @Test
  void selectTrainingShouldReturnMappedDtoWhenTrainingExists() {
    Long trainingId = 10L;
    Training training = createTraining(trainingId, "Java Basics");
    TrainingDto trainingDto = createTrainingDto(trainingId, "Java Basics");

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
    when(modelMapper.map(training, TrainingDto.class)).thenReturn(trainingDto);

    Optional<TrainingDto> result = trainingService.selectTraining(trainingId);

    Assertions.assertThat(result).contains(trainingDto);
  }

  @Test
  void selectTrainingShouldReturnEmptyWhenTrainingDoesNotExist() {
    Long trainingId = 404L;

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

    Optional<TrainingDto> result = trainingService.selectTraining(trainingId);

    Assertions.assertThat(result).isEmpty();
    verifyNoInteractions(modelMapper);
  }

  @Test
  void selectAllTrainingsShouldReturnMappedDtos() {
    Training firstTraining = createTraining(1L, "Java Basics");
    Training secondTraining = createTraining(2L, "Spring Basics");
    TrainingDto firstDto = createTrainingDto(1L, "Java Basics");
    TrainingDto secondDto = createTrainingDto(2L, "Spring Basics");

    when(trainingRepository.findAll()).thenReturn(List.of(firstTraining, secondTraining));
    when(modelMapper.map(firstTraining, TrainingDto.class)).thenReturn(firstDto);
    when(modelMapper.map(secondTraining, TrainingDto.class)).thenReturn(secondDto);

    List<TrainingDto> result = trainingService.selectAllTrainings();

    Assertions.assertThat(result).containsExactly(firstDto, secondDto);
  }

  private Training createTraining(Long id, String name) {
    Training training = new Training();
    training.setId(id);
    training.setName(name);
    training.setDate(LocalDate.of(2026, 5, 8));
    training.setDurationInMinutes(60);
    return training;
  }

  private TrainingDto createTrainingDto(Long id, String name) {
    return new TrainingDto(id, name, null, null, null, LocalDate.of(2026, 5, 8), 60);
  }
}

