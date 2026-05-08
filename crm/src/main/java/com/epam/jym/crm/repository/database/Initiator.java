package com.epam.jym.crm.repository.database;

import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Initiator {

  private final Table<Trainee> traineeTable;
  private final Table<Trainer> trainerTable;
  private final Table<Training> trainingTable;
  private final Table<TrainingType> trainingTypeTable;

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

  @PostConstruct
  public void init() {
    TrainingType technicalTrainingType = TrainingType.builder().id(1L).name("TECHNICAL").build();
    TrainingType nonTechnicalTrainingType =
        TrainingType.builder().id(2L).name("NON_TECHNICAL").build();
    TrainingType advancedTechnicalTrainingType =
        TrainingType.builder().id(3L).name("TECHNICAL").build();

    List<TrainingType> trainingTypes =
        List.of(technicalTrainingType, nonTechnicalTrainingType, advancedTechnicalTrainingType);
    trainingTypes.forEach(trainingTypeTable::write);

    Trainer trainer1 =
        Trainer.builder()
            .id(101L)
            .firstName("John")
            .lastName("Smith")
            .username("john.smith")
            .password("password1")
            .isActive(true)
            .specialization(technicalTrainingType)
            .build();
    Trainer trainer2 =
        Trainer.builder()
            .id(102L)
            .firstName("Emma")
            .lastName("Brown")
            .username("emma.brown")
            .password("password2")
            .isActive(true)
            .specialization(nonTechnicalTrainingType)
            .build();
    Trainer trainer3 =
        Trainer.builder()
            .id(103L)
            .firstName("Michael")
            .lastName("Johnson")
            .username("michael.johnson")
            .password("password3")
            .isActive(true)
            .specialization(advancedTechnicalTrainingType)
            .build();
    Trainer trainer4 =
        Trainer.builder()
            .id(104L)
            .firstName("Sophia")
            .lastName("Davis")
            .username("sophia.davis")
            .password("password4")
            .isActive(true)
            .specialization(technicalTrainingType)
            .build();
    Trainer trainer5 =
        Trainer.builder()
            .id(105L)
            .firstName("David")
            .lastName("Wilson")
            .username("david.wilson")
            .password("password5")
            .isActive(true)
            .specialization(nonTechnicalTrainingType)
            .build();

    List<Trainer> trainers = List.of(trainer1, trainer2, trainer3, trainer4, trainer5);
    trainers.forEach(trainerTable::write);

    Trainee trainee1 =
        Trainee.builder()
            .id(201L)
            .firstName("Alice")
            .lastName("Green")
            .username("alice.green")
            .password("password6")
            .isActive(true)
            .dateOfBirth(LocalDate.of(1998, 3, 14))
            .address("12 Oak Street")
            .build();
    Trainee trainee2 =
        Trainee.builder()
            .id(202L)
            .firstName("Bob")
            .lastName("Miller")
            .username("bob.miller")
            .password("password7")
            .isActive(true)
            .dateOfBirth(LocalDate.of(1997, 7, 22))
            .address("45 Pine Avenue")
            .build();
    Trainee trainee3 =
        Trainee.builder()
            .id(203L)
            .firstName("Carol")
            .lastName("Taylor")
            .username("carol.taylor")
            .password("password8")
            .isActive(true)
            .dateOfBirth(LocalDate.of(1999, 1, 9))
            .address("78 Maple Road")
            .build();
    Trainee trainee4 =
        Trainee.builder()
            .id(204L)
            .firstName("Daniel")
            .lastName("Anderson")
            .username("daniel.anderson")
            .password("password9")
            .isActive(true)
            .dateOfBirth(LocalDate.of(1996, 11, 30))
            .address("90 Cedar Lane")
            .build();
    Trainee trainee5 =
        Trainee.builder()
            .id(205L)
            .firstName("Eva")
            .lastName("Thomas")
            .username("eva.thomas")
            .password("password10")
            .isActive(true)
            .dateOfBirth(LocalDate.of(1998, 5, 18))
            .address("23 Birch Drive")
            .build();
    Trainee trainee6 =
        Trainee.builder()
            .id(206L)
            .firstName("Frank")
            .lastName("Moore")
            .username("frank.moore")
            .password("password11")
            .isActive(true)
            .dateOfBirth(LocalDate.of(1995, 8, 4))
            .address("56 Walnut Street")
            .build();
    Trainee trainee7 =
        Trainee.builder()
            .id(207L)
            .firstName("Grace")
            .lastName("Martin")
            .username("grace.martin")
            .password("password12")
            .isActive(true)
            .dateOfBirth(LocalDate.of(2000, 2, 27))
            .address("34 Cherry Court")
            .build();
    Trainee trainee8 =
        Trainee.builder()
            .id(208L)
            .firstName("Henry")
            .lastName("Lee")
            .username("henry.lee")
            .password("password13")
            .isActive(true)
            .dateOfBirth(LocalDate.of(1997, 9, 15))
            .address("67 Spruce Way")
            .build();
    Trainee trainee9 =
        Trainee.builder()
            .id(209L)
            .firstName("Ivy")
            .lastName("Walker")
            .username("ivy.walker")
            .password("password14")
            .isActive(true)
            .dateOfBirth(LocalDate.of(1999, 12, 6))
            .address("89 Ash Boulevard")
            .build();
    Trainee trainee10 =
        Trainee.builder()
            .id(210L)
            .firstName("Jack")
            .lastName("Hall")
            .username("jack.hall")
            .password("password15")
            .isActive(true)
            .dateOfBirth(LocalDate.of(1996, 4, 21))
            .address("10 Elm Place")
            .build();

    List<Trainee> trainees =
        List.of(
            trainee1, trainee2, trainee3, trainee4, trainee5, trainee6, trainee7, trainee8,
            trainee9, trainee10);
    trainees.forEach(traineeTable::write);

    List<Training> trainings =
        List.of(
            Training.builder()
                .id(301L)
                .name("Java Basics")
                .type(technicalTrainingType)
                .trainee(trainee1)
                .trainer(trainer1)
                .date(LocalDate.of(2026, 1, 12))
                .durationInMinutes(60)
                .build(),
            Training.builder()
                .id(302L)
                .name("Spring Boot Fundamentals")
                .type(technicalTrainingType)
                .trainee(trainee2)
                .trainer(trainer1)
                .date(LocalDate.of(2026, 1, 15))
                .durationInMinutes(90)
                .build(),
            Training.builder()
                .id(303L)
                .name("Presentation Skills")
                .type(nonTechnicalTrainingType)
                .trainee(trainee3)
                .trainer(trainer2)
                .date(LocalDate.of(2026, 1, 19))
                .durationInMinutes(60)
                .build(),
            Training.builder()
                .id(304L)
                .name("Team Collaboration")
                .type(nonTechnicalTrainingType)
                .trainee(trainee4)
                .trainer(trainer2)
                .date(LocalDate.of(2026, 1, 22))
                .durationInMinutes(75)
                .build(),
            Training.builder()
                .id(305L)
                .name("AWS Essentials")
                .type(advancedTechnicalTrainingType)
                .trainee(trainee5)
                .trainer(trainer3)
                .date(LocalDate.of(2026, 1, 26))
                .durationInMinutes(120)
                .build(),
            Training.builder()
                .id(306L)
                .name("Docker Workshop")
                .type(advancedTechnicalTrainingType)
                .trainee(trainee6)
                .trainer(trainer3)
                .date(LocalDate.of(2026, 1, 29))
                .durationInMinutes(90)
                .build(),
            Training.builder()
                .id(307L)
                .name("SQL Query Practice")
                .type(technicalTrainingType)
                .trainee(trainee7)
                .trainer(trainer4)
                .date(LocalDate.of(2026, 2, 2))
                .durationInMinutes(60)
                .build(),
            Training.builder()
                .id(308L)
                .name("Database Design")
                .type(technicalTrainingType)
                .trainee(trainee8)
                .trainer(trainer4)
                .date(LocalDate.of(2026, 2, 5))
                .durationInMinutes(90)
                .build(),
            Training.builder()
                .id(309L)
                .name("Business Communication")
                .type(nonTechnicalTrainingType)
                .trainee(trainee9)
                .trainer(trainer5)
                .date(LocalDate.of(2026, 2, 9))
                .durationInMinutes(60)
                .build(),
            Training.builder()
                .id(310L)
                .name("Conflict Management")
                .type(nonTechnicalTrainingType)
                .trainee(trainee10)
                .trainer(trainer5)
                .date(LocalDate.of(2026, 2, 12))
                .durationInMinutes(75)
                .build());
    trainings.forEach(trainingTable::write);

    trainer1.setTraining(trainings.get(0));
    trainer2.setTraining(trainings.get(2));
    trainer3.setTraining(trainings.get(4));
    trainer4.setTraining(trainings.get(6));
    trainer5.setTraining(trainings.get(8));
  }
}
