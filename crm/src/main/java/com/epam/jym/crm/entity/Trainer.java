package com.epam.jym.crm.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@Setter
@SuperBuilder
public class Trainer extends User {

  private Training training;
  private TrainingType specialization;
}
