package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.Trainer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

  @Query(
      """
          SELECT t
          FROM Trainer t
          LEFT JOIN FETCH User u
          WHERE u.username IN (:usernames)
          """)
  List<Trainer> findByUsernames(List<String> usernames);

  @Query(
      """
          SELECT t
          FROM Trainer t
          LEFT JOIN FETCH User u
          WHERE u.username = :username
          """)
  Optional<Trainer> findByUsername(String username);

  @Query(
      """
          SELECT trainer
          FROM Trainer trainer
          WHERE trainer.id NOT IN (
            SELECT assignedTrainer.id
            FROM Trainee trainee
            JOIN trainee.trainers assignedTrainer
            WHERE trainee.user.username = :traineeUsername
          )
          """)
  List<Trainer> findTrainersNotAssignedToTrainee(String traineeUsername);

  @Query(
      """
          SELECT t.id
          FROM Trainer t
          WHERE LOWER(t.user.username) LIKE LOWER(:name)
          """)
  List<Long> findIdsByNameContaining(String name);
}
