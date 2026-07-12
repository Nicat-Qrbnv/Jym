package com.epam.jym.trainerworkload.exception;

import static com.epam.jym.trainerworkload.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({InvalidRequestException.class, BusinessRuleViolationException.class})
  public ProblemDetail handleBadRequest(ApiException exception) {
    log.warn("Bad request: {}", exception.getMessage());
    return problem(HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
  public ProblemDetail handleValidationFailure(Exception exception) {
    log.warn("Validation failed: {}", exception.getMessage());
    return problem(HttpStatus.BAD_REQUEST, resolveValidationMessage(exception));
  }

  private ProblemDetail problem(HttpStatus status, String detail) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
    problemDetail.setType(URI.create("about:blank"));
    String traceId = MDC.get(TRACE_ID_MDC_KEY);
    if (traceId != null) {
      problemDetail.setProperty(TRACE_ID_MDC_KEY, traceId);
    }
    return problemDetail;
  }

  private String resolveValidationMessage(Exception exception) {
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
