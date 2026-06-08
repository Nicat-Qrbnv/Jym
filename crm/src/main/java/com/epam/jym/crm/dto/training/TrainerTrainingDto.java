package com.epam.jym.crm.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;

public record TrainerTrainingDto(
    @Schema(example = "Jane Smith") String trainerFullName, TrainingDto training) {}
