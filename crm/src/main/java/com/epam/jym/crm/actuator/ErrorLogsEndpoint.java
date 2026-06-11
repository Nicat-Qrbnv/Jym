package com.epam.jym.crm.actuator;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "errorLogs")
public class ErrorLogsEndpoint {

  private final String errorLogAppenderName;

  public ErrorLogsEndpoint(
      @Value("${app.logging.error-appender-name}") String errorLogAppenderName) {
    this.errorLogAppenderName = errorLogAppenderName;
  }

  @ReadOperation
  public ErrorLogAppender.ErrorLogSummary errorLogs() {
    ErrorLogAppender appender = errorLogAppender();
    if (appender == null) {
      return new ErrorLogAppender.ErrorLogSummary(0, null);
    }
    return appender.summary();
  }

  private ErrorLogAppender errorLogAppender() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
    var configuredAppender = rootLogger.getAppender(errorLogAppenderName);
    if (configuredAppender instanceof ErrorLogAppender appender) {
      return appender;
    }
    return null;
  }
}
