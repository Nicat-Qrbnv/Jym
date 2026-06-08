package com.epam.jym.crm.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;

public record TraineeTrainingDto(
    @Schema(example = "John Doe") String traineeFullName, TrainingDto training) {}
