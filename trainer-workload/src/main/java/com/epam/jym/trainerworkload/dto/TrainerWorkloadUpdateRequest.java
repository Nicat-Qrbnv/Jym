package com.epam.jym.trainerworkload.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TrainerWorkloadUpdateRequest(
    @NotBlank String trainerUsername,
    @NotBlank String trainerFirstName,
    @NotBlank String trainerLastName,
    @NotNull Boolean trainerActive,
    @NotNull LocalDate trainingDate,
    @NotNull @Min(1) Integer durationInMinutes,
    @NotNull ActionType actionType,
    @NotNull Long trainingId) {}
