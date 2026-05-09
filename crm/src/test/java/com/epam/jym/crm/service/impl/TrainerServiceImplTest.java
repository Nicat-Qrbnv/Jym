package com.epam.jym.crm.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.TrainerDto;
import com.epam.jym.crm.dto.TrainerUpdateDto;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.service.AuthenticationService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

  @Mock
  private TrainerRepository trainerRepository;

  @Mock
  private AuthenticationService authenticationService;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private TrainerServiceImpl trainerService;

  @BeforeEach
  public void setUp() {
    trainerService.setModelMapper(modelMapper);
  }

  @Test
  void createTrainerShouldRegisterSaveAndReturnDto() {
    TrainerDto trainerDto = createTrainerDto(1L, "john.doe");
    Trainer trainerEntity = createTrainer(null, null);
    Trainer registeredTrainer = createTrainer(10L, "john.doe");
    TrainerDto savedTrainerDto = createTrainerDto(10L, "john.doe");

    when(modelMapper.map(trainerDto, Trainer.class)).thenReturn(trainerEntity);
    when(authenticationService.register(trainerEntity)).thenReturn(registeredTrainer);
    when(trainerRepository.save(registeredTrainer)).thenReturn(registeredTrainer);
    when(modelMapper.map(registeredTrainer, TrainerDto.class)).thenReturn(savedTrainerDto);

    TrainerDto result = trainerService.createTrainer(trainerDto);

    Assertions.assertThat(result).isSameAs(savedTrainerDto);
    verify(authenticationService).register(trainerEntity);
    verify(trainerRepository).save(registeredTrainer);
  }

  @Test
  void updateTrainerShouldPreserveIdAndRegenerateUsernameWhenNameChanges() {
    Long trainerId = 10L;
    TrainerUpdateDto trainerDto = createTrainerUpdateDto();
    Trainer existingTrainer = createTrainer(trainerId, "Old.Name");
    existingTrainer.setFirstName("Old");
    existingTrainer.setLastName("Name");
    TrainerDto savedTrainerDto = createTrainerDto(trainerId, "John.Doe");

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
    when(authenticationService.generateUsername("John", "Doe")).thenReturn("John.Doe");
    when(trainerRepository.save(existingTrainer)).thenReturn(existingTrainer);
    when(modelMapper.map(existingTrainer, TrainerDto.class)).thenReturn(savedTrainerDto);

    TrainerDto result = trainerService.updateTrainer(trainerId, trainerDto);

    Assertions.assertThat(result).isSameAs(savedTrainerDto);
    Assertions.assertThat(existingTrainer.getId()).isEqualTo(trainerId);
    Assertions.assertThat(existingTrainer.getUsername()).isEqualTo("John.Doe");
    verify(authenticationService).generateUsername("John", "Doe");
    verify(trainerRepository).save(existingTrainer);
  }

  @Test
  void updateTrainerShouldKeepUsernameWhenNameDoesNotChange() {
    Long trainerId = 10L;
    TrainerUpdateDto trainerDto = createTrainerUpdateDto();
    Trainer existingTrainer = createTrainer(trainerId, "John.Doe");
    TrainerDto savedTrainerDto = createTrainerDto(trainerId, "John.Doe");

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
    when(trainerRepository.save(existingTrainer)).thenReturn(existingTrainer);
    when(modelMapper.map(existingTrainer, TrainerDto.class)).thenReturn(savedTrainerDto);

    TrainerDto result = trainerService.updateTrainer(trainerId, trainerDto);

    Assertions.assertThat(result).isSameAs(savedTrainerDto);
    Assertions.assertThat(existingTrainer.getUsername()).isEqualTo("John.Doe");
    verifyNoInteractions(authenticationService);
  }

  @Test
  void updateTrainerShouldThrowExceptionWhenTrainerDoesNotExist() {
    Long trainerId = 404L;
    TrainerUpdateDto trainerDto = createTrainerUpdateDto();

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> trainerService.updateTrainer(trainerId, trainerDto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Trainer not found: " + trainerId);

    verifyNoInteractions(authenticationService, modelMapper);
  }

  @Test
  void selectTrainerShouldReturnMappedDtoWhenTrainerExists() {
    Long trainerId = 10L;
    Trainer trainer = createTrainer(trainerId, "john.doe");
    TrainerDto trainerDto = createTrainerDto(trainerId, "john.doe");

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(trainer));
    when(modelMapper.map(trainer, TrainerDto.class)).thenReturn(trainerDto);

    Optional<TrainerDto> result = trainerService.selectTrainer(trainerId);

    Assertions.assertThat(result).contains(trainerDto);
  }

  @Test
  void selectTrainerShouldReturnEmptyWhenTrainerDoesNotExist() {
    Long trainerId = 404L;

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.empty());

    Optional<TrainerDto> result = trainerService.selectTrainer(trainerId);

    Assertions.assertThat(result).isEmpty();
    verifyNoInteractions(modelMapper);
  }

  @Test
  void selectTrainerByUsernameShouldReturnMappedDtoWhenTrainerExists() {
    String username = "john.doe";
    Trainer trainer = createTrainer(10L, username);
    TrainerDto trainerDto = createTrainerDto(10L, username);

    when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
    when(modelMapper.map(trainer, TrainerDto.class)).thenReturn(trainerDto);

    Optional<TrainerDto> result = trainerService.selectTrainerByUsername(username);

    Assertions.assertThat(result).contains(trainerDto);
  }

  @Test
  void selectAllTrainersShouldReturnMappedDtos() {
    Trainer firstTrainer = createTrainer(1L, "first.trainer");
    Trainer secondTrainer = createTrainer(2L, "second.trainer");
    TrainerDto firstDto = createTrainerDto(1L, "first.trainer");
    TrainerDto secondDto = createTrainerDto(2L, "second.trainer");

    when(trainerRepository.findAll()).thenReturn(List.of(firstTrainer, secondTrainer));
    when(modelMapper.map(firstTrainer, TrainerDto.class)).thenReturn(firstDto);
    when(modelMapper.map(secondTrainer, TrainerDto.class)).thenReturn(secondDto);

    List<TrainerDto> result = trainerService.selectAllTrainers();

    Assertions.assertThat(result).containsExactly(firstDto, secondDto);
  }

  private Trainer createTrainer(Long id, String username) {
    Trainer trainer = new Trainer();
    trainer.setId(id);
    trainer.setFirstName("John");
    trainer.setLastName("Doe");
    trainer.setUsername(username);
    trainer.setPassword("password");
    trainer.setActive(true);
    return trainer;
  }

  private TrainerDto createTrainerDto(Long id, String username) {
    return new TrainerDto(id, "John", "Doe", username, "password", true, null, null);
  }

  private TrainerUpdateDto createTrainerUpdateDto() {
    return new TrainerUpdateDto("John", "Doe", "password", true, null, null);
  }
}

