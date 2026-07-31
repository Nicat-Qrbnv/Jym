package com.epam.jym.trainerworkload.controller;

import com.epam.jym.trainerworkload.dto.TrainerMonthlySummaryResponse;
import com.epam.jym.trainerworkload.service.TrainerWorkloadService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping("/monthly-summary")
  @ResponseStatus(HttpStatus.OK)
  public TrainerMonthlySummaryResponse getMonthlySummary(
      @RequestParam @NotBlank String trainerUsername) {
    return trainerWorkloadService.getMonthlySummary(trainerUsername);
  }
}
