package com.epam.jym.gateway.logging;

import static com.epam.jym.gateway.logging.TraceLoggingFilter.TRACE_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

class TraceLoggingFilterTest {

  private final TraceLoggingFilter filter = new TraceLoggingFilter();

  @Test
  void shouldGenerateTraceIdWhenHeaderIsMissing() {
    GatewayFilterChain chain = mock(GatewayFilterChain.class);
    AtomicReference<ServerWebExchange> forwardedExchange = new AtomicReference<>();
    when(chain.filter(any()))
        .thenAnswer(
            invocation -> {
              forwardedExchange.set(invocation.getArgument(0));
              return Mono.empty();
            });

    MockServerWebExchange exchange =
        MockServerWebExchange.from(MockServerHttpRequest.get("/api/crm/v1/trainings").build());

    filter.filter(exchange, chain).block();

    assertThat(forwardedExchange.get()).isNotNull();
    String traceId = forwardedExchange.get().getRequest().getHeaders().getFirst(TRACE_ID_HEADER);
    assertThat(traceId).isNotBlank();
    assertThat(UUID.fromString(traceId)).isNotNull();
    assertThat(exchange.getResponse().getHeaders().getFirst(TRACE_ID_HEADER)).isEqualTo(traceId);
  }

  @Test
  void shouldReuseIncomingTraceId() {
    GatewayFilterChain chain = mock(GatewayFilterChain.class);
    AtomicReference<ServerWebExchange> forwardedExchange = new AtomicReference<>();
    when(chain.filter(any()))
        .thenAnswer(
            invocation -> {
              forwardedExchange.set(invocation.getArgument(0));
              return Mono.empty();
            });

    MockServerWebExchange exchange =
        MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/crm/v1/trainings")
                .header(TRACE_ID_HEADER, "incoming-trace-id")
                .build());

    filter.filter(exchange, chain).block();

    assertThat(forwardedExchange.get()).isNotNull();
    assertThat(forwardedExchange.get().getRequest().getHeaders().getFirst(TRACE_ID_HEADER))
        .isEqualTo("incoming-trace-id");
    assertThat(exchange.getResponse().getHeaders().getFirst(TRACE_ID_HEADER))
        .isEqualTo("incoming-trace-id");
  }
}
