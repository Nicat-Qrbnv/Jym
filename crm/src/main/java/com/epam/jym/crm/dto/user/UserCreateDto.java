package com.epam.jym.crm.dto.user;

import com.epam.jym.crm.validation.annotation.PersonName;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request user profile data")
public record UserCreateDto(
    @Schema(description = "First name", example = "John") @PersonName String firstName,
    @Schema(description = "Last name", example = "Doe") @PersonName String lastName) {}
