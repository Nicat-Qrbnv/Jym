package com.epam.jym.crm.dto.training;

import com.epam.jym.crm.validation.annotation.Username;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "Request to create a training")
public record TrainingCreateDto(
    @Schema(description = "Training name", example = "Strength training") @NotBlank @Size(max = 255)
        String name,
    @Schema(description = "Trainee username", example = "john.doe") @Username
        String traineeUsername,
    @Schema(description = "Trainer username", example = "jane.smith") @Username
        String trainerUsername,
    @Schema(description = "Training date", example = "2026-06-01") @NotNull LocalDate date,
    @Schema(description = "Training duration in minutes", example = "60") @NotNull @Min(1)
        Integer durationInMinutes) {}
