package com.epam.jym.crm.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
    @NotBlank @Size(max = 150) String firstName, @NotBlank @Size(max = 150) String lastName) {}
