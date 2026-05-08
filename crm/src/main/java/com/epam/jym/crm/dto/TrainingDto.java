package com.epam.jym.crm.dto;

import java.time.LocalDate;

public record TrainingDto(
    Long id,
    String name,
    TrainingTypeDto type,
    TraineeDto trainee,
    TrainerDto trainer,
    LocalDate date,
    int durationInMinutes) {}
