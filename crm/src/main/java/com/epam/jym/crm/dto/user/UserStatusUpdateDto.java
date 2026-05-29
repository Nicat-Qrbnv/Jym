package com.epam.jym.crm.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateDto(
    @NotBlank String username, @NotNull @JsonProperty("isActive") Boolean isActive) {}
