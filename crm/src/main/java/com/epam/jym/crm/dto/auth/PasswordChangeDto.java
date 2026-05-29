package com.epam.jym.crm.dto.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordChangeDto(
    @NotNull @Valid CredentialsDto credentials, @NotBlank String newPassword) {}
