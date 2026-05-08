package com.epam.jym.crm.repository.database;

import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.TrainingType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TableCreator {

  @Bean
  public Table<Trainee> createTraineeTable() {
    return new Table<>() {};
  }

  @Bean
  public Table<Trainer> createTrainerTable() {
    return new Table<>() {};
  }

  @Bean
  public Table<Training> createTrainingTable() {
    return new Table<>() {};
  }

  @Bean
  public Table<TrainingType> createTrainingTypeTable() {
    return new Table<>() {};
  }
}
