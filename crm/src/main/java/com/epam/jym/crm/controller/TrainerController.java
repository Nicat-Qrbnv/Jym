package com.epam.jym.crm.controller;

import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.trainer.UpdatedTrainerProfileDto;
import com.epam.jym.crm.dto.user.CreatedCredentialsDto;
import com.epam.jym.crm.facade.CrmFacade;
import com.epam.jym.crm.validation.annotation.Username;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainers", description = "Trainer registration and profile management")
public class TrainerController {

  private final CrmFacade crmFacade;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Register trainer",
      description = "Creates a trainer profile and returns login credentials.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainer registered",
            content = @Content(schema = @Schema(implementation = CreatedCredentialsDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Request body is invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public CreatedCredentialsDto register(@Valid @RequestBody TrainerCreateDto request) {
    return crmFacade.createTrainer(request);
  }

  @GetMapping("/{username}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get trainer profile",
      description = "Returns a trainer profile by username.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainer profile returned",
            content = @Content(schema = @Schema(implementation = TrainerProfileDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Username is invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Credentials are missing or invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Trainer was not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public TrainerProfileDto getProfile(
      @Parameter(description = "Trainer username", required = true) @Username @PathVariable
          String username) {
    return crmFacade.getTrainerProfile(username);
  }

  @PutMapping("/{username}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Update trainer profile", description = "Updates trainer profile details.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainer profile updated",
            content = @Content(schema = @Schema(implementation = UpdatedTrainerProfileDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Request data is invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Credentials are missing or invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Trainer was not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public UpdatedTrainerProfileDto updateProfile(
      @Parameter(description = "Trainer username", required = true) @PathVariable @Username
          String username,
      @Valid @RequestBody TrainerUpdateDto request) {
    return crmFacade.updateTrainerProfile(username, request);
  }

  @PatchMapping("/{username}/change-status")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Change trainer status", description = "Updates the trainer active status.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainer status changed",
            content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "Username is invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Credentials are missing or invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Trainer was not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public void updateStatus(
      @Parameter(description = "Trainer username", required = true) @PathVariable @Username
          String username,
      @Parameter(description = "Whether the trainer status should be changed", required = true)
          @RequestParam
          boolean isActive) {
    crmFacade.changeUserStatus(username, isActive);
  }
}
