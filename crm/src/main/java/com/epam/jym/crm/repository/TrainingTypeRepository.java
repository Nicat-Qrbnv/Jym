package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.TrainingType;
import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository extends BaseRepository<TrainingType> {

  TrainingType save(TrainingType trainingType);

  Optional<TrainingType> findById(Long id);

  List<TrainingType> findAll();

  void delete(Long id);
}
