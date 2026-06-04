package com.epam.jym.crm.logging;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.aspect.logging.LogOperation;
import com.epam.jym.crm.aspect.logging.OperationLoggingAspect;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OperationLoggingAspectTest {

  @Mock private ProceedingJoinPoint joinPoint;

  @Mock private MethodSignature methodSignature;

  @Test
  void logOperationShouldProceedAndReturnResult() throws Throwable {
    OperationLoggingAspect aspect = new OperationLoggingAspect();
    Method method = LoggedTarget.class.getMethod("execute");

    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(joinPoint.getTarget()).thenReturn(new LoggedTarget());
    when(methodSignature.getMethod()).thenReturn(method);
    when(joinPoint.proceed()).thenReturn("result");

    Object result = aspect.logOperation(joinPoint);

    Assertions.assertThat(result).isEqualTo("result");
    verify(joinPoint).proceed();
  }

  @Test
  void logOperationShouldRethrowOperationException() throws Throwable {
    OperationLoggingAspect aspect = new OperationLoggingAspect();
    Method method = LoggedTarget.class.getMethod("execute");
    IllegalStateException exception = new IllegalStateException("failed");

    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(joinPoint.getTarget()).thenReturn(new LoggedTarget());
    when(methodSignature.getMethod()).thenReturn(method);
    when(joinPoint.proceed()).thenThrow(exception);

    Assertions.assertThatThrownBy(() -> aspect.logOperation(joinPoint)).isSameAs(exception);
  }

  @LogOperation("LoggedTarget")
  private static class LoggedTarget {

    public String execute() {
      return "result";
    }
  }
}
