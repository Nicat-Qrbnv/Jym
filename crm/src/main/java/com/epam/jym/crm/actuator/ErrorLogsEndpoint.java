package com.epam.jym.crm.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "errorLogs")
public class ErrorLogsEndpoint {

  private final ErrorLogRecorder errorLogRecorder;

  public ErrorLogsEndpoint(ErrorLogRecorder errorLogRecorder) {
    this.errorLogRecorder = errorLogRecorder;
  }

  @ReadOperation
  public ErrorLogRecorder.ErrorLogSummary errorLogs() {
    return errorLogRecorder.summary();
  }
}
