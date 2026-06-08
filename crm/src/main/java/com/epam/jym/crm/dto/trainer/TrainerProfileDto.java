package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.trainee.TraineeSummaryDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record TrainerProfileDto(
    @Schema(description = "Trainer user profile") UserDto profile,
    @Schema(description = "Trainer specialization training type") TrainingTypeDto specialization,
    @Schema(description = "Trainees assigned to the trainer") List<TraineeSummaryDto> trainees) {}
