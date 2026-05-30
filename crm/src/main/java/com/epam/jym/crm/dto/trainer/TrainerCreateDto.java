package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.user.UserCreateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TrainerCreateDto(
    @NotNull @Valid UserCreateDto profile, @NotNull @Positive Long specializationId) {}
