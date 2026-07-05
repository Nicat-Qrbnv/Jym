package com.epam.jym.trainerworkload.service;

import com.epam.jym.trainerworkload.dto.TrainerMonthlySummaryResponse;
import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;

public interface TrainerWorkloadService {

  void acceptTrainerWorkload(TrainerWorkloadUpdateRequest request);

  TrainerMonthlySummaryResponse getMonthlySummary(String trainerUsername);
}
