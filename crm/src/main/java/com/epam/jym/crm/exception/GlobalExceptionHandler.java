package com.epam.jym.crm.exception;

import static com.epam.jym.crm.logging.TransactionLoggingConstants.TRANSACTION_ID_MDC_KEY;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.stream.Collectors;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ProblemDetail handleResourceNotFound(ResourceNotFoundException exception) {
    return problem(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler({
    InvalidRequestException.class,
    BusinessRuleViolationException.class,
    MethodArgumentNotValidException.class,
    ConstraintViolationException.class,
    MissingServletRequestParameterException.class
  })
  public ProblemDetail handleBadRequest(Exception exception) {
    return problem(HttpStatus.BAD_REQUEST, resolveBadRequestMessage(exception));
  }

  @ExceptionHandler({InvalidCredentialsException.class, MissingRequestHeaderException.class})
  public ProblemDetail handleUnauthorized(Exception exception) {
    return problem(HttpStatus.UNAUTHORIZED, exception.getMessage());
  }

  private ProblemDetail problem(HttpStatus status, String detail) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
    problemDetail.setType(URI.create("about:blank"));
    String transactionId = MDC.get(TRANSACTION_ID_MDC_KEY);
    if (transactionId != null) {
      problemDetail.setProperty(TRANSACTION_ID_MDC_KEY, transactionId);
    }
    return problemDetail;
  }

  private String resolveBadRequestMessage(Exception exception) {
    if (exception instanceof MethodArgumentNotValidException validationException) {
      return validationException.getBindingResult().getFieldErrors().stream()
          .map(error -> error.getField() + ": " + error.getDefaultMessage())
          .collect(Collectors.joining("; "));
    }
    if (exception instanceof ConstraintViolationException validationException) {
      return validationException.getConstraintViolations().stream()
          .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
          .collect(Collectors.joining("; "));
    }
    return exception.getMessage();
  }
}
