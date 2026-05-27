package com.epam.jym.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
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
    User user = createUser(1L, "john.doe");
    Trainee savedTrainee = createTrainee(10L, user);

    when(userService.register(traineeDto.userDto())).thenReturn(user);
    when(traineeRepository.save(any(Trainee.class))).thenReturn(savedTrainee);

    Trainee result = traineeService.createTrainee(traineeDto);

    Assertions.assertThat(result).isSameAs(savedTrainee);
    ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
    verify(traineeRepository).save(traineeCaptor.capture());
    Assertions.assertThat(traineeCaptor.getValue().getUser()).isSameAs(user);
    Assertions.assertThat(traineeCaptor.getValue().getDateOfBirth())
        .isEqualTo(traineeDto.dateOfBirth());
    Assertions.assertThat(traineeCaptor.getValue().getAddress()).isEqualTo(traineeDto.address());
  }

  @Test
  void updateTraineeShouldUpdateBirthDateAndAddress() {
    Long traineeId = 10L;
    final TraineeUpdateDto traineeDto = createTraineeUpdateDto();
    Trainee existingTrainee = createTrainee(traineeId, createUser(1L, "john.doe"));
    existingTrainee.setDateOfBirth(LocalDate.of(1999, 12, 31));
    existingTrainee.setAddress("Old address");

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(existingTrainee));
    when(traineeRepository.save(existingTrainee)).thenReturn(existingTrainee);

    Trainee result = traineeService.updateTrainee(traineeId, traineeDto);

    Assertions.assertThat(result).isSameAs(existingTrainee);
    Assertions.assertThat(existingTrainee.getDateOfBirth()).isEqualTo(traineeDto.dateOfBirth());
    Assertions.assertThat(existingTrainee.getAddress()).isEqualTo(traineeDto.address());
    verify(traineeRepository).save(existingTrainee);
  }

  @Test
  void updateTraineeShouldThrowExceptionWhenTraineeDoesNotExist() {
    Long traineeId = 404L;
    TraineeUpdateDto traineeDto = createTraineeUpdateDto();

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> traineeService.updateTrainee(traineeId, traineeDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + traineeId);

    verifyNoInteractions(userService);
  }

  @Test
  void deleteTraineeShouldDeleteById() {
    Long traineeId = 10L;

    traineeService.deleteTrainee(traineeId);

    verify(traineeRepository).deleteById(traineeId);
  }

  @Test
  void deleteTraineeShouldDeleteByUsername() {
    String username = "john.doe";
    Trainee trainee = createTrainee(10L, createUser(1L, username));

    when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));

    traineeService.deleteTrainee(username);

    verify(traineeRepository).delete(trainee);
  }

  @Test
  void deleteTraineeShouldThrowExceptionWhenUsernameDoesNotExist() {
    String username = "missing";

    when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.empty());

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

    when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));

    Trainee result = traineeService.getTraineeByUsername(username);

    Assertions.assertThat(result).isSameAs(trainee);
  }

  @Test
  void getTraineeByUsernameShouldThrowExceptionWhenTraineeDoesNotExist() {
    String username = "missing";

    when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> traineeService.getTraineeByUsername(username))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + username);
  }

  @Test
  void getAllTraineesShouldReturnEntities() {
    Trainee firstTrainee = createTrainee(1L, createUser(1L, "first.trainee"));
    Trainee secondTrainee = createTrainee(2L, createUser(2L, "second.trainee"));

    when(traineeRepository.findAll()).thenReturn(List.of(firstTrainee, secondTrainee));

    List<Trainee> result = traineeService.getAllTrainees();

    Assertions.assertThat(result).containsExactly(firstTrainee, secondTrainee);
  }

  @Test
  void updateTraineeTrainersShouldReplaceTrainerListAndReturnEntities() {
    Long traineeId = 10L;
    Long firstTrainerId = 1L;
    Long secondTrainerId = 2L;
    Trainee trainee = createTrainee(traineeId, createUser(1L, "john.doe"));
    Trainer firstTrainer =
        createTrainer(
            firstTrainerId, createUser(2L, "first.trainer"), createTrainingType(1L, "Yoga"));
    Trainer secondTrainer =
        createTrainer(
            secondTrainerId, createUser(3L, "second.trainer"), createTrainingType(2L, "Fitness"));

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(trainee));
    when(trainerService.getTrainersByIds(List.of(firstTrainerId, secondTrainerId)))
        .thenReturn(List.of(firstTrainer, secondTrainer));

    List<Trainer> result =
        traineeService.updateTraineeTrainers(traineeId, List.of(firstTrainerId, secondTrainerId));

    Assertions.assertThat(result).containsExactly(firstTrainer, secondTrainer);
    Assertions.assertThat(trainee.getTrainers()).containsExactly(firstTrainer, secondTrainer);
    verify(traineeRepository).save(trainee);
  }

  @Test
  void updateTraineeTrainersShouldClearTrainerListWhenTrainerIdsAreEmpty() {
    Long traineeId = 10L;
    Trainee trainee = createTrainee(traineeId, createUser(1L, "john.doe"));
    trainee.setTrainers(List.of(createTrainer(1L, createUser(2L, "first.trainer"), null)));

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(trainee));

    List<Trainer> result = traineeService.updateTraineeTrainers(traineeId, List.of());

    Assertions.assertThat(result).isEmpty();
    Assertions.assertThat(trainee.getTrainers()).isEmpty();
    verify(traineeRepository).save(trainee);
    verifyNoInteractions(trainerService);
  }

  @Test
  void updateTraineeTrainersShouldThrowExceptionWhenTraineeDoesNotExist() {
    Long traineeId = 404L;

    when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(
            () -> traineeService.updateTraineeTrainers(traineeId, List.of(1L)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + traineeId);

    verifyNoInteractions(trainerService, userService);
  }

  private Trainee createTrainee(Long id, User user) {
    Trainee trainee = new Trainee();
    trainee.setId(id);
    trainee.setUser(user);
    trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
    trainee.setAddress("Baku");
    return trainee;
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

  private Trainer createTrainer(Long id, User user, TrainingType specialization) {
    Trainer trainer = new Trainer();
    trainer.setId(id);
    trainer.setUser(user);
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

  private TraineeUpdateDto createTraineeUpdateDto() {
    return new TraineeUpdateDto(LocalDate.of(2000, 1, 1), "Baku");
  }
}
