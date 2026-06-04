package com.epam.jym.crm.actuator;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.Test;

class ErrorLogAppenderTest {

  @Test
  void appendShouldRecordOnlyErrorEvents() {
    ErrorLogRecorder recorder = org.mockito.Mockito.mock(ErrorLogRecorder.class);
    ErrorLogAppender appender = new ErrorLogAppender(recorder);
    ILoggingEvent warningEvent = loggingEvent(Level.WARN);
    ILoggingEvent errorEvent = loggingEvent(Level.ERROR);

    appender.setContext(new LoggerContext());
    appender.start();
    appender.doAppend(warningEvent);
    appender.doAppend(errorEvent);

    verify(recorder, never()).record(warningEvent);
    verify(recorder).record(errorEvent);
  }

  private static ILoggingEvent loggingEvent(Level level) {
    ILoggingEvent event = org.mockito.Mockito.mock(ILoggingEvent.class);

    when(event.getLevel()).thenReturn(level);

    return event;
  }
}
