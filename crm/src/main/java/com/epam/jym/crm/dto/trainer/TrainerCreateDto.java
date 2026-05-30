package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record TrainerCreateDto(
    @NotNull @Valid UserCreateDto profile, @Valid TrainingTypeDto specialization) {}
