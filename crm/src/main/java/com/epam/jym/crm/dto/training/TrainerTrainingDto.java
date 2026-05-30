package com.epam.jym.crm.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;

public record TrainerTrainingDto(
    @Schema(example = "Jane Smith") String trainerFullName,
    @Schema(
            example =
                "{\"trainingName\":\"Strength training\",\"trainingDate\":\"2026-06-01\","
                    + "\"trainingType\":\"Strength\",\"trainingDuration\":60}")
        TrainingDto training) {}
