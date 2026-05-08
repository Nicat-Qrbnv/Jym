package com.epam.jym.crm.repository.impl;

import com.epam.jym.crm.entity.Trainee;
import com.epam.jym.crm.repository.TraineeRepository;
import com.epam.jym.crm.repository.database.Table;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {

  private final Table<Trainee> traineeTable;

  public TraineeRepositoryImpl(Table<Trainee> traineeTable) {
    this.traineeTable = traineeTable;
  }

  @Override
  public Trainee save(Trainee trainee) {
    requireNotNull(trainee);
    traineeTable.write(trainee);
    return trainee;
  }

  @Override
  public Optional<Trainee> findById(Long id) {
    requireId(id);
    return traineeTable.getById(id);
  }

  @Override
  public Optional<Trainee> findByUsername(String name) {
    requireName(name);
    return traineeTable.getByName(name);
  }

  @Override
  public List<Trainee> findAll() {
    return List.copyOf(traineeTable.getAll());
  }

  @Override
  public void delete(Long id) {
    requireId(id);
    traineeTable.delete(id);
  }
}
