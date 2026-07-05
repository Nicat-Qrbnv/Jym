package com.epam.jym.trainerworkload.logging;

import static com.epam.jym.trainerworkload.logging.TraceLoggingConstants.TRACE_ID_HEADER;
import static com.epam.jym.trainerworkload.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TraceLoggingFilter extends OncePerRequestFilter {

  private static final String ACTUATOR_ENDPOINT_PREFIX = "/actuator";

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return request.getRequestURI().startsWith(ACTUATOR_ENDPOINT_PREFIX);
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String traceId = request.getHeader(TRACE_ID_HEADER);
    long startedAt = System.currentTimeMillis();

    if (StringUtils.hasText(traceId)) {
      MDC.put(TRACE_ID_MDC_KEY, traceId);
      response.setHeader(TRACE_ID_HEADER, traceId);
    }

    try {
      log.info(
          "Trace started: method={} endpoint={}",
          request.getMethod(),
          request.getRequestURI());
      filterChain.doFilter(request, response);
      log.info(
          "Trace completed: method={} endpoint={} status={} durationMs={}",
          request.getMethod(),
          request.getRequestURI(),
          response.getStatus(),
          System.currentTimeMillis() - startedAt);
    } finally {
      MDC.remove(TRACE_ID_MDC_KEY);
    }
  }
}
