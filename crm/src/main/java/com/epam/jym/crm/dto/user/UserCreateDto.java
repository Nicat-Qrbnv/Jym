package com.epam.jym.crm.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request user profile data")
public record UserCreateDto(
    @Schema(description = "First name", example = "John") @NotBlank @Size(max = 150)
        String firstName,
    @Schema(description = "Last name", example = "Doe") @NotBlank @Size(max = 150)
        String lastName) {}
