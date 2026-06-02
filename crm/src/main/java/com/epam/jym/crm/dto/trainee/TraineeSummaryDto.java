package com.epam.jym.crm.dto.trainee;

import com.epam.jym.crm.dto.user.UserProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record TraineeSummaryDto(
    @Schema(example = "{\"username\":\"jane.smith\",\"firstName\":\"Jane\",\"lastName\":\"Smith\"}")
        UserProfileDto profile) {}
