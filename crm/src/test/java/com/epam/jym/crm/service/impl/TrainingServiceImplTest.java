package com.epam.jym.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

  @Mock private TrainingRepository trainingRepository;

  @Mock private TrainingTypeService trainingTypeService;

  @Mock private TraineeService traineeService;

  @Mock private TrainerService trainerService;

  @InjectMocks private TrainingServiceImpl trainingServiceImpl;

  private TrainingService trainingService;

  @org.junit.jupiter.api.BeforeEach
  void setUp() {
    trainingService = trainingServiceImpl;
  }

  @Test
  void createTrainingShouldLoadRelationsSaveTrainingAndReturnEntity() {
    TrainingCreateDto trainingDto = createTrainingCreateDto();
    TrainingType type = createTrainingType();
    Trainee trainee = createTrainee(createUser(1L, "john.doe"));
    Trainer trainer = createTrainer(createUser(2L, "jane.doe"), type);
    Training savedTraining = createTraining(10L, "Java Basics", type, trainee, trainer);

    when(trainingTypeService.getType(2L)).thenReturn(type);
    when(traineeService.getTrainee(3L)).thenReturn(trainee);
    when(trainerService.getTrainer(4L)).thenReturn(trainer);
    when(trainingRepository.save(any(Training.class))).thenReturn(savedTraining);

    Training result = trainingService.createTraining(trainingDto);

    Assertions.assertThat(result).isSameAs(savedTraining);
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

    verifyNoInteractions(traineeService, trainerService);
  }

  @Test
  void createTrainingShouldThrowExceptionWhenTraineeDoesNotExist() {
    TrainingCreateDto trainingDto = createTrainingCreateDto();

    when(trainingTypeService.getType(2L)).thenReturn(createTrainingType());
    when(traineeService.getTrainee(3L))
        .thenThrow(new IllegalArgumentException("Trainee not found: 3"));

    Assertions.assertThatThrownBy(() -> trainingService.createTraining(trainingDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: 3");

    verifyNoInteractions(trainerService);
  }

  @Test
  void createTrainingShouldThrowExceptionWhenTrainerDoesNotExist() {
    TrainingCreateDto trainingDto = createTrainingCreateDto();

    when(trainingTypeService.getType(2L)).thenReturn(createTrainingType());
    when(traineeService.getTrainee(3L)).thenReturn(createTrainee(createUser(1L, "john.doe")));
    when(trainerService.getTrainer(4L))
        .thenThrow(new IllegalArgumentException("Trainer not found: 4"));

    Assertions.assertThatThrownBy(() -> trainingService.createTraining(trainingDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainer not found: 4");
  }

  @Test
  void getTrainingShouldReturnEntityWhenTrainingExists() {
    Long trainingId = 10L;
    Training training =
        createTraining(
            trainingId,
            "Java Basics",
            createTrainingType(),
            createTrainee(createUser(1L, "john.doe")),
            createTrainer(createUser(2L, "jane.doe"), createTrainingType()));

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));

    Training result = trainingService.getTraining(trainingId);

    Assertions.assertThat(result).isSameAs(training);
  }

  @Test
  void getTrainingShouldThrowExceptionWhenTrainingDoesNotExist() {
    Long trainingId = 404L;

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainingService.getTraining(trainingId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Training not found: " + trainingId);
  }

  @Test
  void getAllTrainingsShouldReturnEntities() {
    TrainingType type = createTrainingType();
    Trainee trainee = createTrainee(createUser(1L, "john.doe"));
    Trainer trainer = createTrainer(createUser(2L, "jane.doe"), type);
    Training firstTraining = createTraining(1L, "Java Basics", type, trainee, trainer);
    Training secondTraining = createTraining(2L, "Spring Basics", type, trainee, trainer);

    when(trainingRepository.findAll()).thenReturn(List.of(firstTraining, secondTraining));

    List<Training> result = trainingService.getAllTrainings();

    Assertions.assertThat(result).containsExactly(firstTraining, secondTraining);
  }

  @Test
  void getTraineeTrainingsShouldReturnEntitiesMatchingCriteria() {
    LocalDate fromDate = LocalDate.of(2026, 5, 1);
    LocalDate toDate = LocalDate.of(2026, 5, 31);
    TraineeTrainingsCriteriaDto criteria =
        new TraineeTrainingsCriteriaDto(fromDate, toDate, " Jane Doe ", "Fitness");
    TrainingType type = createTrainingType();
    Trainee trainee = createTrainee(createUser(1L, "john.doe"));
    Trainer trainer = createTrainer(createUser(2L, "jane.doe"), type);
    Training training = createTraining(1L, "Java Basics", type, trainee, trainer);

    when(trainingRepository.findTraineeTrainings(
            "john.doe", fromDate, toDate, "Jane Doe", "Fitness"))
        .thenReturn(List.of(training));

    List<Training> result = trainingService.getTraineeTrainings("john.doe", criteria);

    Assertions.assertThat(result).containsExactly(training);
  }

  @Test
  void getTraineeTrainingsShouldTreatNullCriteriaAsNoOptionalFilters() {
    TrainingType type = createTrainingType();
    Trainee trainee = createTrainee(createUser(1L, "john.doe"));
    Trainer trainer = createTrainer(createUser(2L, "jane.doe"), type);
    Training training = createTraining(1L, "Java Basics", type, trainee, trainer);

    when(trainingRepository.findTraineeTrainings("john.doe", null, null, null, null))
        .thenReturn(List.of(training));

    List<Training> result = trainingService.getTraineeTrainings("john.doe", null);

    Assertions.assertThat(result).containsExactly(training);
  }

  @Test
  void getTraineeTrainingsShouldThrowExceptionWhenTraineeUsernameIsBlank() {
    Assertions.assertThatThrownBy(
            () ->
                trainingService.getTraineeTrainings(
                    " ", new TraineeTrainingsCriteriaDto(null, null, null, null)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("traineeUsername must not be blank");

    verifyNoInteractions(trainingRepository);
  }

  @Test
  void getTrainerTrainingsShouldReturnEntitiesMatchingCriteria() {
    LocalDate fromDate = LocalDate.of(2026, 5, 1);
    LocalDate toDate = LocalDate.of(2026, 5, 31);
    TrainerTrainingsCriteriaDto criteria =
        new TrainerTrainingsCriteriaDto(fromDate, toDate, " John Doe ");
    TrainingType type = createTrainingType();
    Trainee trainee = createTrainee(createUser(1L, "john.doe"));
    Trainer trainer = createTrainer(createUser(2L, "jane.doe"), type);
    Training training = createTraining(1L, "Java Basics", type, trainee, trainer);

    when(trainingRepository.findTrainerTrainings("jane.doe", fromDate, toDate, "John Doe"))
        .thenReturn(List.of(training));

    List<Training> result = trainingService.getTrainerTrainings("jane.doe", criteria);

    Assertions.assertThat(result).containsExactly(training);
  }

  @Test
  void getTrainerTrainingsShouldTreatNullCriteriaAsNoOptionalFilters() {
    TrainingType type = createTrainingType();
    Trainee trainee = createTrainee(createUser(1L, "john.doe"));
    Trainer trainer = createTrainer(createUser(2L, "jane.doe"), type);
    Training training = createTraining(1L, "Java Basics", type, trainee, trainer);

    when(trainingRepository.findTrainerTrainings("jane.doe", null, null, null))
        .thenReturn(List.of(training));

    List<Training> result = trainingService.getTrainerTrainings("jane.doe", null);

    Assertions.assertThat(result).containsExactly(training);
  }

  @Test
  void getTrainerTrainingsShouldThrowExceptionWhenTrainerUsernameIsBlank() {
    Assertions.assertThatThrownBy(
            () ->
                trainingService.getTrainerTrainings(
                    " ", new TrainerTrainingsCriteriaDto(null, null, null)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("trainerUsername must not be blank");

    verifyNoInteractions(trainingRepository);
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

  private TrainingType createTrainingType() {
    TrainingType trainingType = new TrainingType();
    trainingType.setId(2L);
    trainingType.setName("Fitness");
    return trainingType;
  }

  private Trainee createTrainee(User user) {
    Trainee trainee = new Trainee();
    trainee.setId(3L);
    trainee.setUser(user);
    trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
    trainee.setAddress("Baku");
    return trainee;
  }

  private Trainer createTrainer(User user, TrainingType specialization) {
    Trainer trainer = new Trainer();
    trainer.setId(4L);
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

}
