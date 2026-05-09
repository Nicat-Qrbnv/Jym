package com.epam.jym.crm.repository.impl;

import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.repository.TrainingRepository;
import com.epam.jym.crm.repository.database.Table;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {

  private final Table<Training> trainingTable;

  public TrainingRepositoryImpl(Table<Training> trainingTable) {
    this.trainingTable = trainingTable;
  }

  @Override
  public Training save(Training training) {
    requireNotNull(training);
    trainingTable.write(training);
    return training;
  }

  @Override
  public Optional<Training> findById(Long id) {
    requireId(id);
    return trainingTable.getById(id);
  }

  @Override
  public List<Training> findAll() {
    return List.copyOf(trainingTable.getAll());
  }

  @Override
  public void delete(Long id) {
    requireId(id);
    trainingTable.delete(id);
  }
}
