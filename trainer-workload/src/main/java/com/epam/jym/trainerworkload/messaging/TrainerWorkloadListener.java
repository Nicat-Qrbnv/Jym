package com.epam.jym.trainerworkload.messaging;

import static com.epam.jym.trainerworkload.logging.TraceLoggingConstants.TRACE_ID_HEADER;
import static com.epam.jym.trainerworkload.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;

import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.trainerworkload.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadListener {

  private final TrainerWorkloadService trainerWorkloadService;

  @JmsListener(destination = "${messaging.trainer-workload.queue}")
  public void acceptTrainerWorkload(
      TrainerWorkloadUpdateRequest request,
      @Header(name = TRACE_ID_HEADER, required = false) String traceId) {
    withTraceId(traceId, () -> trainerWorkloadService.acceptTrainerWorkload(request));
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
}
