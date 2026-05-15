package com.epam.jym.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "username"})
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 255)
  @Column(name = "first_name", nullable = false)
  private String firstName;

  @NotBlank
  @Size(max = 255)
  @Column(name = "last_name", nullable = false)
  private String lastName;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false, unique = true)
  private String username;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false)
  private String password;

  @Column(name = "is_active", nullable = false)
  private boolean isActive;
}
