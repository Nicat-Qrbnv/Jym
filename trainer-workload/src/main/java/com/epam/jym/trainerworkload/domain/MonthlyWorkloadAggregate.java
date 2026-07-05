package com.epam.jym.trainerworkload.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MonthlyWorkloadAggregate {

  private String trainerUsername;
  private String trainerFirstName;
  private String trainerLastName;
  private boolean trainerActive;
  private int year;
  private int month;
  private int totalDurationInMinutes;

}
