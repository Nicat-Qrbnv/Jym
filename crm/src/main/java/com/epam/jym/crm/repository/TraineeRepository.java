package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.Trainee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

  Optional<Trainee> findByUserUsername(String username);

  @Query(
      """
          SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
          FROM Trainee t
          WHERE t.user.id = :userId
          """)
  boolean userHasTraineeProfile(Long userId);
}
