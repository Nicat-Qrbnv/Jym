package com.epam.jym.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

  @Bean
  public RouteLocator customRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("user-service", r -> r
            .path("/api/user/**")
            .filters(f -> f.stripPrefix(2))
            .uri("lb://user"))
        .route("crm-service", r -> r
            .path("/api/crm/**")
            .filters(f -> f.stripPrefix(2))
            .uri("lb://crm"))
        .build();
  }
}
