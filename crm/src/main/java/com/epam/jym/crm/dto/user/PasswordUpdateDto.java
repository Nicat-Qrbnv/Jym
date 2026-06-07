package com.epam.jym.crm.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Password update request")
public record PasswordUpdateDto(
    @Schema(description = "Current password", example = "currentPassword123")
        @NotBlank
        String oldPassword,
    @Schema(description = "New password", example = "newPassword123")
        @NotBlank
        String newPassword) {}
