package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record TrainerUpdateDto(
    @NotNull @Valid UserDto profile, @Valid TrainingTypeDto specialization) {}
