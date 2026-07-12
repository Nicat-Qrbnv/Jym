package com.epam.jym.trainerworkload.service;

import com.epam.jym.trainerworkload.dto.TrainerMonthlySummaryResponse;
import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import java.util.List;

public interface TrainerWorkloadService {

  void acceptTrainerWorkload(TrainerWorkloadUpdateRequest request);

  void acceptTrainerWorkload(List<TrainerWorkloadUpdateRequest> requests);

  TrainerMonthlySummaryResponse getMonthlySummary(String trainerUsername);
}
