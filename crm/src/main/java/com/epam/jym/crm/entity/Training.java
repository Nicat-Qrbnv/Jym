package com.epam.jym.crm.entity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
public class Training implements Entity {

  private Long id;
  // Not null
  private String name;
  // Not null
  private TrainingType type;
  // Not null
  private Trainee trainee;
  // Not null
  private Trainer trainer;
  // Not null
  private LocalDate date;
  private int durationInMinutes;
}
