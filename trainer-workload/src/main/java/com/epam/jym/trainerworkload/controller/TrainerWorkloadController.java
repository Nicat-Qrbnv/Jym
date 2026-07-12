package com.epam.jym.trainerworkload.controller;

import com.epam.jym.trainerworkload.dto.TrainerMonthlySummaryResponse;
import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.trainerworkload.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/v1/trainer-workloads")
public class TrainerWorkloadController {

  private final TrainerWorkloadService trainerWorkloadService;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public void acceptTrainerWorkload(@Valid @RequestBody TrainerWorkloadUpdateRequest request) {
    trainerWorkloadService.acceptTrainerWorkload(request);
  }

  @PostMapping("/batch")
  @ResponseStatus(HttpStatus.OK)
  public void acceptTrainerWorkload(@Valid @RequestBody List<TrainerWorkloadUpdateRequest> requests) {
    trainerWorkloadService.acceptTrainerWorkload(requests);
  }

  @GetMapping("/monthly-summary")
  @ResponseStatus(HttpStatus.OK)
  public TrainerMonthlySummaryResponse getMonthlySummary(
      @RequestParam @NotBlank String trainerUsername) {
    return trainerWorkloadService.getMonthlySummary(trainerUsername);
  }
}
