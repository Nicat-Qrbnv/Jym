package com.epam.jym.crm.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TrainingTypeDto(@NotNull @Positive Long id, @NotBlank String name) {}
