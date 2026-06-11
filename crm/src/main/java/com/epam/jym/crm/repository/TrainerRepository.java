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
          LEFT JOIN FETCH t.user u
          WHERE u.username IN (:usernames)
          """)
  List<Trainer> findByUsernames(List<String> usernames);

  @Query(
      """
          SELECT t
          FROM Trainer t
          LEFT JOIN FETCH t.user u
          WHERE u.username = :username
          """)
  Optional<Trainer> findByUsername(String username);

  @Query(
      """
          SELECT specialization.name AS specializationType, COUNT(trainer) AS trainerCount
          FROM Trainer trainer
          JOIN trainer.user user
          JOIN trainer.specialization specialization
          WHERE user.isActive = true
          GROUP BY specialization.name
          ORDER BY specialization.name
          """)
  List<SpecializationTrainerCount> countActiveTrainersBySpecialization();

  @Query(
      """
          SELECT trainer
          FROM Trainer trainer
          JOIN FETCH trainer.user user
          JOIN FETCH trainer.specialization specialization
          WHERE user.isActive = true
          AND NOT EXISTS (
            SELECT 1
            FROM Trainee trainee
            JOIN trainee.trainers assignedTrainer
            WHERE trainee.user.username = :traineeUsername
            AND assignedTrainer = trainer
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
