package com.epam.jym.crm.repository.impl;

import com.epam.jym.crm.entity.TrainingType;
import com.epam.jym.crm.repository.TrainingTypeRepository;
import com.epam.jym.crm.repository.database.Table;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {

  private final Table<TrainingType> trainingTypeTable;

  public TrainingTypeRepositoryImpl(Table<TrainingType> trainingTypeTable) {
    this.trainingTypeTable = trainingTypeTable;
  }

  @Override
  public TrainingType save(TrainingType trainingType) {
    requireNotNull(trainingType);
    trainingTypeTable.write(trainingType);
    return trainingType;
  }

  @Override
  public Optional<TrainingType> findById(Long id) {
    requireId(id);
    return trainingTypeTable.getById(id);
  }

  @Override
  public List<TrainingType> findAll() {
    return List.copyOf(trainingTypeTable.getAll());
  }

  @Override
  public void delete(Long id) {
    requireId(id);
    trainingTypeTable.delete(id);
  }
}
