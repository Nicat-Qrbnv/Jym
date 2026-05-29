package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.Trainee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

  @Query(
      """
          SELECT t
          FROM Trainee t
          LEFT JOIN FETCH User u ON t.user.id = u.id
          WHERE u.username = :username
          """)
  Optional<Trainee> findTraineeByUsername(String username);
}
