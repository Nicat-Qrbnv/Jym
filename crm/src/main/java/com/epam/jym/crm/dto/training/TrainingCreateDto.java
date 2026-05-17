package com.epam.jym.crm.dto.training;

import java.time.LocalDate;

public record TrainingCreateDto(
    String name,
    Long typeId,
    Long traineeId,
    Long trainerId,
    LocalDate date,
    int durationInMinutes) {}
