package com.epam.jym.crm.dto.training;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TrainingTypeDto(
    @Schema(example = "1") @NotNull @Positive Long id,
    @Schema(example = "Strength") @NotBlank String name) {}
