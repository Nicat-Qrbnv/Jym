package com.epam.jym.trainerworkload.exception;

public abstract class ApiException extends RuntimeException {

  protected ApiException(String message) {
    super(message);
  }
}
