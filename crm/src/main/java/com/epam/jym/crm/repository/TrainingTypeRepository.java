package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

  @Query(
      """
          SELECT tt
          FROM TrainingType tt
          WHERE tt.id = :id AND LOWER(tt.name) = LOWER(:name)
          """)
  Optional<TrainingType> findMatchingType(@NotNull @Positive Long id, @NotBlank String name);
}
