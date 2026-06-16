package com.epam.jym.crm.controller;

import com.epam.jym.crm.dto.user.AuthTokenDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.user.PasswordUpdateDto;
import com.epam.jym.crm.facade.CrmFacade;
import com.epam.jym.crm.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
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
      Authentication authentication,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Old and new password",
              required = true)
          @Valid
          @RequestBody
          PasswordUpdateDto passwordUpdateDto) {
    crmFacade.changeLogin(new CredentialsDto(authentication.getName(), ""), passwordUpdateDto);
  }
}
