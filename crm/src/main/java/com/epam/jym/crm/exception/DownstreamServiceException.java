package com.epam.jym.crm.exception;

public class DownstreamServiceException extends ApiException {

  public DownstreamServiceException(String message) {
    super(message);
  }

  public DownstreamServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
