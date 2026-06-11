package com.epam.jym.crm.controller;

import static com.epam.jym.crm.util.CredentialsHeaderParser.parse;

import com.epam.jym.crm.dto.trainee.TraineeCreateDto;
import com.epam.jym.crm.dto.trainee.TraineeProfileDto;
import com.epam.jym.crm.dto.trainee.TraineeUpdateDto;
import com.epam.jym.crm.dto.trainee.UpdatedTraineeProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerSummaryDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.facade.CrmFacade;
import com.epam.jym.crm.validation.annotation.Username;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainees", description = "Trainee registration and profile management")
public class TraineeController {

  private final CrmFacade crmFacade;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Register trainee",
      description = "Creates a trainee profile and returns login credentials.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainee registered",
            content = @Content(schema = @Schema(implementation = CredentialsDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Request body is invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public CredentialsDto register(@Valid @RequestBody TraineeCreateDto request) {
    return crmFacade.createTrainee(request);
  }

  @GetMapping("/{username}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get trainee profile",
      description = "Returns a trainee profile by username.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainee profile returned",
            content = @Content(schema = @Schema(implementation = TraineeProfileDto.class))),
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
            description = "Trainee was not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public TraineeProfileDto getProfile(
      @Parameter(description = "Trainee username", required = true) @Username @PathVariable
          String username,
      @RequestHeader("Authorization") String userCredentials) {
    return crmFacade.getTraineeProfile(parse(userCredentials), username);
  }

  @GetMapping("/{username}/not-assigned-trainers")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get unassigned active trainers",
      description = "Returns active trainers that are not assigned to the trainee.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainers returned",
            content =
                @Content(
                    array =
                        @ArraySchema(schema = @Schema(implementation = TrainerSummaryDto.class)))),
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
            description = "Trainee was not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public List<TrainerSummaryDto> getNotAssignedActiveTrainers(
      @Parameter(description = "Trainee username", required = true) @Username @PathVariable
          String username,
      @RequestHeader("Authorization") String userCredentials) {
    return crmFacade.getNotAssignedActiveTrainers(parse(userCredentials), username);
  }

  @PutMapping("/{username}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Update trainee profile", description = "Updates trainee profile details.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainee profile updated",
            content = @Content(schema = @Schema(implementation = UpdatedTraineeProfileDto.class))),
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
            description = "Trainee was not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public UpdatedTraineeProfileDto updateProfile(
      @Parameter(description = "Trainee username", required = true) @PathVariable @Username
          String username,
      @Valid @RequestBody TraineeUpdateDto request,
      @RequestHeader("Authorization") String userCredentials) {
    return crmFacade.updateTraineeProfile(parse(userCredentials), username, request);
  }

  @PatchMapping("/{username}/change-status")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Change trainee status", description = "Updates the trainee active status.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainee status changed",
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
            description = "Trainee was not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public void updateStatus(
      @Parameter(description = "Trainee username", required = true) @PathVariable @Username
          String username,
      @Parameter(description = "Whether the trainee status should be changed", required = true)
          @RequestParam
          boolean isActive,
      @RequestHeader("Authorization") String userCredentials) {
    crmFacade.changeUserStatus(parse(userCredentials), username, isActive);
  }

  @PutMapping("/{traineeUsername}/trainers")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Update trainee trainers",
      description = "Replaces the trainee's assigned trainer list.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Assigned trainers updated",
            content =
                @Content(
                    array =
                        @ArraySchema(schema = @Schema(implementation = TrainerSummaryDto.class)))),
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
            description = "Trainee or trainer was not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public List<TrainerSummaryDto> updateTrainers(
      @Parameter(description = "Trainee username", required = true) @PathVariable @Username
          String traineeUsername,
      @RequestBody @NotNull List<@NotBlank @Size(max = 310) String> trainerUsernames,
      @RequestHeader("Authorization") String userCredentials) {
    return crmFacade.updateTraineeTrainers(
        parse(userCredentials), traineeUsername, trainerUsernames);
  }

  @DeleteMapping("/{username}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Delete trainee profile",
      description = "Deletes a trainee profile by username.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Trainee deleted", content = @Content),
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
            description = "Trainee was not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public void deleteProfile(
      @Parameter(description = "Trainee username", required = true) @Username @PathVariable
          String username,
      @RequestHeader("Authorization") String userCredentials) {
    crmFacade.deleteTrainee(parse(userCredentials), username);
  }
}
