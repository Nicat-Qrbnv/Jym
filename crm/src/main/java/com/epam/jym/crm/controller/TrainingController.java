package com.epam.jym.crm.controller;

import static com.epam.jym.crm.util.CredentialsHeaderParser.parse;

import com.epam.jym.crm.dto.training.TraineeTrainingDto;
import com.epam.jym.crm.dto.training.TraineeTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainerTrainingDto;
import com.epam.jym.crm.dto.training.TrainerTrainingsCriteriaDto;
import com.epam.jym.crm.dto.training.TrainingCreateDto;
import com.epam.jym.crm.facade.CrmFacade;
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
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
@Tag(name = "Trainings", description = "Training creation and training history queries")
public class TrainingController {

  private final CrmFacade crmFacade;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Create training", description = "Creates a training session.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Training created", content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "Request body is invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Credentials are missing or invalid",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Referenced trainee, trainer, or training type was not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
      })
  public void createTraining(
      @Valid @RequestBody TrainingCreateDto trainingDto,
      @RequestHeader("Authorization") String userCredentials) {
    crmFacade.createTraining(parse(userCredentials), trainingDto);
  }

  @GetMapping("/trainer")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get trainer trainings",
      description = "Returns trainings for a trainer filtered by optional period and trainee name.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainer trainings returned",
            content =
                @Content(
                    array =
                        @ArraySchema(schema = @Schema(implementation = TrainerTrainingDto.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Query parameters are invalid",
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
  public List<TrainerTrainingDto> getTrainings(
      @Parameter(description = "Trainer username", required = true) @NotBlank @RequestParam
          String username,
      @Parameter(description = "Start date in ISO format, inclusive")
          @RequestParam(required = false)
          @DateTimeFormat(iso = ISO.DATE)
          LocalDate periodFrom,
      @Parameter(description = "End date in ISO format, inclusive")
          @RequestParam(required = false)
          @DateTimeFormat(iso = ISO.DATE)
          LocalDate periodTo,
      @Parameter(description = "Trainee name filter") @RequestParam(required = false)
          String traineeName,
      @RequestHeader("Authorization") String userCredentials) {
    TrainerTrainingsCriteriaDto criteria =
        new TrainerTrainingsCriteriaDto(periodFrom, periodTo, traineeName);
    return crmFacade.getTrainerTrainings(parse(userCredentials), username, criteria);
  }

  @GetMapping("/trainee")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get trainee trainings",
      description =
          "Returns trainings for a trainee filtered by optional period, trainer name, "
              + "and training type.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainee trainings returned",
            content =
                @Content(
                    array =
                        @ArraySchema(schema = @Schema(implementation = TraineeTrainingDto.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Query parameters are invalid",
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
  public List<TraineeTrainingDto> getTrainings(
      @Parameter(description = "Trainee username", required = true) @NotBlank @RequestParam
          String username,
      @Parameter(description = "Start date in ISO format, inclusive")
          @RequestParam(required = false)
          @DateTimeFormat(iso = ISO.DATE)
          LocalDate periodFrom,
      @Parameter(description = "End date in ISO format, inclusive")
          @RequestParam(required = false)
          @DateTimeFormat(iso = ISO.DATE)
          LocalDate periodTo,
      @Parameter(description = "Trainer name filter") @RequestParam(required = false)
          String trainerName,
      @Parameter(description = "Training type name filter") @RequestParam(required = false)
          String trainingType,
      @RequestHeader("Authorization") String userCredentials) {
    TraineeTrainingsCriteriaDto criteria =
        new TraineeTrainingsCriteriaDto(periodFrom, periodTo, trainerName, trainingType);
    return crmFacade.getTraineeTrainings(parse(userCredentials), username, criteria);
  }
}
