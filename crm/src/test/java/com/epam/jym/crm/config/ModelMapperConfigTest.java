package com.epam.jym.crm.config;

import com.epam.jym.crm.dto.trainee.TraineeDto;
import com.epam.jym.crm.dto.trainer.TrainerDto;
import com.epam.jym.crm.dto.training.TrainingDto;
import com.epam.jym.crm.dto.user.RegisteredUserDto;
import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.entity.User;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class ModelMapperConfigTest {

  private ModelMapper modelMapper;

  @BeforeEach
  void setUp() {
    modelMapper = new ModelMapperConfig().modelMapper();
  }

  @Test
  void shouldMapUserToRegisteredUserDto() {
    User user = createUser(1L, "John", "Doe", "john.doe");
    user.setPassword("password");

    RegisteredUserDto result = modelMapper.map(user, RegisteredUserDto.class);

    Assertions.assertThat(result)
        .isEqualTo(new RegisteredUserDto(1L, "John", "Doe", "john.doe", "password"));
  }

  @Test
  void shouldMapTraineeToTraineeDtoWithUserFields() {
    Trainee trainee = createTrainee(2L, createUser(1L, "John", "Doe", "john.doe"));

    TraineeDto result = modelMapper.map(trainee, TraineeDto.class);

    Assertions.assertThat(result)
        .isEqualTo(new TraineeDto(2L, 1L, "john.doe", LocalDate.of(2000, 1, 1), "Baku"));
  }

  @Test
  void shouldMapTraineeToTraineeDtoWhenUserIsMissing() {
    Trainee trainee = createTrainee(2L, null);

    TraineeDto result = modelMapper.map(trainee, TraineeDto.class);

    Assertions.assertThat(result)
        .isEqualTo(new TraineeDto(2L, null, null, LocalDate.of(2000, 1, 1), "Baku"));
  }

  @Test
  void shouldMapTrainerToTrainerDtoWithSpecializationAndUserFields() {
    TrainingType specialization = createTrainingType(3L, "Fitness");
    Trainer trainer = createTrainer(4L, createUser(1L, "Jane", "Doe", "jane.doe"), specialization);

    TrainerDto result = modelMapper.map(trainer, TrainerDto.class);

    Assertions.assertThat(result.id()).isEqualTo(4L);
    Assertions.assertThat(result.userId()).isEqualTo(1L);
    Assertions.assertThat(result.username()).isEqualTo("jane.doe");
    Assertions.assertThat(result.specialization().id()).isEqualTo(3L);
    Assertions.assertThat(result.specialization().name()).isEqualTo("Fitness");
  }

  @Test
  void shouldMapTrainerToTrainerDtoWhenNestedEntitiesAreMissing() {
    Trainer trainer = createTrainer(4L, null, null);

    TrainerDto result = modelMapper.map(trainer, TrainerDto.class);

    Assertions.assertThat(result).isEqualTo(new TrainerDto(4L, null, null, null));
  }

  @Test
  void shouldMapTrainingToTrainingDtoWithNestedDtos() {
    TrainingType type = createTrainingType(3L, "Fitness");
    Trainee trainee = createTrainee(2L, createUser(1L, "John", "Doe", "john.doe"));
    Trainer trainer = createTrainer(4L, createUser(5L, "Jane", "Doe", "jane.doe"), type);
    Training training = createTraining(6L, "Java Basics", type, trainee, trainer);

    TrainingDto result = modelMapper.map(training, TrainingDto.class);

    Assertions.assertThat(result.id()).isEqualTo(6L);
    Assertions.assertThat(result.name()).isEqualTo("Java Basics");
    Assertions.assertThat(result.type().id()).isEqualTo(3L);
    Assertions.assertThat(result.type().name()).isEqualTo("Fitness");
    Assertions.assertThat(result.trainee())
        .isEqualTo(new TraineeDto(2L, 1L, "john.doe", LocalDate.of(2000, 1, 1), "Baku"));
    Assertions.assertThat(result.trainer().id()).isEqualTo(4L);
    Assertions.assertThat(result.trainer().userId()).isEqualTo(5L);
    Assertions.assertThat(result.trainer().username()).isEqualTo("jane.doe");
    Assertions.assertThat(result.trainer().specialization().id()).isEqualTo(3L);
    Assertions.assertThat(result.date()).isEqualTo(LocalDate.of(2026, 5, 18));
    Assertions.assertThat(result.durationInMinutes()).isEqualTo(60);
  }

  @Test
  void shouldMapTrainingToTrainingDtoWhenNestedEntitiesAreMissing() {
    Training training = createTraining(6L, "Java Basics", null, null, null);

    TrainingDto result = modelMapper.map(training, TrainingDto.class);

    Assertions.assertThat(result)
        .isEqualTo(
            new TrainingDto(6L, "Java Basics", null, null, null, LocalDate.of(2026, 5, 18), 60));
  }

  private User createUser(Long id, String firstName, String lastName, String username) {
    User user = new User();
    user.setId(id);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(username);
    user.setActive(true);
    return user;
  }

  private Trainee createTrainee(Long id, User user) {
    Trainee trainee = new Trainee();
    trainee.setId(id);
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

  private TrainingType createTrainingType(Long id, String name) {
    TrainingType trainingType = new TrainingType();
    trainingType.setId(id);
    trainingType.setName(name);
    return trainingType;
  }

  private Training createTraining(
      Long id, String name, TrainingType type, Trainee trainee, Trainer trainer) {
    Training training = new Training();
    training.setId(id);
    training.setName(name);
    training.setType(type);
    training.setTrainee(trainee);
    training.setTrainer(trainer);
    training.setScheduledDate(LocalDate.of(2026, 5, 18));
    training.setDurationInMinutes(60);
    return training;
  }
}
