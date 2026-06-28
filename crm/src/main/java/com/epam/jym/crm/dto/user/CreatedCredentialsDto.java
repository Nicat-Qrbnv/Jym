package com.epam.jym.crm.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generated credentials and JWT authentication token")
public record CreatedCredentialsDto(CredentialsDto credentials, AuthTokenDto tokenDetails) {}
