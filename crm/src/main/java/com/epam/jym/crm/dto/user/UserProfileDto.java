package com.epam.jym.crm.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserProfileDto(
    @Schema(example = "john.doe") String username,
    @Schema(example = "John") String firstName,
    @Schema(example = "Doe") String lastName) {}
