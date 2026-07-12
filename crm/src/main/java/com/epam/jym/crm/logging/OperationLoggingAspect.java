package com.epam.jym.crm.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Logs execution of classes and methods annotated with {@link LogOperation}.
 *
 * <p>The aspect writes start, completion, failure, and duration messages. Trace identifiers are
 * not handled here directly; they are included automatically when {@link TraceLoggingFilter} has
 * placed the current trace id in SLF4J MDC.
 */
@Aspect
@Component
@Slf4j
public class OperationLoggingAspect {

  /** Matches methods declared on classes annotated with {@link LogOperation}. */
  @Pointcut("@within(com.epam.jym.crm.logging.LogOperation)")
  public void logOperationClass() {}

  /** Matches methods directly annotated with {@link LogOperation}. */
  @Pointcut("@annotation(com.epam.jym.crm.logging.LogOperation)")
  public void logOperationMethod() {}

  /**
   * Logs operation start, completion, failure, and duration around matched join points.
   *
   * @param joinPoint intercepted method invocation
   * @return intercepted method result
   * @throws Throwable when the intercepted method fails
   */
  @Around("logOperationClass() || logOperationMethod()")
  public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
    String operationName = resolveOperationName(joinPoint);
    long startedAt = System.currentTimeMillis();

    log.debug("Operation started: {}", operationName);
    try {
      Object result = joinPoint.proceed();
      log.debug(
          "Operation completed: {} durationMs={}",
          operationName,
          System.currentTimeMillis() - startedAt);
      return result;
    } catch (Exception exception) {
      log.warn(
          "Operation failed: {} durationMs={} error={}",
          operationName,
          System.currentTimeMillis() - startedAt,
          exception.getClass().getSimpleName());
      throw exception;
    }
  }

  private String resolveOperationName(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    LogOperation methodAnnotation = signature.getMethod().getAnnotation(LogOperation.class);
    if (methodAnnotation != null && StringUtils.hasText(methodAnnotation.value())) {
      return methodAnnotation.value();
    }

    LogOperation classAnnotation =
        joinPoint.getTarget().getClass().getAnnotation(LogOperation.class);
    if (classAnnotation != null && StringUtils.hasText(classAnnotation.value())) {
      return classAnnotation.value() + "." + signature.getMethod().getName();
    }

    return signature.getDeclaringType().getSimpleName() + "." + signature.getMethod().getName();
  }
}
