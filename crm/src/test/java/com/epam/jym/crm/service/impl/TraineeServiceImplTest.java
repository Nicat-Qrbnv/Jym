package com.epam.jym.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.dto.user.UserDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.service.TraineeService;
import com.epam.jym.crm.service.TrainerService;
import com.epam.jym.crm.service.UserService;
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

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

  @Mock private TraineeRepository traineeRepository;

  @Mock private TrainerService trainerService;

  @Mock private UserService userService;

  @InjectMocks private TraineeServiceImpl traineeServiceImpl;

  private TraineeService traineeService;

  @BeforeEach
  public void setUp() {
    traineeService = traineeServiceImpl;
  }

  @Test
  void createTraineeShouldRegisterUserSaveProfileAndReturnEntity() {
    TraineeCreateDto traineeDto = createTraineeCreateDto();
    User profile = createUser(1L, "john.doe");
    Trainee savedTrainee = createTrainee(10L, profile);

    when(userService.register(traineeDto.profile())).thenReturn(profile);
    when(traineeRepository.save(any(Trainee.class))).thenReturn(savedTrainee);

    Trainee result = traineeService.createTrainee(traineeDto);

    Assertions.assertThat(result).isSameAs(savedTrainee);
    ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
    verify(traineeRepository).save(traineeCaptor.capture());
    Assertions.assertThat(traineeCaptor.getValue().getUser()).isSameAs(profile);
    Assertions.assertThat(traineeCaptor.getValue().getDateOfBirth())
        .isEqualTo(traineeDto.dateOfBirth());
    Assertions.assertThat(traineeCaptor.getValue().getAddress()).isEqualTo(traineeDto.address());
  }

  @Test
  void createTraineeShouldThrowExceptionWhenDtoIsNull() {
    Assertions.assertThatThrownBy(() -> traineeService.createTrainee(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("traineeDto must not be null");

    verifyNoInteractions(traineeRepository, trainerService, userService);
  }

  @Test
  void updateTraineeProfileShouldUpdateUserAndTraineeFields() {
    TraineeUpdateDto traineeDto =
        new TraineeUpdateDto(
            new UserDto("Jane", "Smith", false), LocalDate.of(1995, 5, 5), "New address");
    Trainee existingTrainee = createTrainee(10L, createUser(1L, "john.doe"));

    when(traineeRepository.findTraineeByUsername("john.doe"))
        .thenReturn(Optional.of(existingTrainee));
    when(traineeRepository.save(existingTrainee)).thenReturn(existingTrainee);

    Trainee result = traineeService.updateTraineeProfile("john.doe", traineeDto);

    Assertions.assertThat(result).isSameAs(existingTrainee);
    Assertions.assertThat(existingTrainee.getUser().getFirstName()).isEqualTo("Jane");
    Assertions.assertThat(existingTrainee.getUser().getLastName()).isEqualTo("Smith");
    Assertions.assertThat(existingTrainee.getUser().isActive()).isFalse();
    Assertions.assertThat(existingTrainee.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 5, 5));
    Assertions.assertThat(existingTrainee.getAddress()).isEqualTo("New address");
    verify(traineeRepository).save(existingTrainee);
  }

  @Test
  void updateTraineeProfileShouldThrowExceptionWhenTraineeDoesNotExist() {
    TraineeUpdateDto traineeDto =
        new TraineeUpdateDto(new UserDto("Jane", "Smith", true), null, null);

    when(traineeRepository.findTraineeByUsername("missing"))
        .thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> traineeService.updateTraineeProfile("missing", traineeDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: missing");

    verifyNoInteractions(userService);
  }

  @Test
  void updateTraineeProfileShouldThrowExceptionWhenDtoIsNull() {
    Assertions.assertThatThrownBy(() -> traineeService.updateTraineeProfile("john.doe", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("traineeDto must not be null");

    verifyNoInteractions(traineeRepository, trainerService, userService);
  }

  @Test
  void deleteTraineeShouldDeleteEntityAndDeactivateUser() {
    String username = "john.doe";
    Trainee trainee = createTrainee(10L, createUser(1L, username));

    when(traineeRepository.findTraineeByUsername(username)).thenReturn(Optional.of(trainee));

    traineeService.deleteTrainee(username);

    verify(traineeRepository).delete(trainee);
    verify(userService).deactivateUser(1L);
  }

  @Test
  void deleteTraineeShouldThrowExceptionWhenUsernameDoesNotExist() {
    String username = "missing";

    when(traineeRepository.findTraineeByUsername(username)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> traineeService.deleteTrainee(username))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + username);

    verifyNoInteractions(userService);
  }

  @Test
  void getTraineeShouldReturnEntityWhenTraineeByIdExists() {
    Long traineeId = 10L;
    Trainee trainee = createTrainee(traineeId, createUser(1L, "john.doe"));

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(trainee));

    Trainee result = traineeService.getTrainee(traineeId);

    Assertions.assertThat(result).isSameAs(trainee);
  }

  @Test
  void getTraineeShouldThrowExceptionWhenTraineeByIdDoesNotExist() {
    Long traineeId = 404L;

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> traineeService.getTrainee(traineeId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + traineeId);
  }

  @Test
  void getTraineeByUsernameShouldReturnEntityWhenTraineeExists() {
    String username = "john.doe";
    Trainee trainee = createTrainee(10L, createUser(1L, username));

    when(traineeRepository.findTraineeByUsername(username))
        .thenReturn(Optional.of(trainee));

    Trainee result = traineeService.getTraineeByUsername(username);

    Assertions.assertThat(result).isSameAs(trainee);
  }

  @Test
  void getTraineeByUsernameShouldThrowExceptionWhenTraineeDoesNotExist() {
    String username = "missing";

    when(traineeRepository.findTraineeByUsername(username))
        .thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> traineeService.getTraineeByUsername(username))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + username);
  }

  @Test
  void updateTraineeTrainersShouldReplaceTrainerListAndReturnEntities() {
    String traineeUsername = "john.doe";
    Trainee trainee = createTrainee(10L, createUser(1L, traineeUsername));
    Trainer firstTrainer =
        createTrainer(1L, createUser(2L, "first.trainer"), createTrainingType(1L, "Yoga"));
    Trainer secondTrainer =
        createTrainer(2L, createUser(3L, "second.trainer"), createTrainingType(2L, "Fitness"));

    when(traineeRepository.findTraineeByUsername(traineeUsername))
        .thenReturn(Optional.of(trainee));
    when(trainerService.getTrainersByUsernames(List.of("first.trainer", "second.trainer")))
        .thenReturn(List.of(firstTrainer, secondTrainer));

    List<Trainer> result =
        traineeService.updateTraineeTrainers(
            traineeUsername, List.of("first.trainer", "second.trainer"));

    Assertions.assertThat(result).containsExactly(firstTrainer, secondTrainer);
    Assertions.assertThat(trainee.getTrainers()).containsExactly(firstTrainer, secondTrainer);
    verify(traineeRepository).save(trainee);
  }

  @Test
  void updateTraineeTrainersShouldClearTrainerListWhenTrainerUsernamesAreEmpty() {
    String traineeUsername = "john.doe";
    Trainee trainee = createTrainee(10L, createUser(1L, traineeUsername));
    trainee.setTrainers(List.of(createTrainer(1L, createUser(2L, "first.trainer"), null)));

    when(traineeRepository.findTraineeByUsername(traineeUsername))
        .thenReturn(Optional.of(trainee));
    when(traineeRepository.save(trainee)).thenReturn(trainee);

    List<Trainer> result = traineeService.updateTraineeTrainers(traineeUsername, List.of());

    Assertions.assertThat(result).isEmpty();
    Assertions.assertThat(trainee.getTrainers()).isEmpty();
    verify(traineeRepository).save(trainee);
    verifyNoInteractions(trainerService);
  }

  @Test
  void updateTraineeTrainersShouldReturnSavedEmptyTrainerListWhenRepositoryAdjustsEntity() {
    String traineeUsername = "john.doe";
    Trainee trainee = createTrainee(10L, createUser(1L, traineeUsername));
    Trainer retainedTrainer = createTrainer(1L, createUser(2L, "retained.trainer"), null);
    Trainee savedTrainee = createTrainee(10L, createUser(1L, traineeUsername));
    savedTrainee.setTrainers(List.of(retainedTrainer));

    when(traineeRepository.findTraineeByUsername(traineeUsername))
        .thenReturn(Optional.of(trainee));
    when(traineeRepository.save(trainee)).thenReturn(savedTrainee);

    List<Trainer> result = traineeService.updateTraineeTrainers(traineeUsername, List.of());

    Assertions.assertThat(result).containsExactly(retainedTrainer);
    verifyNoInteractions(trainerService);
  }

  @Test
  void updateTraineeTrainersShouldThrowExceptionWhenTraineeDoesNotExist() {
    String traineeUsername = "missing";

    when(traineeRepository.findTraineeByUsername(traineeUsername))
        .thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(
            () -> traineeService.updateTraineeTrainers(traineeUsername, List.of("trainer")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + traineeUsername);

    verifyNoInteractions(trainerService, userService);
    verifyNoMoreInteractions(traineeRepository);
  }

  private Trainee createTrainee(Long id, User profile) {
    Trainee trainee = new Trainee();
    trainee.setId(id);
    trainee.setUser(profile);
    trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
    trainee.setAddress("Baku");
    return trainee;
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

  private Trainer createTrainer(Long id, User profile, TrainingType specialization) {
    Trainer trainer = new Trainer();
    trainer.setId(id);
    trainer.setUser(profile);
    trainer.setSpecialization(specialization);
    return trainer;
  }

  private TrainingType createTrainingType(Long id, String name) {
    TrainingType trainingType = new TrainingType();
    trainingType.setId(id);
    trainingType.setName(name);
    return trainingType;
  }

  private TraineeCreateDto createTraineeCreateDto() {
    return new TraineeCreateDto(new UserCreateDto("John", "Doe"), LocalDate.of(2000, 1, 1), "Baku");
  }
}
