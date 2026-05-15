package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.Trainee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

  Optional<Trainee> findByUserUsername(String username);
}
