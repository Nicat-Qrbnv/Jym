package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record TrainerSummaryDto(
    @Schema(description = "Basic trainer user profile") UserProfileDto profile,
    @Schema(description = "Trainer specialization training type") TrainingTypeDto specialization) {}
