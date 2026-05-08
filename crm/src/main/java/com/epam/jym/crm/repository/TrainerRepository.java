package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.Trainer;
import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends UserRepository<Trainer> {

  Trainer save(Trainer trainer);

  Optional<Trainer> findById(Long id);

  Optional<Trainer> findByUsername(String name);

  List<Trainer> findAll();

  void delete(Long id);
}
