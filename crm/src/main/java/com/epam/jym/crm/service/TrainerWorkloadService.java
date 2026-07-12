package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.ActionType;
import com.epam.jym.crm.entity.Training;
import java.util.List;

public interface TrainerWorkloadService {

  void sendWorkloadUpdate(Training training, ActionType actionType);

  void sendWorkloadUpdate(List<Training> trainings, ActionType actionType);
}
