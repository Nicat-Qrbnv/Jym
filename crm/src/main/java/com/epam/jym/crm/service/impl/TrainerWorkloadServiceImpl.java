package com.epam.jym.crm.service.impl;

import static com.epam.jym.crm.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;

import com.epam.jym.crm.dto.ActionType;
import com.epam.jym.crm.client.workload.TrainerWorkloadClient;
import com.epam.jym.crm.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.DownstreamServiceException;
import com.epam.jym.crm.service.TrainerWorkloadService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

  private static final String CIRCUIT_BREAKER_NAME = "trainerWorkloadUpdate";

  private final TrainerWorkloadClient trainerWorkloadClient;
  private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

  @Override
  public void sendAddWorkloadUpdate(Training training) {
    TrainerWorkloadUpdateRequest request = toAddRequest(training);
    String authorizationHeader = resolveAuthorizationHeader();
    String traceId = MDC.get(TRACE_ID_MDC_KEY);

    circuitBreakerFactory
        .create(CIRCUIT_BREAKER_NAME)
        .run(
            () -> {
              trainerWorkloadClient.acceptTrainerWorkload(authorizationHeader, traceId, request);
              return null;
            },
            throwable -> {
              throw new DownstreamServiceException(
                  "Failed to synchronize trainer workload update", throwable);
            });
  }

  private TrainerWorkloadUpdateRequest toAddRequest(Training training) {
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
        ActionType.ADD,
        Objects.requireNonNull(training.getId(), "training id must not be null"));
  }

  private String resolveAuthorizationHeader() {
    Object requestAttributes = RequestContextHolder.getRequestAttributes();
    if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
      throw new DownstreamServiceException("HTTP request context is missing");
    }
    String authorizationHeader =
        servletRequestAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
    if (!StringUtils.hasText(authorizationHeader)) {
      throw new DownstreamServiceException("Authorization header is missing");
    }
    return authorizationHeader;
  }
}
