package com.epam.jym.crm.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication token response")
public record AuthTokenDto(
    @Schema(description = "JWT token to send as Bearer authorization") String token,
    @Schema(description = "Token lifetime in seconds") long expiresIn) {}
