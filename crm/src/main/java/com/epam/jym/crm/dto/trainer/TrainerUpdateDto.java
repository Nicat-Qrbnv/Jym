package com.epam.jym.crm.dto.trainer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TrainerUpdateDto(@NotNull @Positive Long specializationId) {}
