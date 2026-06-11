package com.epam.jym.crm.actuator;

import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ErrorLogAppenderTest {

  @Test
  void appendShouldRecordEventsPassedByLogback() {
    ErrorLogAppender appender = new ErrorLogAppender();
    ILoggingEvent errorEvent = loggingEvent(Level.ERROR, 2000L, "error");

    appender.setContext(new LoggerContext());
    appender.start();
    appender.doAppend(errorEvent);

    ErrorLogAppender.ErrorLogSummary summary = appender.summary();
    Assertions.assertThat(summary.errorCountSinceRestart()).isEqualTo(1);
    Assertions.assertThat(summary.lastError().message()).isEqualTo("error");
  }

  private static ILoggingEvent loggingEvent(Level level, long timestamp, String message) {
    ILoggingEvent event = org.mockito.Mockito.mock(ILoggingEvent.class);

    when(event.getLevel()).thenReturn(level);
    when(event.getTimeStamp()).thenReturn(timestamp);
    when(event.getFormattedMessage()).thenReturn(message);

    return event;
  }
}
