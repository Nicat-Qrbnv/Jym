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
          LEFT JOIN FETCH t.user u
          WHERE u.username = :username
          """)
  Optional<Trainee> findTraineeByUsername(String username);

  @Query(
      """
          SELECT t
          FROM Trainee t
          LEFT JOIN FETCH t.user u
          LEFT JOIN FETCH t.trainings tr
          LEFT JOIN FETCH tr.trainer trainer
          LEFT JOIN FETCH trainer.user
          WHERE u.username = :username
          """)
  Optional<Trainee> findTraineeWithTrainingsByUsername(String username);

  @Query(
      """
          SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
          FROM Trainee t
          LEFT JOIN t.user u
          WHERE u.username = :traineeUsername
          """)
  boolean existsByUsername(String traineeUsername);
}
