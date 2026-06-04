package com.epam.jym.crm.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record TrainingDto(
    @Schema(example = "Strength training") String trainingName,
    @Schema(example = "2026-06-01") LocalDate trainingDate,
    @Schema(example = "Strength") String trainingType,
    @Schema(example = "60") int trainingDuration) {}
