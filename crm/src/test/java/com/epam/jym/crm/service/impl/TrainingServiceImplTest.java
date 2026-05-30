package com.epam.jym.crm.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

  @Mock private TrainingRepository trainingRepository;

  @Mock private TraineeService traineeService;

  @Mock private TrainerService trainerService;

  @InjectMocks private TrainingServiceImpl trainingService;

  @Test
  void createTrainingShouldLoadRelationsByUsernameDeriveTypeFromTrainerAndSave() {
    TrainingCreateDto trainingDto = createTrainingCreateDto();
    TrainingType specialization = createTrainingType();
    Trainee trainee = createTrainee(createUser(1L, "john.doe"));
    Trainer trainer = createTrainer(createUser(2L, "jane.doe"), specialization);
    Training savedTraining = createTraining(10L, specialization, trainee, trainer);

    when(traineeService.getTraineeByUsername("john.doe")).thenReturn(trainee);
    when(trainerService.getTrainerByUsername("jane.doe")).thenReturn(trainer);
    when(trainingRepository.save(any(Training.class))).thenReturn(savedTraining);

    Training result = trainingService.createTraining(trainingDto);

    assertThat(result).isSameAs(savedTraining);
    verify(traineeService).getTraineeByUsername("john.doe");
    verify(trainerService).getTrainerByUsername("jane.doe");

    ArgumentCaptor<Training> trainingCaptor = ArgumentCaptor.forClass(Training.class);
    verify(trainingRepository).save(trainingCaptor.capture());
    Training trainingToSave = trainingCaptor.getValue();
    assertThat(trainingToSave.getName()).isEqualTo(trainingDto.name());
    assertThat(trainingToSave.getType()).isSameAs(specialization);
    assertThat(trainingToSave.getTrainee()).isSameAs(trainee);
    assertThat(trainingToSave.getTrainer()).isSameAs(trainer);
    assertThat(trainingToSave.getScheduledDate()).isEqualTo(trainingDto.date());
    assertThat(trainingToSave.getDurationInMinutes()).isEqualTo(trainingDto.durationInMinutes());
  }

  @Test
  void createTrainingShouldThrowExceptionWhenDtoIsNull() {
    assertThatThrownBy(() -> trainingService.createTraining(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("trainingDto must not be null");

    verifyNoInteractions(traineeService, trainerService, trainingRepository);
  }

  @Test
  void createTrainingShouldThrowExceptionWhenTraineeAndTrainerAreSameUser() {
    TrainingCreateDto trainingDto =
        new TrainingCreateDto(
            "Java Basics", "john.doe", "john.doe", LocalDate.of(2026, 5, 8), 60);

    assertThatThrownBy(() -> trainingService.createTraining(trainingDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee and trainer cannot be the same person");

    verifyNoInteractions(traineeService, trainerService, trainingRepository);
  }

  @Test
  void createTrainingShouldPropagateExceptionWhenTraineeDoesNotExist() {
    TrainingCreateDto trainingDto = createTrainingCreateDto();

    when(traineeService.getTraineeByUsername("john.doe"))
        .thenThrow(new IllegalArgumentException("Trainee not found: john.doe"));

    assertThatThrownBy(() -> trainingService.createTraining(trainingDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: john.doe");

    verifyNoInteractions(trainerService, trainingRepository);
  }

  @Test
  void createTrainingShouldPropagateExceptionWhenTrainerDoesNotExist() {
    TrainingCreateDto trainingDto = createTrainingCreateDto();
    Trainee trainee = createTrainee(createUser(1L, "john.doe"));

    when(traineeService.getTraineeByUsername("john.doe")).thenReturn(trainee);
    when(trainerService.getTrainerByUsername("jane.doe"))
        .thenThrow(new IllegalArgumentException("Trainer not found: jane.doe"));

    assertThatThrownBy(() -> trainingService.createTraining(trainingDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainer not found: jane.doe");

    verifyNoInteractions(trainingRepository);
  }

  @Test
  void getTraineeTrainingsShouldReturnEntitiesMatchingCriteria() {
    LocalDate fromDate = LocalDate.of(2026, 5, 1);
    LocalDate toDate = LocalDate.of(2026, 5, 31);
    TraineeTrainingsCriteriaDto criteria =
        new TraineeTrainingsCriteriaDto(fromDate, toDate, " Jane Doe ", " Fitness ");
    List<Long> trainerIds = List.of(4L, 7L);
    Training training = createTraining();

    when(trainerService.searchTrainersByName("Jane Doe")).thenReturn(trainerIds);
    when(trainingRepository.findTraineeTrainings(
            "john.doe", fromDate, toDate, true, trainerIds, "Fitness"))
        .thenReturn(List.of(training));

    List<Training> result = trainingService.getTraineeTrainings("john.doe", criteria);

    assertThat(result).containsExactly(training);
  }

  @Test
  void getTraineeTrainingsShouldTreatBlankOptionalCriteriaAsNoOptionalFilters() {
    TraineeTrainingsCriteriaDto criteria =
        new TraineeTrainingsCriteriaDto(null, null, " ", "\t");
    Training training = createTraining();

    when(trainerService.searchTrainersByName(null)).thenReturn(List.of());
    when(trainingRepository.findTraineeTrainings("john.doe", null, null, false, List.of(), null))
        .thenReturn(List.of(training));

    List<Training> result = trainingService.getTraineeTrainings("john.doe", criteria);

    assertThat(result).containsExactly(training);
  }

  @Test
  void getTraineeTrainingsShouldTreatNullCriteriaAsNoOptionalFilters() {
    Training training = createTraining();

    when(trainerService.searchTrainersByName(null)).thenReturn(List.of());
    when(trainingRepository.findTraineeTrainings("john.doe", null, null, false, List.of(), null))
        .thenReturn(List.of(training));

    List<Training> result = trainingService.getTraineeTrainings("john.doe", null);

    assertThat(result).containsExactly(training);
  }

  @Test
  void getTraineeTrainingsShouldThrowExceptionWhenTraineeUsernameIsBlank() {
    assertThatThrownBy(
            () ->
                trainingService.getTraineeTrainings(
                    " ", new TraineeTrainingsCriteriaDto(null, null, null, null)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("traineeUsername must not be blank");

    verifyNoInteractions(trainerService, trainingRepository);
  }

  @Test
  void getTrainerTrainingsShouldReturnEntitiesMatchingCriteria() {
    LocalDate fromDate = LocalDate.of(2026, 5, 1);
    LocalDate toDate = LocalDate.of(2026, 5, 31);
    TrainerTrainingsCriteriaDto criteria =
        new TrainerTrainingsCriteriaDto(fromDate, toDate, " John Doe ");
    Training training = createTraining();

    when(trainingRepository.findTrainerTrainings("jane.doe", fromDate, toDate, "John Doe"))
        .thenReturn(List.of(training));

    List<Training> result = trainingService.getTrainerTrainings("jane.doe", criteria);

    assertThat(result).containsExactly(training);
  }

  @Test
  void getTrainerTrainingsShouldTreatBlankOptionalCriteriaAsNoOptionalFilters() {
    TrainerTrainingsCriteriaDto criteria = new TrainerTrainingsCriteriaDto(null, null, " ");
    Training training = createTraining();

    when(trainingRepository.findTrainerTrainings("jane.doe", null, null, null))
        .thenReturn(List.of(training));

    List<Training> result = trainingService.getTrainerTrainings("jane.doe", criteria);

    assertThat(result).containsExactly(training);
  }

  @Test
  void getTrainerTrainingsShouldTreatNullCriteriaAsNoOptionalFilters() {
    Training training = createTraining();

    when(trainingRepository.findTrainerTrainings("jane.doe", null, null, null))
        .thenReturn(List.of(training));

    List<Training> result = trainingService.getTrainerTrainings("jane.doe", null);

    assertThat(result).containsExactly(training);
  }

  @Test
  void getTrainerTrainingsShouldThrowExceptionWhenTrainerUsernameIsBlank() {
    assertThatThrownBy(
            () ->
                trainingService.getTrainerTrainings(
                    " ", new TrainerTrainingsCriteriaDto(null, null, null)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("trainerUsername must not be blank");

    verifyNoInteractions(trainingRepository);
    verifyNoMoreInteractions(trainerService);
  }

  private Training createTraining() {
    TrainingType type = createTrainingType();
    Trainee trainee = createTrainee(createUser(1L, "john.doe"));
    Trainer trainer = createTrainer(createUser(2L, "jane.doe"), type);
    return createTraining(1L, type, trainee, trainer);
  }

  private Training createTraining(
      Long id, TrainingType type, Trainee trainee, Trainer trainer) {
    Training training = new Training();
    training.setId(id);
    training.setName("Java Basics");
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

  private Trainee createTrainee(User profile) {
    Trainee trainee = new Trainee();
    trainee.setId(3L);
    trainee.setUser(profile);
    trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
    trainee.setAddress("Baku");
    return trainee;
  }

  private Trainer createTrainer(User profile, TrainingType specialization) {
    Trainer trainer = new Trainer();
    trainer.setId(4L);
    trainer.setUser(profile);
    trainer.setSpecialization(specialization);
    return trainer;
  }

  private User createUser(Long id, String username) {
    User profile = new User();
    profile.setId(id);
    profile.setFirstName("John");
    profile.setLastName("Doe");
    profile.setUsername(username);
    profile.setPassword("password");
    profile.setActive(true);
    return profile;
  }

  private TrainingCreateDto createTrainingCreateDto() {
    return new TrainingCreateDto(
        "Java Basics", "john.doe", "jane.doe", LocalDate.of(2026, 5, 8), 60);
  }
}
