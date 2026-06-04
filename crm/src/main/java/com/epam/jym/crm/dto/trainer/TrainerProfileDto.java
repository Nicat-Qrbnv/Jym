package com.epam.jym.crm.dto.trainer;

import com.epam.jym.crm.dto.trainee.TraineeSummaryDto;
import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.dto.user.UserDto;
import com.epam.jym.crm.dto.user.UserProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record TrainerProfileDto(
    UserDto profile,
    @Schema(example = "{\"id\":1,\"name\":\"Strength\"}") TrainingTypeDto specialization,
    List<TraineeSummaryDto> trainees) {}
