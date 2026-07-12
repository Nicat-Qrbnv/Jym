package com.epam.jym.trainerworkload.messaging;

import static com.epam.jym.trainerworkload.logging.TraceLoggingConstants.TRACE_ID_HEADER;
import static com.epam.jym.trainerworkload.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;

import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.trainerworkload.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadListener {

  private static final int INITIAL_DELIVERY_COUNT = 1;

  private final TrainerWorkloadMessagingProperties messagingProperties;
  private final TrainerWorkloadDeadLetterPublisher deadLetterPublisher;
  private final TrainerWorkloadService trainerWorkloadService;

  @JmsListener(
      destination = "${messaging.trainer-workload.queue}",
      containerFactory = "trainerWorkloadJmsListenerContainerFactory")
  public void acceptTrainerWorkload(
      TrainerWorkloadUpdateRequest request,
      @Header(name = TRACE_ID_HEADER, required = false) String traceId,
      @Header(name = "JMSXDeliveryCount", required = false) Integer deliveryCount) {
    withTraceId(traceId, () -> processRequest(request, traceId, deliveryCount));
  }

  private static void withTraceId(String traceId, Runnable action) {
    if (StringUtils.hasText(traceId)) {
      MDC.put(TRACE_ID_MDC_KEY, traceId);
    }
    try {
      action.run();
    } finally {
      if (StringUtils.hasText(traceId)) {
        MDC.remove(TRACE_ID_MDC_KEY);
      }
    }
  }

  private void processRequest(
      TrainerWorkloadUpdateRequest request, String traceId, Integer deliveryCount) {
    try {
      trainerWorkloadService.acceptTrainerWorkload(request);
    } catch (RuntimeException exception) {
      int currentDeliveryCount =
          deliveryCount == null ? INITIAL_DELIVERY_COUNT : deliveryCount;
      if (currentDeliveryCount >= messagingProperties.maxDeliveryAttempts()) {
        log.error(
            "Routing trainer workload message to DLQ after {} attempts for trainingId={}",
            currentDeliveryCount,
            request.trainingId(),
            exception);
        deadLetterPublisher.publish(
            new TrainerWorkloadDeadLetterMessage(
                request, traceId, currentDeliveryCount, exception.getMessage()),
            traceId);
        return;
      }
      throw exception;
    }
  }
}
