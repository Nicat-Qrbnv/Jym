package com.epam.jym.crm.actuator;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class ErrorLogAppender extends AppenderBase<ILoggingEvent>
    implements InitializingBean, DisposableBean {

  private static final String APPENDER_NAME = "errorLogRecorder";

  private final ErrorLogRecorder errorLogRecorder;

  private Logger rootLogger;

  public ErrorLogAppender(ErrorLogRecorder errorLogRecorder) {
    this.errorLogRecorder = errorLogRecorder;
  }

  @Override
  public void afterPropertiesSet() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    setContext(loggerContext);
    setName(APPENDER_NAME);
    start();

    rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.addAppender(this);
  }

  @Override
  protected void append(ILoggingEvent event) {
    if (Level.ERROR.equals(event.getLevel())) {
      errorLogRecorder.record(event);
    }
  }

  @Override
  public void destroy() {
    if (rootLogger != null) {
      rootLogger.detachAppender(this);
    }
    stop();
  }
}
