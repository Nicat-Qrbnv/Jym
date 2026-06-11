package com.epam.jym.crm.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;

@Configuration
public class OpenApiConfig {

  private static final String AUTHORIZATION_SCHEME = "basicAuth";

  @Bean
  public OpenAPI openApi() {
    return new OpenAPI()
        .components(
            new Components()
                .addSecuritySchemes(
                    AUTHORIZATION_SCHEME,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                        .description("HTTP Basic authentication")));
  }

  @Bean
  public OperationCustomizer authorizationHeaderCustomizer() {
    return (operation, handlerMethod) -> {
      if (requiresAuthorizationHeader(handlerMethod.getMethodParameters())) {
        operation.addSecurityItem(new SecurityRequirement().addList(AUTHORIZATION_SCHEME));
        removeExplicitAuthorizationParameter(operation);
      }
      return operation;
    };
  }

  private boolean requiresAuthorizationHeader(MethodParameter[] parameters) {
    return Arrays.stream(parameters).anyMatch(this::isAuthorizationHeader);
  }

  private boolean isAuthorizationHeader(MethodParameter parameter) {
    RequestHeader requestHeader = parameter.getParameterAnnotation(RequestHeader.class);
    if (requestHeader == null) {
      return false;
    }

    return HttpHeaders.AUTHORIZATION.equalsIgnoreCase(requestHeader.value())
        || HttpHeaders.AUTHORIZATION.equalsIgnoreCase(requestHeader.name());
  }

  private void removeExplicitAuthorizationParameter(Operation operation) {
    if (operation.getParameters() == null) {
      return;
    }

    operation.setParameters(
        operation.getParameters().stream()
            .filter(this::isNotAuthorizationHeaderParameter)
            .toList());
  }

  private boolean isNotAuthorizationHeaderParameter(Parameter parameter) {
    return !HttpHeaders.AUTHORIZATION.equalsIgnoreCase(parameter.getName())
        || !"header".equals(parameter.getIn());
  }
}
