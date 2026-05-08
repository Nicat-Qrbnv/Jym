package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.Trainee;
import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends UserRepository<Trainee> {

  Trainee save(Trainee trainee);

  Optional<Trainee> findById(Long id);

  Optional<Trainee> findByUsername(String name);

  List<Trainee> findAll();

  void delete(Long id);
}
