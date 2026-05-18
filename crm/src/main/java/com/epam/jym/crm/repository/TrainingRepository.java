package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.Training;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

  @Query(
      """
      SELECT training
      FROM Training training
      JOIN training.trainee trainee
      JOIN trainee.user traineeUser
      JOIN training.trainer trainer
      JOIN trainer.user trainerUser
      JOIN training.type type
      WHERE traineeUser.username = :traineeUsername
        AND (:fromDate IS NULL OR training.scheduledDate >= :fromDate)
        AND (:toDate IS NULL OR training.scheduledDate <= :toDate)
        AND (:trainerName IS NULL
          OR LOWER(CONCAT(trainerUser.firstName, ' ', trainerUser.lastName))
            LIKE LOWER(CONCAT('%', :trainerName, '%')))
        AND (:trainingType IS NULL OR type.name = :trainingType)
      """)
  List<Training> findTraineeTrainings(
      @Param("traineeUsername") String traineeUsername,
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("trainerName") String trainerName,
      @Param("trainingType") String trainingType);

  @Query(
      """
      SELECT training
      FROM Training training
      JOIN training.trainee trainee
      JOIN trainee.user traineeUser
      JOIN training.trainer trainer
      JOIN trainer.user trainerUser
      WHERE trainerUser.username = :trainerUsername
        AND (:fromDate IS NULL OR training.scheduledDate >= :fromDate)
        AND (:toDate IS NULL OR training.scheduledDate <= :toDate)
        AND (:traineeName IS NULL
          OR LOWER(CONCAT(traineeUser.firstName, ' ', traineeUser.lastName))
            LIKE LOWER(CONCAT('%', :traineeName, '%')))
      """)
  List<Training> findTrainerTrainings(
      @Param("trainerUsername") String trainerUsername,
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("traineeName") String traineeName);
}
