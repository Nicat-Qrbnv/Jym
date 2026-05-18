package com.epam.jym.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "type", "scheduledDate"})
@ToString
@Entity
@Table(name = "trainings")
public class Training {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false)
  private String name;

  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "training_type_id", nullable = false)
  private TrainingType type;

  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "trainee_id", nullable = false)
  private Trainee trainee;

  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "trainer_id", nullable = false)
  private Trainer trainer;

  @NotNull
  @Column(name = "scheduled_date", nullable = false)
  private LocalDate scheduledDate;

  @Min(1)
  @Column(name = "duration_in_minutes", nullable = false)
  private int durationInMinutes;
}
