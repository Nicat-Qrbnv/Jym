package com.epam.jym.crm.repository.impl;

import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.repository.database.Table;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {

  private final Table<Trainer> trainerTable;

  public TrainerRepositoryImpl(Table<Trainer> trainerTable) {
    this.trainerTable = trainerTable;
  }

  @Override
  public Trainer save(Trainer trainer) {
    requireNotNull(trainer);
    trainerTable.write(trainer);
    return trainer;
  }

  @Override
  public Optional<Trainer> findById(Long id) {
    requireId(id);
    return trainerTable.getById(id);
  }

  @Override
  public Optional<Trainer> findByUsername(String name) {
    requireName(name);
    return trainerTable.getByName(name);
  }

  @Override
  public List<Trainer> findAll() {
    return List.copyOf(trainerTable.getAll());
  }

  @Override
  public void delete(Long id) {
    requireId(id);
    trainerTable.delete(id);
  }
}
