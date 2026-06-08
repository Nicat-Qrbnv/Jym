package com.epam.jym.crm.dto.trainee;

import com.epam.jym.crm.dto.trainer.TrainerSummaryDto;
import com.epam.jym.crm.dto.user.UserProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record UpdatedTraineeProfileDto(
    UserProfileDto user,
    @Schema(example = "1995-04-12") LocalDate dateOfBirth,
    @Schema(example = "221B Baker Street") String address,
    List<TrainerSummaryDto> trainers) {}
