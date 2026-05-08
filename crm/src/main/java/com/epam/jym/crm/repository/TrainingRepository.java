package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.Training;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends BaseRepository<Training> {

  Training save(Training training);

  Optional<Training> findById(Long id);

  Optional<Training> findByName(String name);

  List<Training> findAll();

  void delete(Long id);
}
