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
          LEFT JOIN FETCH User u
          WHERE u.username = :username
          """)
  Optional<Trainee> findTraineeByUsername(String username);

  @Query(
      """
          SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
          FROM Trainee t
          LEFT JOIN User u
          WHERE u.username = :traineeUsername
          """)
  boolean existsByUsername(String traineeUsername);
}
