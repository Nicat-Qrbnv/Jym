package com.epam.jym.crm.entity;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@Setter
@SuperBuilder
public class Trainee extends User {

  private LocalDate dateOfBirth;
  private String address;
}
