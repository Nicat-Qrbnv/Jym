package com.epam.jym.gateway.config;

import com.epam.jym.jwthandler.service.JwtService;
import io.jsonwebtoken.JwtException;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayAuthFilter implements GlobalFilter, Ordered {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String USER_HEADER = "X-Authenticated-User";
  private final JwtService jwtService;

  public GatewayAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
    if (isPublicPath(exchange)) {
      return chain.filter(exchange);
    }

    String authorizationHeader =
        exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    String token = extractToken(authorizationHeader);
    if (token == null) {
      return unauthorized(exchange);
    }

    try {
      String username = jwtService.extractUsername(token);
      boolean isValid = jwtService.isValid(token);
      if (!StringUtils.hasText(username) || !isValid) {
        return unauthorized(exchange);
      }

      ServerHttpRequest request =
          exchange.getRequest().mutate().header(USER_HEADER, username).build();
      return chain.filter(exchange.mutate().request(request).build());
    } catch (JwtException | IllegalArgumentException exception) {
      return unauthorized(exchange);
    }
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 1;
  }

  private boolean isPublicPath(ServerWebExchange exchange) {
    String path = exchange.getRequest().getPath().value();
    HttpMethod method = exchange.getRequest().getMethod();
    return HttpMethod.OPTIONS.equals(method)
        || (HttpMethod.POST.equals(method) && "/api/crm/v1/auth/login".equals(path))
        || (HttpMethod.POST.equals(method) && "/api/crm/v1/trainees".equals(path))
        || (HttpMethod.POST.equals(method) && "/api/crm/v1/trainers".equals(path))
        || (HttpMethod.GET.equals(method) && path.startsWith("/api/crm/actuator"));
  }

  private String extractToken(String authorizationHeader) {
    if (!StringUtils.hasText(authorizationHeader)
        || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      return null;
    }

    return authorizationHeader.substring(BEARER_PREFIX.length());
  }

  private Mono<Void> unauthorized(ServerWebExchange exchange) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    return exchange.getResponse().setComplete();
  }
}
