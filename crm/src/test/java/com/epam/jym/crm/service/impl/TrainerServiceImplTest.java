package com.epam.jym.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.service.TrainingTypeService;
import com.epam.jym.crm.service.UserService;
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
class TrainerServiceImplTest {

  @Mock private TrainerRepository trainerRepository;

  @Mock private TraineeRepository traineeRepository;

  @Mock private UserService userService;

  @Mock private TrainingTypeService trainingTypeService;

  @InjectMocks private TrainerServiceImpl trainerService;

  @Test
  void createTrainerShouldRegisterUserAndSpecializationSaveProfileAndReturnEntity() {
    TrainerCreateDto trainerDto = createTrainerCreateDto();
    User user = createUser(1L, "john.doe");
    TrainingType specialization = createTrainingType(2L, "Fitness");
    Trainer savedTrainer = createTrainer(10L, user, specialization);

    when(userService.register(trainerDto.userDto())).thenReturn(user);
    when(trainingTypeService.getType(2L)).thenReturn(specialization);
    when(trainerRepository.save(any(Trainer.class))).thenReturn(savedTrainer);

    Trainer result = trainerService.createTrainer(trainerDto);

    Assertions.assertThat(result).isSameAs(savedTrainer);
    ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
    verify(trainerRepository).save(trainerCaptor.capture());
    Assertions.assertThat(trainerCaptor.getValue().getUser()).isSameAs(user);
    Assertions.assertThat(trainerCaptor.getValue().getSpecialization()).isSameAs(specialization);
  }

  @Test
  void createTrainerShouldThrowExceptionWhenSpecializationDoesNotExist() {
    TrainerCreateDto trainerDto = createTrainerCreateDto();
    User user = createUser(1L, "john.doe");

    when(userService.register(trainerDto.userDto())).thenReturn(user);
    when(trainingTypeService.getType(2L))
        .thenThrow(new IllegalArgumentException("Training type not found: 2"));

    Assertions.assertThatThrownBy(() -> trainerService.createTrainer(trainerDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Training type not found: 2");
  }

  @Test
  void updateTrainerShouldUpdateSpecialization() {
    Long trainerId = 10L;
    TrainerUpdateDto trainerDto = createTrainerUpdateDto();
    TrainingType oldSpecialization = createTrainingType(1L, "Yoga");
    TrainingType newSpecialization = createTrainingType(2L, "Fitness");
    Trainer existingTrainer =
        createTrainer(trainerId, createUser(1L, "john.doe"), oldSpecialization);

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
    when(trainingTypeService.getType(2L)).thenReturn(newSpecialization);
    when(trainerRepository.save(existingTrainer)).thenReturn(existingTrainer);

    Trainer result = trainerService.updateTrainer(trainerId, trainerDto);

    Assertions.assertThat(result).isSameAs(existingTrainer);
    Assertions.assertThat(existingTrainer.getSpecialization()).isSameAs(newSpecialization);
    verify(trainerRepository).save(existingTrainer);
  }

  @Test
  void updateTrainerShouldThrowExceptionWhenSpecializationDoesNotExist() {
    Long trainerId = 10L;
    TrainerUpdateDto trainerDto = createTrainerUpdateDto();
    Trainer existingTrainer =
        createTrainer(trainerId, createUser(1L, "john.doe"), createTrainingType(1L, "Yoga"));

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
    when(trainingTypeService.getType(2L))
        .thenThrow(new IllegalArgumentException("Training type not found: 2"));

    Assertions.assertThatThrownBy(() -> trainerService.updateTrainer(trainerId, trainerDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Training type not found: 2");
  }

  @Test
  void updateTrainerShouldThrowExceptionWhenTrainerDoesNotExist() {
    Long trainerId = 404L;
    TrainerUpdateDto trainerDto = createTrainerUpdateDto();

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.updateTrainer(trainerId, trainerDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainer not found by id: " + trainerId);

    verifyNoInteractions(userService, trainingTypeService);
  }

  @Test
  void getTrainerShouldReturnEntityWhenTrainerExists() {
    Long trainerId = 10L;
    Trainer trainer =
        createTrainer(trainerId, createUser(1L, "john.doe"), createTrainingType(2L, "Fitness"));

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(trainer));

    Trainer result = trainerService.getTrainer(trainerId);

    Assertions.assertThat(result).isSameAs(trainer);
  }

  @Test
  void selectTrainerShouldThrowExceptionWhenTrainerDoesNotExist() {
    Long trainerId = 404L;

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.getTrainer(trainerId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainer not found by id: " + trainerId);
  }

  @Test
  void getTrainerByUsernameShouldReturnEntityWhenTrainerExists() {
    String username = "john.doe";
    Trainer trainer =
        createTrainer(10L, createUser(1L, username), createTrainingType(2L, "Fitness"));

    when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));

    Trainer result = trainerService.getTrainerByUsername(username);

    Assertions.assertThat(result).isSameAs(trainer);
  }

  @Test
  void getTrainerByUsernameShouldThrowExceptionWhenTrainerDoesNotExist() {
    String username = "missing";

    when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.getTrainerByUsername(username))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainer not found: " + username);
  }

  @Test
  void getAllTrainersShouldReturnEntities() {
    Trainer firstTrainer =
        createTrainer(1L, createUser(1L, "first.trainer"), createTrainingType(2L, "Fitness"));
    Trainer secondTrainer =
        createTrainer(2L, createUser(2L, "second.trainer"), createTrainingType(2L, "Fitness"));

    when(trainerRepository.findAll()).thenReturn(List.of(firstTrainer, secondTrainer));

    List<Trainer> result = trainerService.getAllTrainers();

    Assertions.assertThat(result).containsExactly(firstTrainer, secondTrainer);
  }

  @Test
  void getTrainersNotAssignedToTraineeShouldReturnEntities() {
    String traineeUsername = "john.doe";
    Trainee trainee = createTrainee(createUser(1L, traineeUsername));
    Trainer firstTrainer =
        createTrainer(1L, createUser(2L, "first.trainer"), createTrainingType(2L, "Fitness"));
    Trainer secondTrainer =
        createTrainer(2L, createUser(3L, "second.trainer"), createTrainingType(3L, "Yoga"));

    when(traineeRepository.findByUserUsername(traineeUsername)).thenReturn(Optional.of(trainee));
    when(trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername))
        .thenReturn(List.of(firstTrainer, secondTrainer));

    List<Trainer> result = trainerService.getTrainersNotAssignedToTrainee(traineeUsername);

    Assertions.assertThat(result).containsExactly(firstTrainer, secondTrainer);
  }

  @Test
  void getTrainersNotAssignedToTraineeShouldThrowExceptionWhenTraineeDoesNotExist() {
    String traineeUsername = "missing";

    when(traineeRepository.findByUserUsername(traineeUsername)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(
            () -> trainerService.getTrainersNotAssignedToTrainee(traineeUsername))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainee not found: " + traineeUsername);

    verifyNoInteractions(userService, trainingTypeService);
  }

  private Trainee createTrainee(User user) {
    Trainee trainee = new Trainee();
    trainee.setId(10L);
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

  private TrainingType createTrainingType(Long id, String name) {
    TrainingType trainingType = new TrainingType();
    trainingType.setId(id);
    trainingType.setName(name);
    return trainingType;
  }

  private TrainerCreateDto createTrainerCreateDto() {
    return new TrainerCreateDto(new UserCreateDto("John", "Doe"), 2L);
  }

  private TrainerUpdateDto createTrainerUpdateDto() {
    return new TrainerUpdateDto(2L);
  }
}
