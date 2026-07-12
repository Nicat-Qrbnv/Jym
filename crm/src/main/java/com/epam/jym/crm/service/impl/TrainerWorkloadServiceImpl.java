package com.epam.jym.crm.service.impl;

import static com.epam.jym.crm.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;

import com.epam.jym.crm.client.workload.TrainerWorkloadClient;
import com.epam.jym.crm.dto.ActionType;
import com.epam.jym.crm.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.DownstreamServiceException;
import com.epam.jym.crm.service.TrainerWorkloadService;
import com.epam.jym.jwthandler.service.JwtService;
import io.jsonwebtoken.JwtException;
import java.util.List;
import java.util.Objects;
import org.slf4j.MDC;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {
  private static final String BEARER_PREFIX = "Bearer ";

  private final TrainerWorkloadClient workloadClient;
  private final CircuitBreaker circuitBreaker;
  private final JwtService jwtService;

  public TrainerWorkloadServiceImpl(
      TrainerWorkloadClient workloadClient,
      JwtService jwtService,
      CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
    this.workloadClient = workloadClient;
    this.jwtService = jwtService;
    this.circuitBreaker = circuitBreakerFactory.create("trainerWorkloadUpdate");
  }

  @Override
  public void sendWorkloadUpdate(Training training, ActionType actionType) {
    TrainerWorkloadUpdateRequest request = toUpdateRequest(training, actionType);
    String token = resolveAuthorizationHeader();
    String traceId = MDC.get(TRACE_ID_MDC_KEY);

    circuitBreaker.run(
        () -> workloadClient.acceptTrainerWorkload(token, traceId, request),
        throwable -> {
          throw new DownstreamServiceException(
              "Failed to synchronize trainer workload update", throwable);
        });
  }

  @Override
  public void sendWorkloadUpdate(List<Training> trainings, ActionType actionType) {
    List<TrainerWorkloadUpdateRequest> requests =
        trainings.stream().map(t -> toUpdateRequest(t, actionType)).toList();
    String token = resolveAuthorizationHeader();
    String traceId = MDC.get(TRACE_ID_MDC_KEY);
    circuitBreaker.run(
        () -> workloadClient.acceptTrainerWorkload(token, traceId, requests),
        throwable -> {
          throw new DownstreamServiceException(
              "Failed to synchronize trainer workload update", throwable);
        });
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
    if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
      throw new DownstreamServiceException("Authorization header must use Bearer scheme");
    }
    String token = authorizationHeader.substring(BEARER_PREFIX.length());
    boolean isValid;
    try {
      isValid = jwtService.isValid(token);
    } catch (JwtException | IllegalArgumentException exception) {
      throw new DownstreamServiceException("Invalid authorization header", exception);
    }
    if (!isValid) {
      throw new DownstreamServiceException("Invalid authorization header");
    }
    return authorizationHeader;
  }
}
