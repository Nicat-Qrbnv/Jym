package com.epam.jym.crm.actuator;

import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class ErrorLogsEndpointTest {

  private static final String ERROR_LOG_APPENDER_NAME = "errorLogAppender";

  private final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
  private final Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

  @AfterEach
  void tearDown() {
    rootLogger.detachAppender(ERROR_LOG_APPENDER_NAME);
  }

  @Test
  void errorLogsShouldReturnConfiguredAppenderSummary() {
    ErrorLogAppender appender = new ErrorLogAppender();
    appender.setContext(loggerContext);
    appender.setName(ERROR_LOG_APPENDER_NAME);
    appender.start();
    rootLogger.addAppender(appender);
    appender.doAppend(loggingEvent());
    ErrorLogsEndpoint endpoint = new ErrorLogsEndpoint(ERROR_LOG_APPENDER_NAME);

    ErrorLogAppender.ErrorLogSummary actual = endpoint.errorLogs();

    Assertions.assertThat(actual.errorCountSinceRestart()).isEqualTo(1);
    Assertions.assertThat(actual.lastError().message()).isEqualTo("failed");
  }

  @Test
  void errorLogsShouldReturnEmptySummaryWhenAppenderIsNotConfigured() {
    ErrorLogsEndpoint endpoint = new ErrorLogsEndpoint(ERROR_LOG_APPENDER_NAME);

    ErrorLogAppender.ErrorLogSummary actual = endpoint.errorLogs();

    Assertions.assertThat(actual.errorCountSinceRestart()).isZero();
    Assertions.assertThat(actual.lastError()).isNull();
  }

  private static ILoggingEvent loggingEvent() {
    ILoggingEvent event = org.mockito.Mockito.mock(ILoggingEvent.class);

    when(event.getLevel()).thenReturn(Level.ERROR);
    when(event.getTimeStamp()).thenReturn(1000L);
    when(event.getFormattedMessage()).thenReturn("failed");

    return event;
  }
}
