package com.epam.jym.crm.validation.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@NotBlank
@Size(max = 150)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target({FIELD, METHOD, PARAMETER, RECORD_COMPONENT, TYPE_USE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface PersonName {
  String message() default "must not be blank and must be at most 150 characters";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
