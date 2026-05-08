package com.epam.jym.crm.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.TrainerDto;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.service.AuthenticationService;
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
class TrainerServiceImplTest {

  @Mock
  private TrainerRepository trainerRepository;

  @Mock
  private AuthenticationService authenticationService;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private TrainerServiceImpl trainerService;

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
  void updateTrainerShouldSaveMappedEntityWithRequestedId() {
    Long trainerId = 10L;
    TrainerDto trainerDto = createTrainerDto(null, "updated.trainer");
    Trainer existingTrainer = createTrainer(trainerId, "old.trainer");
    Trainer mappedTrainer = createTrainer(null, "updated.trainer");
    Trainer savedTrainer = createTrainer(trainerId, "updated.trainer");
    TrainerDto savedTrainerDto = createTrainerDto(trainerId, "updated.trainer");

    when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
    when(modelMapper.map(trainerDto, Trainer.class)).thenReturn(mappedTrainer);
    when(trainerRepository.save(mappedTrainer)).thenReturn(savedTrainer);
    when(modelMapper.map(savedTrainer, TrainerDto.class)).thenReturn(savedTrainerDto);

    TrainerDto result = trainerService.updateTrainer(trainerId, trainerDto);

    Assertions.assertThat(result).isSameAs(savedTrainerDto);
    Assertions.assertThat(mappedTrainer.getId()).isEqualTo(trainerId);
    verify(trainerRepository).save(mappedTrainer);
  }

  @Test
  void updateTrainerShouldThrowExceptionWhenTrainerDoesNotExist() {
    Long trainerId = 404L;
    TrainerDto trainerDto = createTrainerDto(null, "missing.trainer");

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
}

