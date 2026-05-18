package com.epam.jym.crm.dto.training;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TrainingCreateDto(
    @NotBlank @Size(max = 255) String name,
    @NotNull @Positive Long typeId,
    @NotNull @Positive Long traineeId,
    @NotNull @Positive Long trainerId,
    @NotNull LocalDate date,
    @Min(1) int durationInMinutes) {}
