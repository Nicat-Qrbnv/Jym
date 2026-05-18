package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.Trainer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

  Optional<Trainer> findByUserUsername(String username);

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
          SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
          FROM Trainer t
          WHERE t.user.id = :userId
          """)
  boolean userHasTrainerProfile(Long userId);
}
