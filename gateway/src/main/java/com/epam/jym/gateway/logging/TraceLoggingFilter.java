package com.epam.jym.gateway.logging;

import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TraceLoggingFilter implements GlobalFilter, Ordered {
  public static final String TRACE_ID_HEADER = "X-Trace-Id";

  @Override
  public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
    String traceId = resolveTraceId(exchange);
    ServerHttpRequest request =
        exchange
            .getRequest()
            .mutate()
            .headers(
                headers -> {
                  if (headers.get(TRACE_ID_HEADER) == null) {
                    headers.set(TRACE_ID_HEADER, traceId);
                  }
                })
            .build();

    exchange.getResponse().getHeaders().set(TRACE_ID_HEADER, traceId);
    return chain.filter(exchange.mutate().request(request).build());
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  private String resolveTraceId(ServerWebExchange exchange) {
    String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER);
    if (StringUtils.hasText(traceId)) {
      return traceId;
    }
    return UUID.randomUUID().toString();
  }
}
