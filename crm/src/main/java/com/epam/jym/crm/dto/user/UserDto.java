package com.epam.jym.crm.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request user profile data with status")
public record UserDto(
    @Schema(description = "First name", example = "John") @NotBlank @Size(max = 150)
        String firstName,
    @Schema(description = "Last name", example = "Doe") @NotBlank @Size(max = 150) String lastName,
    @Schema(description = "Whether the user is active", example = "true")
        @NotNull
        @JsonProperty("isActive")
        boolean isActive) {}
