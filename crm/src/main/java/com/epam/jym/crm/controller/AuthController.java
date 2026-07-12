package com.epam.jym.crm.controller;

import com.epam.jym.crm.dto.user.AuthTokenDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.user.PasswordUpdateDto;
import com.epam.jym.crm.facade.CrmFacade;
import com.epam.jym.jwthandler.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and password management")
public class AuthController {

  private final CrmFacade crmFacade;
  private final JwtService jwtService;

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Log in",
      description = "Validates user credentials and returns a JWT token.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Credentials are valid",
            content = @Content(schema = @Schema(implementation = AuthTokenDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Credentials are missing or invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public AuthTokenDto login(@Valid @RequestBody CredentialsDto credentials) {
    crmFacade.login(credentials);
    return new AuthTokenDto(
        jwtService.generateToken(credentials.username()), jwtService.expirationSeconds());
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Log out", description = "Revokes the current JWT token.")
  public void logout(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
          String authorizationHeader) {
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwtService.revokeToken(authorizationHeader.substring("Bearer ".length()));
    }
  }

  @PutMapping("/change-password")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Change password",
      description = "Changes the password for the authenticated user using old and new passwords.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Password changed", content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "Password update request is invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Credentials are missing or invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public void changeLogin(
      @RequestHeader(value = "X-Authenticated-User", required = false) String username,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Old and new password",
              required = true)
          @Valid
          @RequestBody
          PasswordUpdateDto passwordUpdateDto) {
    if (username == null || username.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authenticated user");
    }
    crmFacade.changeLogin(username, passwordUpdateDto);
  }
}
