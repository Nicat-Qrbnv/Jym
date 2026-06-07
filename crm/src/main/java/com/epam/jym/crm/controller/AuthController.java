package com.epam.jym.crm.controller;

import static com.epam.jym.crm.util.CredentialsHeaderParser.parse;

import com.epam.jym.crm.dto.user.PasswordUpdateDto;
import com.epam.jym.crm.facade.CrmFacade;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and password management")
public class AuthController {

  private final CrmFacade crmFacade;

  @GetMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Log in",
      description = "Validates user credentials from the Authorization header.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Credentials are valid",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Credentials are missing or invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public void login(@RequestHeader("Authorization") String userCredentials) {
    crmFacade.login(parse(userCredentials));
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
      @RequestHeader("Authorization") String userCredentials,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Old and new password",
              required = true)
          @Valid
          @RequestBody
          PasswordUpdateDto passwordUpdateDto) {
    crmFacade.changeLogin(parse(userCredentials), passwordUpdateDto);
  }
}
