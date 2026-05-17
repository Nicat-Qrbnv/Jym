package com.epam.jym.crm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.repository.TrainingTypeRepository;
import com.epam.jym.crm.service.UserService;
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
class TrainerServiceImplTest {

  @Mock
  private TrainerRepository trainerRepository;

  @Mock
  private UserService userService;

  @Mock
  private TrainingTypeRepository trainingTypeRepository;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private TrainerServiceImpl trainerService;

  @BeforeEach
  public void setUp() {
    trainerService.setMapper(modelMapper);
  }

  @Test
  void createTrainerShouldLoadUserAndSpecializationSaveProfileAndReturnDto() {
    TrainerCreateDto trainerDto = createTrainerCreateDto();
    User user = createUser(1L, "john.doe");
    TrainingType specialization = createTrainingType(2L, "Fitness");
    Trainer savedTrainer = createTrainer(10L, user, specialization);
    TrainerDto savedTrainerDto = createTrainerDto(10L, "john.doe");

    when(trainerRepository.userHasTrainerProfile(1L)).thenReturn(false);
    when(userService.getUser(1L)).thenReturn(user);
    when(trainingTypeRepository.findById(2L)).thenReturn(Optional.of(specialization));
    when(trainerRepository.save(any(Trainer.class))).thenReturn(savedTrainer);
    when(modelMapper.map(savedTrainer, TrainerDto.class)).thenReturn(savedTrainerDto);

    TrainerDto result = trainerService.createTrainer(trainerDto);

    Assertions.assertThat(result).isSameAs(savedTrainerDto);
    ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
    verify(trainerRepository).save(trainerCaptor.capture());
    Assertions.assertThat(trainerCaptor.getValue().getUser()).isSameAs(user);
    Assertions.assertThat(trainerCaptor.getValue().getSpecialization()).isSameAs(specialization);
  }

  @Test
  void createTrainerShouldThrowExceptionWhenUserAlreadyHasTrainerProfile() {
    TrainerCreateDto trainerDto = createTrainerCreateDto();

    when(trainerRepository.userHasTrainerProfile(1L)).thenReturn(true);

    Assertions.assertThatThrownBy(() -> trainerService.createTrainer(trainerDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("User already has a trainer profile: 1");

    verifyNoInteractions(userService, trainingTypeRepository, modelMapper);
  }

  @Test
  void createTrainerShouldThrowExceptionWhenSpecializationDoesNotExist() {
    TrainerCreateDto trainerDto = createTrainerCreateDto();
    User user = createUser(1L, "john.doe");

    when(trainerRepository.userHasTrainerProfile(1L)).thenReturn(false);
    when(userService.getUser(1L)).thenReturn(user);
    when(trainingTypeRepository.findById(2L)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.createTrainer(trainerDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Training type not found: 2");

    verifyNoInteractions(modelMapper);
  }

  @Test
  void updateTrainerShouldUpdateSpecialization() {
    Long trainerId = 10L;
    TrainerUpdateDto trainerDto = createTrainerUpdateDto();
    TrainingType oldSpecialization = createTrainingType(1L, "Yoga");
    TrainingType newSpecialization = createTrainingType(2L, "Fitness");
    Trainer existingTrainer =
        createTrainer(trainerId, createUser(1L, "john.doe"), oldSpecialization);
    TrainerDto savedTrainerDto = createTrainerDto(trainerId, "John.Doe");

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
    when(trainingTypeRepository.findById(2L)).thenReturn(Optional.of(newSpecialization));
    when(trainerRepository.save(existingTrainer)).thenReturn(existingTrainer);
    when(modelMapper.map(existingTrainer, TrainerDto.class)).thenReturn(savedTrainerDto);

    TrainerDto result = trainerService.updateTrainer(trainerId, trainerDto);

    Assertions.assertThat(result).isSameAs(savedTrainerDto);
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
    when(trainingTypeRepository.findById(2L)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.updateTrainer(trainerId, trainerDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Training type not found: 2");

    verifyNoInteractions(modelMapper);
  }

  @Test
  void updateTrainerShouldThrowExceptionWhenTrainerDoesNotExist() {
    Long trainerId = 404L;
    TrainerUpdateDto trainerDto = createTrainerUpdateDto();

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.updateTrainer(trainerId, trainerDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainer not found by id: " + trainerId);

    verifyNoInteractions(userService, trainingTypeRepository, modelMapper);
  }

  @Test
  void selectTrainerShouldReturnMappedDtoWhenTrainerExists() {
    Long trainerId = 10L;
    Trainer trainer =
        createTrainer(trainerId, createUser(1L, "john.doe"), createTrainingType(2L, "Fitness"));
    TrainerDto trainerDto = createTrainerDto(trainerId, "john.doe");

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(trainer));
    when(modelMapper.map(trainer, TrainerDto.class)).thenReturn(trainerDto);

    TrainerDto result = trainerService.selectTrainer(trainerId);

    Assertions.assertThat(result).isSameAs(trainerDto);
  }

  @Test
  void selectTrainerShouldThrowExceptionWhenTrainerDoesNotExist() {
    Long trainerId = 404L;

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.selectTrainer(trainerId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainer not found by id: " + trainerId);

    verifyNoInteractions(modelMapper);
  }

  @Test
  void selectTrainerByUsernameShouldReturnMappedDtoWhenTrainerExists() {
    String username = "john.doe";
    Trainer trainer =
        createTrainer(10L, createUser(1L, username), createTrainingType(2L, "Fitness"));
    TrainerDto trainerDto = createTrainerDto(10L, username);

    when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));
    when(modelMapper.map(trainer, TrainerDto.class)).thenReturn(trainerDto);

    TrainerDto result = trainerService.selectTrainerByUsername(username);

    Assertions.assertThat(result).isSameAs(trainerDto);
  }

  @Test
  void selectTrainerByUsernameShouldThrowExceptionWhenTrainerDoesNotExist() {
    String username = "missing";

    when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.selectTrainerByUsername(username))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainer not found: " + username);

    verifyNoInteractions(modelMapper);
  }

  @Test
  void selectAllTrainersShouldReturnMappedDtos() {
    Trainer firstTrainer =
        createTrainer(1L, createUser(1L, "first.trainer"), createTrainingType(2L, "Fitness"));
    Trainer secondTrainer =
        createTrainer(2L, createUser(2L, "second.trainer"), createTrainingType(2L, "Fitness"));
    TrainerDto firstDto = createTrainerDto(1L, "first.trainer");
    TrainerDto secondDto = createTrainerDto(2L, "second.trainer");

    when(trainerRepository.findAll()).thenReturn(List.of(firstTrainer, secondTrainer));
    when(modelMapper.map(firstTrainer, TrainerDto.class)).thenReturn(firstDto);
    when(modelMapper.map(secondTrainer, TrainerDto.class)).thenReturn(secondDto);

    List<TrainerDto> result = trainerService.selectAllTrainers();

    Assertions.assertThat(result).containsExactly(firstDto, secondDto);
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

  private TrainerDto createTrainerDto(Long id, String username) {
    return new TrainerDto(id, 1L, username, new TrainingTypeDto(2L, "Fitness"));
  }

  private TrainerCreateDto createTrainerCreateDto() {
    return new TrainerCreateDto(1L, 2L);
  }

  private TrainerUpdateDto createTrainerUpdateDto() {
    return new TrainerUpdateDto(2L);
  }
}

