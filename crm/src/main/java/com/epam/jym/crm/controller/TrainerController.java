package com.epam.jym.crm.controller;

import static com.epam.jym.crm.util.CredentialsHeaderParser.parse;

import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.trainer.UpdatedTrainerProfileDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.facade.CrmFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
            content = @Content(schema = @Schema(implementation = CredentialsDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Request body is invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public CredentialsDto register(@Valid @RequestBody TrainerCreateDto request) {
    return crmFacade.createTrainer(request);
  }

  @GetMapping
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
      @Parameter(description = "Trainer username", required = true) @NotBlank @RequestParam
          String username,
      @RequestHeader("Authorization") String userCredentials) {
    return crmFacade.getTrainerProfile(parse(userCredentials), username);
  }

  @PutMapping
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
      @Parameter(description = "Trainer username", required = true)
          @RequestParam
          @NotBlank
          @Size(max = 310)
          String username,
      @Valid @RequestBody TrainerUpdateDto request,
      @RequestHeader("Authorization") String userCredentials) {
    return crmFacade.updateTrainerProfile(parse(userCredentials), username, request);
  }

  @PatchMapping("/change-status")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Change trainer status", description = "Toggles the trainer active status.")
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
      @Parameter(description = "Trainer username", required = true) @Valid @RequestParam
          String username,
      @RequestHeader("Authorization") String userCredentials) {
    crmFacade.changeUserStatus(parse(userCredentials), username);
  }
}
