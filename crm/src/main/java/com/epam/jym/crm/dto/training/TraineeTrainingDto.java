package com.epam.jym.crm.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;

public record TraineeTrainingDto(
    @Schema(example = "John Doe") String traineeFullName,
    @Schema(
            example =
                "{\"trainingName\":\"Strength training\",\"trainingDate\":\"2026-06-01\","
                    + "\"trainingType\":\"Strength\",\"trainingDuration\":60}")
        TrainingDto training) {}
