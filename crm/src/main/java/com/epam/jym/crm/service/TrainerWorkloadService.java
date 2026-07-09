package com.epam.jym.crm.service;

import com.epam.jym.crm.entity.Training;

public interface TrainerWorkloadService {

  void sendAddWorkloadUpdate(Training training);
}
