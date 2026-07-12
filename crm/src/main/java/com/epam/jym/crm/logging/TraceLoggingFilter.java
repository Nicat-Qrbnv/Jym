package com.epam.jym.crm.logging;

import static com.epam.jym.crm.logging.TraceLoggingConstants.TRACE_ID_HEADER;
import static com.epam.jym.crm.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Propagates a trace identifier for each HTTP request and logs REST request and response details
 * under that identifier.
 *
 * <p>The filter accepts an incoming {@code X-Trace-Id} header, stores it in SLF4J MDC for the
 * request lifetime, and returns it to the client as a response header. Request and response bodies
 * are logged with basic masking for sensitive values. Trace identifier creation happens in the
 * gateway.
 */
@Component
@Slf4j
public class TraceLoggingFilter extends OncePerRequestFilter {

  private static final String ACTUATOR_ENDPOINT_PREFIX = "/actuator";
  private static final int MAX_BODY_LOG_LENGTH = 2000;
  private static final Pattern SENSITIVE_JSON_FIELD_PATTERN =
      Pattern.compile(
          "(?i)(\"(?:password|oldPassword|newPassword|authorization|token)\"\\s*:\\s*\")"
              + "([^\"]*)(\")");
  private static final Pattern SENSITIVE_CREDENTIAL_PATTERN =
      Pattern.compile("(?i)(password=|Authorization:|Bearer\\s+)([^\\s,;]+)");

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
    ContentCachingRequestWrapper cachingRequest =
        new ContentCachingRequestWrapper(request, MAX_BODY_LOG_LENGTH);
    ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);
    String traceId = resolveTraceId(cachingRequest);
    long startedAt = System.currentTimeMillis();

    if (StringUtils.hasText(traceId)) {
      MDC.put(TRACE_ID_MDC_KEY, traceId);
      cachingResponse.setHeader(TRACE_ID_HEADER, traceId);
    }

    try {
      log.info(
          "Trace started: method={} endpoint={}",
          cachingRequest.getMethod(),
          resolveEndpoint(cachingRequest));
      filterChain.doFilter(cachingRequest, cachingResponse);
      log.info(
          "Trace completed: method={} endpoint={} requestBody={} status={} responseBody={} "
              + "durationMs={}",
          cachingRequest.getMethod(),
          resolveEndpoint(cachingRequest),
          resolveRequestBody(cachingRequest),
          cachingResponse.getStatus(),
          resolveResponseBody(cachingResponse),
          System.currentTimeMillis() - startedAt);
    } catch (Exception exception) {
      log.warn(
          "Trace failed: method={} endpoint={} requestBody={} status={} responseBody={} "
              + "durationMs={} error={} message={}",
          cachingRequest.getMethod(),
          resolveEndpoint(cachingRequest),
          resolveRequestBody(cachingRequest),
          cachingResponse.getStatus(),
          resolveResponseBody(cachingResponse),
          System.currentTimeMillis() - startedAt,
          exception.getClass().getSimpleName(),
          sanitize(exception.getMessage()));
      throw exception;
    } finally {
      cachingResponse.copyBodyToResponse();
      MDC.remove(TRACE_ID_MDC_KEY);
    }
  }

  private String resolveTraceId(HttpServletRequest request) {
    return request.getHeader(TRACE_ID_HEADER);
  }

  private String resolveEndpoint(HttpServletRequest request) {
    String queryString = request.getQueryString();
    if (StringUtils.hasText(queryString)) {
      return request.getRequestURI() + "?" + sanitize(queryString);
    }
    return request.getRequestURI();
  }

  private String resolveRequestBody(ContentCachingRequestWrapper request) {
    return resolveBody(request.getContentAsByteArray(), request.getCharacterEncoding());
  }

  private String resolveResponseBody(ContentCachingResponseWrapper response) {
    return resolveBody(response.getContentAsByteArray(), response.getCharacterEncoding());
  }

  private String resolveBody(byte[] content, String characterEncoding) {
    if (content.length == 0) {
      return "";
    }
    Charset charset =
        StringUtils.hasText(characterEncoding)
            ? Charset.forName(characterEncoding)
            : StandardCharsets.UTF_8;
    return sanitize(new String(content, charset));
  }

  private String sanitize(String value) {
    if (value == null) {
      return "";
    }
    String sanitized = SENSITIVE_JSON_FIELD_PATTERN.matcher(value).replaceAll("$1***$3");
    sanitized = SENSITIVE_CREDENTIAL_PATTERN.matcher(sanitized).replaceAll("$1***");
    sanitized = sanitized.replaceAll("\\s+", " ").trim();
    if (sanitized.length() > MAX_BODY_LOG_LENGTH) {
      return sanitized.substring(0, MAX_BODY_LOG_LENGTH) + "...";
    }
    return sanitized;
  }
}
