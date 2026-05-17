package com.epam.jym.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.TrainingRepository;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.TrainingService;
import com.epam.jym.crm.service.TrainingTypeService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

  @Mock private TrainingRepository trainingRepository;

  @Mock private TrainingTypeService trainingTypeService;

  @Mock private TraineeService traineeService;

  @Mock private TrainerService trainerService;

  @Mock private ModelMapper modelMapper;

  @InjectMocks private TrainingServiceImpl trainingServiceImpl;

  private TrainingService trainingService;

  @BeforeEach
  public void setUp() {
    trainingServiceImpl.setMapper(modelMapper);
    trainingService = trainingServiceImpl;
  }

  @Test
  void createTrainingShouldLoadRelationsSaveTrainingAndReturnDto() {
    TrainingCreateDto trainingDto = createTrainingCreateDto();
    TrainingType type = createTrainingType(2L, "Fitness");
    Trainee trainee = createTrainee(3L, createUser(1L, "john.doe"));
    Trainer trainer = createTrainer(4L, createUser(2L, "jane.doe"), type);
    Training savedTraining = createTraining(10L, "Java Basics", type, trainee, trainer);
    TrainingDto savedTrainingDto = createTrainingDto(10L, "Java Basics");

    when(trainingTypeService.getType(2L)).thenReturn(type);
    when(traineeService.getTrainee(3L)).thenReturn(trainee);
    when(trainerService.getTrainer(4L)).thenReturn(trainer);
    when(trainingRepository.save(any(Training.class))).thenReturn(savedTraining);
    when(modelMapper.map(savedTraining, TrainingDto.class)).thenReturn(savedTrainingDto);

    TrainingDto result = trainingService.createTraining(trainingDto);

    Assertions.assertThat(result).isSameAs(savedTrainingDto);
    ArgumentCaptor<Training> trainingCaptor = ArgumentCaptor.forClass(Training.class);
    verify(trainingRepository).save(trainingCaptor.capture());
    Assertions.assertThat(trainingCaptor.getValue().getName()).isEqualTo(trainingDto.name());
    Assertions.assertThat(trainingCaptor.getValue().getType()).isSameAs(type);
    Assertions.assertThat(trainingCaptor.getValue().getTrainee()).isSameAs(trainee);
    Assertions.assertThat(trainingCaptor.getValue().getTrainer()).isSameAs(trainer);
    Assertions.assertThat(trainingCaptor.getValue().getScheduledDate())
        .isEqualTo(trainingDto.date());
    Assertions.assertThat(trainingCaptor.getValue().getDurationInMinutes())
        .isEqualTo(trainingDto.durationInMinutes());
  }

  @Test
  void createTrainingShouldThrowExceptionWhenTrainingTypeDoesNotExist() {
    TrainingCreateDto trainingDto = createTrainingCreateDto();

    when(trainingTypeService.getType(2L))
        .thenThrow(new IllegalArgumentException("Training type not found: 2"));

    Assertions.assertThatThrownBy(() -> trainingService.createTraining(trainingDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Training type not found: 2");

    verifyNoInteractions(traineeService, trainerService, modelMapper);
  }

  @Test
  void createTrainingShouldThrowExceptionWhenTraineeDoesNotExist() {
    TrainingCreateDto trainingDto = createTrainingCreateDto();

    when(trainingTypeService.getType(2L)).thenReturn(createTrainingType(2L, "Fitness"));
    when(traineeService.getTrainee(3L))
        .thenThrow(new IllegalArgumentException("Trainee not found: 3"));

    Assertions.assertThatThrownBy(() -> trainingService.createTraining(trainingDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: 3");

    verifyNoInteractions(trainerService, modelMapper);
  }

  @Test
  void createTrainingShouldThrowExceptionWhenTrainerDoesNotExist() {
    TrainingCreateDto trainingDto = createTrainingCreateDto();

    when(trainingTypeService.getType(2L)).thenReturn(createTrainingType(2L, "Fitness"));
    when(traineeService.getTrainee(3L)).thenReturn(createTrainee(3L, createUser(1L, "john.doe")));
    when(trainerService.getTrainer(4L))
        .thenThrow(new IllegalArgumentException("Trainer not found: 4"));

    Assertions.assertThatThrownBy(() -> trainingService.createTraining(trainingDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainer not found: 4");

    verifyNoInteractions(modelMapper);
  }

  @Test
  void selectTrainingShouldReturnMappedDtoWhenTrainingExists() {
    Long trainingId = 10L;
    Training training =
        createTraining(
            trainingId,
            "Java Basics",
            createTrainingType(2L, "Fitness"),
            createTrainee(3L, createUser(1L, "john.doe")),
            createTrainer(4L, createUser(2L, "jane.doe"), createTrainingType(2L, "Fitness")));
    TrainingDto trainingDto = createTrainingDto(trainingId, "Java Basics");

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
    when(modelMapper.map(training, TrainingDto.class)).thenReturn(trainingDto);

    TrainingDto result = trainingService.selectTraining(trainingId);

    Assertions.assertThat(result).isSameAs(trainingDto);
  }

  @Test
  void selectTrainingShouldThrowExceptionWhenTrainingDoesNotExist() {
    Long trainingId = 404L;

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainingService.selectTraining(trainingId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Training not found: " + trainingId);
    verifyNoInteractions(modelMapper);
  }

  @Test
  void selectAllTrainingsShouldReturnMappedDtos() {
    TrainingType type = createTrainingType(2L, "Fitness");
    Trainee trainee = createTrainee(3L, createUser(1L, "john.doe"));
    Trainer trainer = createTrainer(4L, createUser(2L, "jane.doe"), type);
    Training firstTraining = createTraining(1L, "Java Basics", type, trainee, trainer);
    Training secondTraining = createTraining(2L, "Spring Basics", type, trainee, trainer);
    TrainingDto firstDto = createTrainingDto(1L, "Java Basics");
    TrainingDto secondDto = createTrainingDto(2L, "Spring Basics");

    when(trainingRepository.findAll()).thenReturn(List.of(firstTraining, secondTraining));
    when(modelMapper.map(firstTraining, TrainingDto.class)).thenReturn(firstDto);
    when(modelMapper.map(secondTraining, TrainingDto.class)).thenReturn(secondDto);

    List<TrainingDto> result = trainingService.selectAllTrainings();

    Assertions.assertThat(result).containsExactly(firstDto, secondDto);
  }

  private Training createTraining(
      Long id, String name, TrainingType type, Trainee trainee, Trainer trainer) {
    Training training = new Training();
    training.setId(id);
    training.setName(name);
    training.setType(type);
    training.setTrainee(trainee);
    training.setTrainer(trainer);
    training.setScheduledDate(LocalDate.of(2026, 5, 8));
    training.setDurationInMinutes(60);
    return training;
  }

  private TrainingType createTrainingType(Long id, String name) {
    TrainingType trainingType = new TrainingType();
    trainingType.setId(id);
    trainingType.setName(name);
    return trainingType;
  }

  private Trainee createTrainee(Long id, User user) {
    Trainee trainee = new Trainee();
    trainee.setId(id);
    trainee.setUser(user);
    trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
    trainee.setAddress("Baku");
    return trainee;
  }

  private Trainer createTrainer(Long id, User user, TrainingType specialization) {
    Trainer trainer = new Trainer();
    trainer.setId(id);
    trainer.setUser(user);
    trainer.setSpecialization(specialization);
    return trainer;
  }

  private User createUser(Long id, String username) {
    User user = new User();
    user.setId(id);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setUsername(username);
    user.setPassword("password");
    user.setActive(true);
    return user;
  }

  private TrainingCreateDto createTrainingCreateDto() {
    return new TrainingCreateDto("Java Basics", 2L, 3L, 4L, LocalDate.of(2026, 5, 8), 60);
  }

  private TrainingDto createTrainingDto(Long id, String name) {
    return new TrainingDto(id, name, null, null, null, LocalDate.of(2026, 5, 8), 60);
  }
}
