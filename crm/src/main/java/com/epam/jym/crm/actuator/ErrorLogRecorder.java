package com.epam.jym.crm.actuator;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

@Component
public class ErrorLogRecorder {

  private final AtomicLong errorCount = new AtomicLong();

  private final AtomicReference<ErrorLogEntry> lastError = new AtomicReference<>();

  public void record(ILoggingEvent event) {
    errorCount.incrementAndGet();
    var throwableProxy = event.getThrowableProxy();
    lastError.set(
        new ErrorLogEntry(
            Instant.ofEpochMilli(event.getTimeStamp()),
            event.getFormattedMessage(),
            resolveExceptionClass(throwableProxy),
            resolveExceptionMessage(throwableProxy)));
  }

  public ErrorLogSummary summary() {
    return new ErrorLogSummary(errorCount.get(), lastError.get());
  }

  private String resolveExceptionClass(IThrowableProxy throwableProxy) {
    return throwableProxy == null ? null : throwableProxy.getClassName();
  }

  private String resolveExceptionMessage(IThrowableProxy throwableProxy) {
    return throwableProxy == null ? null : throwableProxy.getMessage();
  }

  public record ErrorLogSummary(long errorCountSinceRestart, ErrorLogEntry lastError) {}

  public record ErrorLogEntry(
      Instant timestamp,
      String message,
      String exceptionClass,
      String exceptionMessage) {}
}
