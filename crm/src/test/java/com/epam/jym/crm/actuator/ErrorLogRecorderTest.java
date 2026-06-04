package com.epam.jym.crm.actuator;

import static org.mockito.Mockito.when;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import java.time.Instant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ErrorLogRecorderTest {

  @Test
  void summaryShouldReturnErrorCountAndLastError() {
    ErrorLogRecorder recorder = new ErrorLogRecorder();

    recorder.record(loggingEvent(1000L, "first", null));
    recorder.record(loggingEvent(2000L, "second", throwableProxy()));

    ErrorLogRecorder.ErrorLogSummary summary = recorder.summary();

    Assertions.assertThat(summary.errorCountSinceRestart()).isEqualTo(2);
    Assertions.assertThat(summary.lastError().timestamp()).isEqualTo(Instant.ofEpochMilli(2000L));
    Assertions.assertThat(summary.lastError().message()).isEqualTo("second");
    Assertions.assertThat(summary.lastError().exceptionClass())
        .isEqualTo("java.lang.IllegalStateException");
    Assertions.assertThat(summary.lastError().exceptionMessage()).isEqualTo("broken");
  }

  @Test
  void summaryShouldReturnEmptyStateBeforeAnyError() {
    ErrorLogRecorder recorder = new ErrorLogRecorder();

    ErrorLogRecorder.ErrorLogSummary summary = recorder.summary();

    Assertions.assertThat(summary.errorCountSinceRestart()).isZero();
    Assertions.assertThat(summary.lastError()).isNull();
  }

  private static ILoggingEvent loggingEvent(
      long timestamp, String message, IThrowableProxy throwableProxy) {
    ILoggingEvent event = org.mockito.Mockito.mock(ILoggingEvent.class);

    when(event.getTimeStamp()).thenReturn(timestamp);
    when(event.getLoggerName()).thenReturn("test.logger");
    when(event.getFormattedMessage()).thenReturn(message);
    when(event.getThrowableProxy()).thenReturn(throwableProxy);

    return event;
  }

  private static IThrowableProxy throwableProxy() {
    IThrowableProxy throwableProxy = org.mockito.Mockito.mock(IThrowableProxy.class);

    when(throwableProxy.getClassName()).thenReturn("java.lang.IllegalStateException");
    when(throwableProxy.getMessage()).thenReturn("broken");

    return throwableProxy;
  }
}
