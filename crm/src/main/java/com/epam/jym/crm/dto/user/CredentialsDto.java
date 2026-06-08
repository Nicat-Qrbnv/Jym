package com.epam.jym.crm.dto.user;

import com.epam.jym.crm.validation.annotation.Username;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CredentialsDto(
    @Schema(example = "john.doe") @Username String username,
    @Schema(example = "a1B2c3D4e5") @NotBlank String password) {}
