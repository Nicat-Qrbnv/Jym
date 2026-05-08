package com.epam.jym.crm.service.impl;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.repository.TrainerRepository;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

  private static final int PASSWORD_LENGTH = 10;

  @Mock
  private TrainerRepository trainerRepository;

  @Mock
  private TraineeRepository traineeRepository;

  @InjectMocks
  private AuthenticationServiceImpl authenticationService;

  @Test
  void registerShouldGenerateUsernamePasswordAndIdForTrainee() {
    Trainee trainee = new Trainee();
    trainee.setFirstName("John");
    trainee.setLastName("Doe");

    when(trainerRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());
    when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());

    Trainee registeredTrainee = authenticationService.register(trainee);

    Assertions.assertThat(registeredTrainee).isSameAs(trainee);
    Assertions.assertThat(registeredTrainee.getUsername()).isEqualTo("John.Doe");
    Assertions.assertThat(registeredTrainee.getPassword()).hasSize(PASSWORD_LENGTH);
    Assertions.assertThat(registeredTrainee.getId()).isNotNull();
  }

  @Test
  void registerShouldKeepExistingId() {
    Trainer trainer = new Trainer();
    trainer.setId(99L);
    trainer.setFirstName("Jane");
    trainer.setLastName("Smith");

    when(trainerRepository.findByUsername("Jane.Smith")).thenReturn(Optional.empty());
    when(traineeRepository.findByUsername("Jane.Smith")).thenReturn(Optional.empty());

    Trainer registeredTrainer = authenticationService.register(trainer);

    Assertions.assertThat(registeredTrainer.getId()).isEqualTo(99L);
    Assertions.assertThat(registeredTrainer.getUsername()).isEqualTo("Jane.Smith");
    Assertions.assertThat(registeredTrainer.getPassword()).hasSize(PASSWORD_LENGTH);
  }

  @Test
  void registerShouldAddSuffixWhenUsernameAlreadyExists() {
    Trainee trainee = new Trainee();
    trainee.setFirstName("John");
    trainee.setLastName("Doe");

    Trainer existingTrainer = new Trainer();
    existingTrainer.setFirstName("John");
    existingTrainer.setLastName("Doe");

    Trainee existingTrainee = new Trainee();
    existingTrainee.setFirstName("Alice");
    existingTrainee.setLastName("Doe");

    when(trainerRepository.findByUsername("John.Doe")).thenReturn(Optional.of(existingTrainer));
    when(trainerRepository.findAll()).thenReturn(List.of(existingTrainer));
    when(traineeRepository.findAll()).thenReturn(List.of(existingTrainee));

    Trainee registeredTrainee = authenticationService.register(trainee);

    Assertions.assertThat(registeredTrainee.getUsername()).isEqualTo("John.Doe1");
    Assertions.assertThat(registeredTrainee.getPassword()).hasSize(PASSWORD_LENGTH);
    Assertions.assertThat(registeredTrainee.getId()).isNotNull();
  }

  @Test
  void registerShouldCountMatchingNamesFromBothRepositoriesWhenAddingSuffix() {
    Trainer trainer = new Trainer();
    trainer.setFirstName("John");
    trainer.setLastName("Doe");

    Trainer existingTrainer = new Trainer();
    existingTrainer.setFirstName("John");
    existingTrainer.setLastName("Doe");

    Trainee existingTrainee = new Trainee();
    existingTrainee.setFirstName("john");
    existingTrainee.setLastName("doe");

    when(trainerRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());
    when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(existingTrainee));
    when(trainerRepository.findAll()).thenReturn(List.of(existingTrainer));
    when(traineeRepository.findAll()).thenReturn(List.of(existingTrainee));

    Trainer registeredTrainer = authenticationService.register(trainer);

    Assertions.assertThat(registeredTrainer.getUsername()).isEqualTo("John.Doe2");
  }

  @Test
  void registerShouldThrowExceptionWhenUserIsNull() {
    Assertions.assertThatThrownBy(() -> authenticationService.register((Trainee) null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("user must not be null");

    verifyNoInteractions(trainerRepository, traineeRepository);
  }

  @Test
  void registerShouldThrowExceptionWhenFirstNameIsNull() {
    Trainee trainee = new Trainee();
    trainee.setLastName("Doe");

    Assertions.assertThatThrownBy(() -> authenticationService.register(trainee))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("firstName and lastName must not be null");

    verifyNoInteractions(trainerRepository, traineeRepository);
  }

  @Test
  void registerShouldThrowExceptionWhenLastNameIsNull() {
    Trainee trainee = new Trainee();
    trainee.setFirstName("John");

    Assertions.assertThatThrownBy(() -> authenticationService.register(trainee))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("firstName and lastName must not be null");

    verifyNoInteractions(trainerRepository, traineeRepository);
  }
}

