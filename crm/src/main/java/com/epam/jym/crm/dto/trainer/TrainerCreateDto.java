package com.epam.jym.crm.dto.trainer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TrainerCreateDto(
    @NotNull @Positive Long userId, @NotNull @Positive Long specializationId) {}
