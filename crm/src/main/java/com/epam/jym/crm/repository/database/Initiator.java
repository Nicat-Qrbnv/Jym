package com.epam.jym.crm.repository.database;

import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Getter
@Component
public class Initiator {

  private final Table<Trainee> traineeTable;
  private final Table<Trainer> trainerTable;
  private final Table<Training> trainingTable;
  private final Table<TrainingType> trainingTypeTable;
  private ObjectMapper objectMapper;
  private Resource seedDataResource;

  public Initiator(
      Table<Trainee> traineeTable,
      Table<Trainer> trainerTable,
      Table<Training> trainingTable,
      Table<TrainingType> trainingTypeTable) {
    this.traineeTable = traineeTable;
    this.trainerTable = trainerTable;
    this.trainingTable = trainingTable;
    this.trainingTypeTable = trainingTypeTable;
  }

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Value("${seed-data.location}")
  public void setSeedDataResource(Resource seedDataResource) {
    this.seedDataResource = seedDataResource;
  }

  @PostConstruct
  public void init() {
    SeedData seedData = readSeedData();

    Map<Long, TrainingType> trainingTypes = createTrainingTypes(seedData.trainingTypes());
    Map<Long, Trainee> trainees = createTrainees(seedData.trainees());
    Map<Long, Trainer> trainers = createTrainers(seedData.trainers(), trainingTypes);

    createTrainings(seedData.trainings(), trainingTypes, trainees, trainers);
  }

  private SeedData readSeedData() {
    try (InputStream inputStream = seedDataResource.getInputStream()) {
      return objectMapper.readValue(inputStream, SeedData.class);
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to read seed data", exception);
    }
  }

  private Map<Long, TrainingType> createTrainingTypes(List<TrainingTypeSeed> seeds) {
    Map<Long, TrainingType> trainingTypes =
        seeds.stream()
            .map(seed -> TrainingType.builder().id(seed.id()).name(seed.name()).build())
            .collect(Collectors.toMap(TrainingType::getId, Function.identity()));

    trainingTypes.values().forEach(trainingTypeTable::write);
    return trainingTypes;
  }

  private Map<Long, Trainee> createTrainees(List<TraineeSeed> seeds) {
    Map<Long, Trainee> trainees =
        seeds.stream()
            .map(
                seed ->
                    Trainee.builder()
                        .id(seed.id())
                        .firstName(seed.firstName())
                        .lastName(seed.lastName())
                        .username(seed.username())
                        .password(seed.password())
                        .isActive(seed.active())
                        .dateOfBirth(seed.dateOfBirth())
                        .address(seed.address())
                        .build())
            .collect(Collectors.toMap(Trainee::getId, Function.identity()));

    trainees.values().forEach(traineeTable::write);
    return trainees;
  }

  private Map<Long, Trainer> createTrainers(
      List<TrainerSeed> seeds, Map<Long, TrainingType> trainingTypes) {
    Map<Long, Trainer> trainers =
        seeds.stream()
            .map(
                seed ->
                    Trainer.builder()
                        .id(seed.id())
                        .firstName(seed.firstName())
                        .lastName(seed.lastName())
                        .username(seed.username())
                        .password(seed.password())
                        .isActive(seed.active())
                        .specialization(getById(trainingTypes, seed.specializationId()))
                        .build())
            .collect(Collectors.toMap(Trainer::getId, Function.identity()));

    trainers.values().forEach(trainerTable::write);
    return trainers;
  }

  private void createTrainings(
      List<TrainingSeed> seeds,
      Map<Long, TrainingType> trainingTypes,
      Map<Long, Trainee> trainees,
      Map<Long, Trainer> trainers) {
    seeds.stream()
        .map(
            seed ->
                Training.builder()
                    .id(seed.id())
                    .name(seed.name())
                    .type(getById(trainingTypes, seed.typeId()))
                    .trainee(getById(trainees, seed.traineeId()))
                    .trainer(getById(trainers, seed.trainerId()))
                    .date(seed.date())
                    .durationInMinutes(seed.durationInMinutes())
                    .build())
        .forEach(this::writeTraining);
  }

  private void writeTraining(Training training) {
    trainingTable.write(training);
    Trainer trainer = training.getTrainer();
    if (trainer.getTraining() == null) {
      trainer.setTraining(training);
    }
  }

  private <T> T getById(Map<Long, T> entities, Long id) {
    T entity = entities.get(id);
    if (entity == null) {
      throw new IllegalStateException("Seed data references missing id: " + id);
    }
    return entity;
  }

  private record SeedData(
      List<TrainingTypeSeed> trainingTypes,
      List<TraineeSeed> trainees,
      List<TrainerSeed> trainers,
      List<TrainingSeed> trainings) {}

  private record TrainingTypeSeed(Long id, String name) {}

  private record TraineeSeed(
      Long id,
      String firstName,
      String lastName,
      String username,
      String password,
      boolean active,
      LocalDate dateOfBirth,
      String address) {}

  private record TrainerSeed(
      Long id,
      String firstName,
      String lastName,
      String username,
      String password,
      boolean active,
      Long specializationId) {}

  private record TrainingSeed(
      Long id,
      String name,
      Long typeId,
      Long traineeId,
      Long trainerId,
      LocalDate date,
      int durationInMinutes) {}
}
