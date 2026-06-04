package com.epam.jym.crm.aspect.logging;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a class or method for operation-level logging.
 *
 * <p>When placed on a class, every Spring AOP-intercepted method in that class is logged by {@link
 * OperationLoggingAspect}. When placed on a method, only that method is logged. The optional value
 * is used as a custom operation name or class-level operation prefix in log messages.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface LogOperation {

  /**
   * Custom operation name or class-level prefix to write in operation log messages.
   *
   * @return operation label, or an empty value to let the aspect derive the name from Java metadata
   */
  String value() default "";
}
