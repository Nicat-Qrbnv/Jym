package com.epam.jym.crm.service.impl;

import static com.epam.jym.crm.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.client.workload.TrainerWorkloadClient;
import com.epam.jym.crm.dto.ActionType;
import com.epam.jym.crm.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.DownstreamServiceException;
import com.epam.jym.jwthandler.service.JwtService;
import io.jsonwebtoken.JwtException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

  @Mock private TrainerWorkloadClient workloadClient;
  @Mock private JwtService jwtService;
  @Mock private CircuitBreakerFactory<?, ?> circuitBreakerFactory;
  @Mock private CircuitBreaker circuitBreaker;

  private TrainerWorkloadServiceImpl service;

  @BeforeEach
  void setUp() {
    when(circuitBreakerFactory.create("trainerWorkloadUpdate")).thenReturn(circuitBreaker);
    service = new TrainerWorkloadServiceImpl(workloadClient, jwtService, circuitBreakerFactory);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
    MDC.clear();
  }

  @Test
  void sendWorkloadUpdateShouldMapTrainingAndCallClientForSingleTraining() {
    Training training =
        training(11L, LocalDate.of(2026, 7, 8), 75);
    stubCircuitBreakerToRunSupplier();
    withAuthorizationHeader("Bearer token-123");
    MDC.put(TRACE_ID_MDC_KEY, "trace-42");
    when(jwtService.isValid("token-123")).thenReturn(true);

    service.sendWorkloadUpdate(training, ActionType.ADD);

    ArgumentCaptor<TrainerWorkloadUpdateRequest> requestCaptor =
        ArgumentCaptor.forClass(TrainerWorkloadUpdateRequest.class);
    verify(workloadClient)
        .acceptTrainerWorkload(
            eq("Bearer token-123"), eq("trace-42"), requestCaptor.capture());
    assertThat(requestCaptor.getValue())
        .isEqualTo(
            new TrainerWorkloadUpdateRequest(
                "jane.doe",
                "Jane",
                "Doe",
                true,
                LocalDate.of(2026, 7, 8),
                75,
                ActionType.ADD,
                11L));
  }

  @Test
  void sendWorkloadUpdateShouldMapAndCallClientForBatch() {
    Training first = training(11L, LocalDate.of(2026, 7, 8), 75);
    Training second =
        training(12L, LocalDate.of(2026, 7, 9), 30);
    stubCircuitBreakerToRunSupplier();
    withAuthorizationHeader("Bearer token-123");
    MDC.put(TRACE_ID_MDC_KEY, "trace-42");
    when(jwtService.isValid("token-123")).thenReturn(true);

    service.sendWorkloadUpdate(List.of(first, second), ActionType.DELETE);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<TrainerWorkloadUpdateRequest>> requestCaptor =
        (ArgumentCaptor<List<TrainerWorkloadUpdateRequest>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(List.class);
    verify(workloadClient)
        .acceptTrainerWorkload(
            eq("Bearer token-123"), eq("trace-42"), requestCaptor.capture());
    assertThat(requestCaptor.getValue())
        .containsExactly(
            new TrainerWorkloadUpdateRequest(
                "jane.doe",
                "Jane",
                "Doe",
                true,
                LocalDate.of(2026, 7, 8),
                75,
                ActionType.DELETE,
                11L),
            new TrainerWorkloadUpdateRequest(
                "jane.doe",
                "Jane",
                "Doe",
                true,
                LocalDate.of(2026, 7, 9),
                30,
                ActionType.DELETE,
                12L));
  }

  @Test
  void sendWorkloadUpdateShouldThrowWhenTrainingPayloadIsIncomplete() {
    assertThatThrownBy(() -> service.sendWorkloadUpdate((Training) null, ActionType.ADD))
        .isInstanceOf(DownstreamServiceException.class)
        .hasMessage("Training payload is incomplete for workload update");

    verifyNoInteractions(jwtService, workloadClient);
  }

  @Test
  void sendWorkloadUpdateShouldThrowWhenRequestContextIsMissing() {
    Training training = training(11L, LocalDate.of(2026, 7, 8), 75);

    assertThatThrownBy(() -> service.sendWorkloadUpdate(training, ActionType.ADD))
        .isInstanceOf(DownstreamServiceException.class)
        .hasMessage("HTTP request context is missing");

    verifyNoInteractions(jwtService, workloadClient);
  }

  @Test
  void sendWorkloadUpdateShouldThrowWhenAuthorizationHeaderIsMissing() {
    Training training = training(11L, LocalDate.of(2026, 7, 8), 75);
    withAuthorizationHeader(null);

    assertThatThrownBy(() -> service.sendWorkloadUpdate(training, ActionType.ADD))
        .isInstanceOf(DownstreamServiceException.class)
        .hasMessage("Authorization header is missing");

    verifyNoInteractions(jwtService, workloadClient);
  }

  @Test
  void sendWorkloadUpdateShouldThrowWhenAuthorizationHeaderIsNotBearer() {
    Training training = training(11L, LocalDate.of(2026, 7, 8), 75);
    withAuthorizationHeader("Basic abc123");

    assertThatThrownBy(() -> service.sendWorkloadUpdate(training, ActionType.ADD))
        .isInstanceOf(DownstreamServiceException.class)
        .hasMessage("Authorization header must use Bearer scheme");

    verifyNoInteractions(jwtService, workloadClient);
  }

  @Test
  void sendWorkloadUpdateShouldThrowWhenJwtValidationReturnsFalse() {
    Training training = training(11L, LocalDate.of(2026, 7, 8), 75);
    withAuthorizationHeader("Bearer token-123");
    when(jwtService.isValid("token-123")).thenReturn(false);

    assertThatThrownBy(() -> service.sendWorkloadUpdate(training, ActionType.ADD))
        .isInstanceOf(DownstreamServiceException.class)
        .hasMessage("Invalid authorization header");
  }

  @Test
  void sendWorkloadUpdateShouldWrapJwtParsingFailure() {
    Training training = training(11L, LocalDate.of(2026, 7, 8), 75);
    withAuthorizationHeader("Bearer broken-token");
    JwtException jwtException = new JwtException("Malformed");
    when(jwtService.isValid("broken-token")).thenThrow(jwtException);

    assertThatThrownBy(() -> service.sendWorkloadUpdate(training, ActionType.ADD))
        .isInstanceOf(DownstreamServiceException.class)
        .hasMessage("Invalid authorization header")
        .hasCause(jwtException);
  }

  @Test
  void sendWorkloadUpdateShouldWrapCircuitBreakerFallbackException() {
    Training training = training(11L, LocalDate.of(2026, 7, 8), 75);
    withAuthorizationHeader("Bearer token-123");
    when(jwtService.isValid("token-123")).thenReturn(true);
    doAnswer(
            invocation -> {
              Function<Throwable, ?> fallback = invocation.getArgument(1);
              return fallback.apply(new IllegalStateException("workload-service-down"));
            })
        .when(circuitBreaker)
        .run(any(Supplier.class), any(Function.class));

    assertThatThrownBy(() -> service.sendWorkloadUpdate(training, ActionType.ADD))
        .isInstanceOf(DownstreamServiceException.class)
        .hasMessage("Failed to synchronize trainer workload update")
        .hasCauseInstanceOf(IllegalStateException.class);
  }

  private static void withAuthorizationHeader(String value) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    if (value != null) {
      request.addHeader(HttpHeaders.AUTHORIZATION, value);
    }
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  private static Training training(
      Long id,
      LocalDate scheduledDate,
      int durationInMinutes) {
    User user = new User();
    user.setUsername("jane.doe");
    user.setFirstName("Jane");
    user.setLastName("Doe");
    user.setActive(true);

    Trainer trainer = new Trainer();
    trainer.setUser(user);

    Training training = new Training();
    training.setId(id);
    training.setTrainer(trainer);
    training.setScheduledDate(scheduledDate);
    training.setDurationInMinutes(durationInMinutes);
    return training;
  }

  private void stubCircuitBreakerToRunSupplier() {
    doAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            })
        .when(circuitBreaker)
        .run(any(Supplier.class), any(Function.class));
  }
}
