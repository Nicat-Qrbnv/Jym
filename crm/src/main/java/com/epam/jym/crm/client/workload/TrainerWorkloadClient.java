package com.epam.jym.crm.client.workload;

import com.epam.jym.crm.entity.Training;

public interface TrainerWorkloadClient {

  void sendAddWorkloadUpdate(Training training);
}
