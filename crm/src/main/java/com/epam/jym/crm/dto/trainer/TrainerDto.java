package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record TrainerDto(
    @Schema(example = "{\"username\":\"jane.smith\",\"firstName\":\"Jane\",\"lastName\":\"Smith\"}")
        UserProfileDto profile,
    @Schema(example = "{\"id\":1,\"name\":\"Strength\"}") TrainingTypeDto specialization) {}
