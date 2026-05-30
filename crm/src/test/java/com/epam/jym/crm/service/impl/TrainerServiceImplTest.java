package com.epam.jym.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.dto.user.UserDto;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.InvalidRequestException;
import com.epam.jym.crm.exception.ResourceNotFoundException;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.service.TrainingTypeService;
import com.epam.jym.crm.service.UserService;
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
  void createTrainerShouldRegisterUserResolveSpecializationSaveProfileAndReturnEntity() {
    TrainerCreateDto trainerDto = createTrainerCreateDto();
    User profile = createUser(1L, "john.doe");
    TrainingType specialization = createTrainingType(2L, "Fitness");
    Trainer savedTrainer = createTrainer(10L, profile, specialization);

    when(userService.register(trainerDto.profile())).thenReturn(profile);
    when(trainingTypeService.getTypeIfValid(trainerDto.specialization()))
        .thenReturn(specialization);
    when(trainerRepository.save(any(Trainer.class))).thenReturn(savedTrainer);

    Trainer result = trainerService.createTrainer(trainerDto);

    Assertions.assertThat(result).isSameAs(savedTrainer);
    ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
    verify(trainerRepository).save(trainerCaptor.capture());
    Assertions.assertThat(trainerCaptor.getValue().getUser()).isSameAs(profile);
    Assertions.assertThat(trainerCaptor.getValue().getSpecialization()).isSameAs(specialization);
  }

  @Test
  void createTrainerShouldThrowExceptionWhenDtoIsNull() {
    Assertions.assertThatThrownBy(() -> trainerService.createTrainer(null))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage("trainerDto must not be null");

    verifyNoInteractions(trainerRepository, traineeRepository, userService, trainingTypeService);
  }

  @Test
  void createTrainerShouldThrowExceptionWhenSpecializationDoesNotExist() {
    TrainerCreateDto trainerDto = createTrainerCreateDto();
    User profile = createUser(1L, "john.doe");

    when(userService.register(trainerDto.profile())).thenReturn(profile);
    when(trainingTypeService.getTypeIfValid(trainerDto.specialization()))
        .thenThrow(new ResourceNotFoundException("Training type not found: 2"));

    Assertions.assertThatThrownBy(() -> trainerService.createTrainer(trainerDto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Training type not found: 2");

    verifyNoMoreInteractions(trainerRepository);
  }

  @Test
  void updateTrainerProfileShouldUpdateUserFieldsAndKeepSpecialization() {
    String username = "john.doe";
    TrainerUpdateDto trainerDto = createTrainerUpdateDto();
    TrainingType existingSpecialization = createTrainingType(2L, "Fitness");
    Trainer existingTrainer = createTrainer(10L, createUser(1L, username), existingSpecialization);

    when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(existingTrainer));
    when(trainerRepository.save(existingTrainer)).thenReturn(existingTrainer);

    Trainer result = trainerService.updateTrainerProfile(username, trainerDto);

    Assertions.assertThat(result).isSameAs(existingTrainer);
    Assertions.assertThat(existingTrainer.getUser().getFirstName()).isEqualTo("Jane");
    Assertions.assertThat(existingTrainer.getUser().getLastName()).isEqualTo("Smith");
    Assertions.assertThat(existingTrainer.getUser().isActive()).isFalse();
    Assertions.assertThat(existingTrainer.getSpecialization()).isSameAs(existingSpecialization);
    verify(trainerRepository).save(existingTrainer);
    verifyNoInteractions(trainingTypeService, userService, traineeRepository);
  }

  @Test
  void updateTrainerProfileShouldThrowExceptionWhenDtoIsNull() {
    Assertions.assertThatThrownBy(() -> trainerService.updateTrainerProfile("john.doe", null))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage("trainerDto must not be null");

    verifyNoInteractions(trainerRepository, traineeRepository, userService, trainingTypeService);
  }

  @Test
  void updateTrainerProfileShouldThrowExceptionWhenTrainerDoesNotExist() {
    String username = "missing";
    TrainerUpdateDto trainerDto = createTrainerUpdateDto();

    when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.updateTrainerProfile(username, trainerDto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Trainer not found: " + username);

    verifyNoMoreInteractions(trainerRepository);
    verifyNoInteractions(userService, trainingTypeService, traineeRepository);
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
  void getTrainerShouldThrowExceptionWhenTrainerDoesNotExist() {
    Long trainerId = 404L;

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.getTrainer(trainerId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Trainer not found by id: " + trainerId);
  }

  @Test
  void getTrainerByUsernameShouldReturnEntityWhenTrainerExists() {
    String username = "john.doe";
    Trainer trainer =
        createTrainer(10L, createUser(1L, username), createTrainingType(2L, "Fitness"));

    when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

    Trainer result = trainerService.getTrainerByUsername(username);

    Assertions.assertThat(result).isSameAs(trainer);
  }

  @Test
  void getTrainerByUsernameShouldThrowExceptionWhenTrainerDoesNotExist() {
    String username = "missing";

    when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.getTrainerByUsername(username))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Trainer not found: " + username);
  }

  @Test
  void getTrainersByUsernamesShouldReturnEntitiesWhenUsernamesProvided() {
    List<String> usernames = List.of("first.trainer", "second.trainer");
    Trainer firstTrainer =
        createTrainer(1L, createUser(1L, "first.trainer"), createTrainingType(2L, "Fitness"));
    Trainer secondTrainer =
        createTrainer(2L, createUser(2L, "second.trainer"), createTrainingType(3L, "Yoga"));

    when(trainerRepository.findByUsernames(usernames))
        .thenReturn(List.of(firstTrainer, secondTrainer));

    List<Trainer> result = trainerService.getTrainersByUsernames(usernames);

    Assertions.assertThat(result).containsExactly(firstTrainer, secondTrainer);
  }

  @Test
  void getTrainersByUsernamesShouldReturnEmptyListWhenUsernamesAreNullOrEmpty() {
    Assertions.assertThat(trainerService.getTrainersByUsernames(null)).isEmpty();
    Assertions.assertThat(trainerService.getTrainersByUsernames(List.of())).isEmpty();

    verifyNoInteractions(trainerRepository, traineeRepository, userService, trainingTypeService);
  }

  @Test
  void getTrainersNotAssignedToTraineeShouldReturnEntitiesWhenTraineeExists() {
    String traineeUsername = "john.doe";
    Trainer firstTrainer =
        createTrainer(1L, createUser(2L, "first.trainer"), createTrainingType(2L, "Fitness"));
    Trainer secondTrainer =
        createTrainer(2L, createUser(3L, "second.trainer"), createTrainingType(3L, "Yoga"));

    when(traineeRepository.existsByUsername(traineeUsername)).thenReturn(true);
    when(trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername))
        .thenReturn(List.of(firstTrainer, secondTrainer));

    List<Trainer> result = trainerService.getTrainersNotAssignedToTrainee(traineeUsername);

    Assertions.assertThat(result).containsExactly(firstTrainer, secondTrainer);
  }

  @Test
  void getTrainersNotAssignedToTraineeShouldThrowExceptionWhenTraineeDoesNotExist() {
    String traineeUsername = "missing";

    when(traineeRepository.existsByUsername(traineeUsername)).thenReturn(false);

    Assertions.assertThatThrownBy(
            () -> trainerService.getTrainersNotAssignedToTrainee(traineeUsername))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Trainee not found: " + traineeUsername);

    verifyNoInteractions(userService, trainingTypeService);
    verifyNoMoreInteractions(trainerRepository);
  }

  @Test
  void searchTrainersByNameShouldReturnMatchingTrainerIds() {
    when(trainerRepository.findIdsByNameContaining("%john%")).thenReturn(List.of(1L, 2L));

    List<Long> result = trainerService.searchTrainersByName("john");

    Assertions.assertThat(result).containsExactly(1L, 2L);
  }

  @Test
  void searchTrainersByNameShouldReturnEmptyListWhenNameIsNullOrBlank() {
    Assertions.assertThat(trainerService.searchTrainersByName(null)).isEmpty();
    Assertions.assertThat(trainerService.searchTrainersByName(" ")).isEmpty();

    verifyNoInteractions(trainerRepository, traineeRepository, userService, trainingTypeService);
  }

  private Trainer createTrainer(Long id, User profile, TrainingType specialization) {
    Trainer trainer = new Trainer();
    trainer.setId(id);
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

  private TrainingType createTrainingType(Long id, String name) {
    TrainingType trainingType = new TrainingType();
    trainingType.setId(id);
    trainingType.setName(name);
    return trainingType;
  }

  private TrainerCreateDto createTrainerCreateDto() {
    return new TrainerCreateDto(
        new UserCreateDto("John", "Doe"), new TrainingTypeDto(2L, "Fitness"));
  }

  private TrainerUpdateDto createTrainerUpdateDto() {
    return new TrainerUpdateDto(
        new UserDto("Jane", "Smith", false), new TrainingTypeDto(2L, "Fitness"));
  }
}
