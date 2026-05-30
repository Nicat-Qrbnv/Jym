package com.epam.jym.crm.dto.user;

import jakarta.validation.constraints.NotBlank;

public record CredentialsDto(@NotBlank String username, @NotBlank String password) {}
