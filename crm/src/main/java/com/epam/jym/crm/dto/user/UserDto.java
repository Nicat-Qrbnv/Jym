package com.epam.jym.crm.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDto(
    @NotBlank @Size(max = 150) String firstName,
    @NotBlank @Size(max = 150) String lastName,
    @NotNull @JsonProperty("isActive") boolean isActive) {}
