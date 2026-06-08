package com.epam.jym.crm.dto.user;

import com.epam.jym.crm.validation.annotation.PersonName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request user profile data with status")
public record UserDto(
    @Schema(description = "First name", example = "John") @PersonName String firstName,
    @Schema(description = "Last name", example = "Doe") @PersonName String lastName,
    @Schema(description = "Whether the user is active", example = "true")
        @NotNull
        @JsonProperty("isActive")
        boolean isActive) {}
