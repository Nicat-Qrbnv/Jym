package com.epam.jym.trainerworkload.messaging;

import static com.epam.jym.trainerworkload.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.trainerworkload.dto.ActionType;
import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.trainerworkload.service.TrainerWorkloadService;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadListenerTest {

  @Mock private TrainerWorkloadMessagingProperties messagingProperties;
  @Mock private TrainerWorkloadDeadLetterPublisher deadLetterPublisher;
  @Mock private TrainerWorkloadService trainerWorkloadService;

  @InjectMocks private TrainerWorkloadListener listener;

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  void acceptTrainerWorkloadShouldDelegateSingleMessageAndPopulateTraceId() {
    TrainerWorkloadUpdateRequest request = request();
    AtomicReference<String> observedTraceId = new AtomicReference<>();
    doAnswer(
            _ -> {
              observedTraceId.set(MDC.get(TRACE_ID_MDC_KEY));
              return null;
            })
        .when(trainerWorkloadService)
        .acceptTrainerWorkload(request);

    listener.acceptTrainerWorkload(request, "trace-42", 1);

    verify(trainerWorkloadService).acceptTrainerWorkload(request);
    assertThat(observedTraceId.get()).isEqualTo("trace-42");
    assertThat(MDC.get(TRACE_ID_MDC_KEY)).isNull();
    verifyNoInteractions(deadLetterPublisher);
  }

  @Test
  void acceptTrainerWorkloadShouldRethrowBeforeDlqThreshold() {
    TrainerWorkloadUpdateRequest request = request();
    when(messagingProperties.maxDeliveryAttempts()).thenReturn(3);
    IllegalStateException failure = new IllegalStateException("redis unavailable");
    doAnswer(
            _ -> {
              assertThat(MDC.get(TRACE_ID_MDC_KEY)).isEqualTo("trace-42");
              throw failure;
            })
        .when(trainerWorkloadService)
        .acceptTrainerWorkload(request);

    assertThatThrownBy(() -> listener.acceptTrainerWorkload(request, "trace-42", 2))
        .isSameAs(failure);

    verify(deadLetterPublisher, never()).publish(any(), any());
    assertThat(MDC.get(TRACE_ID_MDC_KEY)).isNull();
  }

  @Test
  void acceptTrainerWorkloadShouldPublishToDlqAtThreshold() {
    TrainerWorkloadUpdateRequest request = request();
    when(messagingProperties.maxDeliveryAttempts()).thenReturn(3);
    doAnswer(
            _ -> {
              assertThat(MDC.get(TRACE_ID_MDC_KEY)).isEqualTo("trace-42");
              throw new IllegalStateException("db unavailable");
            })
        .when(trainerWorkloadService)
        .acceptTrainerWorkload(request);

    listener.acceptTrainerWorkload(request, "trace-42", 3);

    verify(deadLetterPublisher)
        .publish(
            new TrainerWorkloadDeadLetterMessage(
                request, "trace-42", 3, "db unavailable"),
            "trace-42");
    assertThat(MDC.get(TRACE_ID_MDC_KEY)).isNull();
  }

  private static TrainerWorkloadUpdateRequest request() {
    return new TrainerWorkloadUpdateRequest(
        "jane.doe",
        "Jane",
        "Doe",
        true,
        LocalDate.of(2026, 7, 8),
        60,
        ActionType.ADD,
        10L);
  }
}
