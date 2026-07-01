package com.epam.jym.crm.controller;

import com.epam.jym.crm.dto.training.TrainingTypeDto;
import com.epam.jym.crm.facade.CrmFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/training-types")
@RequiredArgsConstructor
@Tag(name = "Training Types", description = "Training type reference data")
public class TrainingTypeController {

  private final CrmFacade crmFacade;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Get training types", description = "Returns all available training types.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Training types returned",
            content =
                @Content(
                    array = @ArraySchema(schema = @Schema(implementation = TrainingTypeDto.class))))
      })
  public List<TrainingTypeDto> getTrainingTypes() {
    return crmFacade.getTrainingTypes();
  }
}
