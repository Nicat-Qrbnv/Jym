package com.epam.jym.crm.dto.training;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TrainingCreateDto(
    @NotBlank @Size(max = 255) String name,
    @NotBlank @Size(max = 310) String traineeUsername,
    @NotBlank @Size(max = 310) String trainerUsername,
    @NotNull LocalDate date,
    @NotNull @Min(1) Integer durationInMinutes) {}
