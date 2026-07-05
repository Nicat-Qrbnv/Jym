package com.epam.jym.trainerworkload.controller;

import com.epam.jym.trainerworkload.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/trainer-workloads")
public class TrainerWorkloadController {

  private final TrainerWorkloadService trainerWorkloadService;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public String acceptTrainerWorkload() {
    return trainerWorkloadService.acceptTrainerWorkload();
  }

  @GetMapping("/monthly-summary")
  @ResponseStatus(HttpStatus.OK)
  public String getMonthlySummary() {
    return trainerWorkloadService.getMonthlySummary();
  }
}
