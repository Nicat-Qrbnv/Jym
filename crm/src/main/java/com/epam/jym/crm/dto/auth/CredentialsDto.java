package com.epam.jym.crm.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record CredentialsDto(@NotBlank String username, @NotBlank String password) {}
