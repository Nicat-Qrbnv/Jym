package com.epam.jym.trainerworkload.service.impl;

import com.epam.jym.trainerworkload.service.TrainerWorkloadService;
import org.springframework.stereotype.Service;

@Service
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

  @Override
  public String acceptTrainerWorkload() {
    return "Trainer workload endpoint is available.";
  }

  @Override
  public String getMonthlySummary() {
    return "Trainer monthly summary endpoint is available.";
  }
}
