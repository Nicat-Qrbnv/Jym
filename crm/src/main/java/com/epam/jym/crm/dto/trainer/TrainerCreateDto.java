package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to create a trainer profile")
public record TrainerCreateDto(
    @Schema(description = "Trainer personal data") @NotNull @Valid UserCreateDto profile,
    @Schema(description = "Trainer specialization") @Valid TrainingTypeDto specialization) {}
