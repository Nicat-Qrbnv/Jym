package com.epam.jym.crm.actuator;

import static org.mockito.Mockito.when;

import java.time.Instant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErrorLogsEndpointTest {

  @Mock private ErrorLogRecorder errorLogRecorder;

  @Test
  void errorLogsShouldReturnRecorderSummary() {
    ErrorLogRecorder.ErrorLogEntry lastError =
        new ErrorLogRecorder.ErrorLogEntry(
            Instant.EPOCH, "failed", "java.lang.RuntimeException", "broken");
    ErrorLogRecorder.ErrorLogSummary expected = new ErrorLogRecorder.ErrorLogSummary(1L, lastError);
    ErrorLogsEndpoint endpoint = new ErrorLogsEndpoint(errorLogRecorder);

    when(errorLogRecorder.summary()).thenReturn(expected);

    ErrorLogRecorder.ErrorLogSummary actual = endpoint.errorLogs();

    Assertions.assertThat(actual).isSameAs(expected);
  }
}
