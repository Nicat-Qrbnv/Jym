package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update a trainer profile")
public record TrainerUpdateDto(
    @Schema(description = "Updated trainer user data") @NotNull @Valid UserDto profile,
    @Schema(description = "Trainer specialization") @Valid TrainingTypeDto specialization) {}
