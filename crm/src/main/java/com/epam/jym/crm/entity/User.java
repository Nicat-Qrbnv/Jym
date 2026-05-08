package com.epam.jym.crm.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@Setter
@SuperBuilder
public abstract class User implements Entity {

  private Long id;
  // Not null
  private String firstName;
  // Not null
  private String lastName;
  // unique
  // Not null
  private String username;
  // Not null
  private String password;
  private boolean isActive;

  @Override
  public String getName() {
    return username;
  }
}
