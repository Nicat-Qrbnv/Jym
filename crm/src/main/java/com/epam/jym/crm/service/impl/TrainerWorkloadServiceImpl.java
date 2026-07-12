package com.epam.jym.crm.service.impl;

import static com.epam.jym.crm.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;

import com.epam.jym.crm.dto.ActionType;
import com.epam.jym.crm.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.DownstreamServiceException;
import com.epam.jym.crm.messaging.TrainerWorkloadPublisher;
import com.epam.jym.crm.service.TrainerWorkloadService;
import java.util.Objects;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {
  private final TrainerWorkloadPublisher workloadPublisher;

  public TrainerWorkloadServiceImpl(TrainerWorkloadPublisher workloadPublisher) {
    this.workloadPublisher = workloadPublisher;
  }

  @Override
  public void sendWorkloadUpdate(Training training, ActionType actionType) {
    TrainerWorkloadUpdateRequest request = toUpdateRequest(training, actionType);
    String traceId = MDC.get(TRACE_ID_MDC_KEY);
    workloadPublisher.publish(request, traceId);
  }

  private TrainerWorkloadUpdateRequest toUpdateRequest(Training training, ActionType actionType) {
    if (training == null
        || training.getTrainer() == null
        || training.getTrainer().getUser() == null) {
      throw new DownstreamServiceException("Training payload is incomplete for workload update");
    }
    User trainerUser = training.getTrainer().getUser();
    return new TrainerWorkloadUpdateRequest(
        trainerUser.getUsername(),
        trainerUser.getFirstName(),
        trainerUser.getLastName(),
        trainerUser.isActive(),
        training.getScheduledDate(),
        training.getDurationInMinutes(),
        actionType,
        Objects.requireNonNull(training.getId(), "training id must not be null"));
  }
}
