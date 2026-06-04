package com.epam.jym.crm.dto.trainee;

import com.epam.jym.crm.dto.user.UserCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "Request to create a trainee profile")
public record TraineeCreateDto(
    @Schema(description = "Trainee personal data") @NotNull @Valid UserCreateDto profile,
    @Schema(description = "Trainee date of birth", example = "1995-04-12") LocalDate dateOfBirth,
    @Schema(description = "Trainee address", example = "221B Baker Street") @Size(max = 255)
        String address) {}
