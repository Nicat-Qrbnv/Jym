package com.epam.jym.crm.aspect.logging;

import static com.epam.jym.crm.aspect.logging.TransactionLoggingConstants.TRANSACTION_ID_HEADER;
import static com.epam.jym.crm.aspect.logging.TransactionLoggingConstants.TRANSACTION_ID_MDC_KEY;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
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
 * Creates and propagates a transaction identifier for each HTTP request and logs REST request and
 * response details under that identifier.
 *
 * <p>The filter accepts an incoming {@code X-Transaction-Id} header or generates a new UUID when
 * the header is absent. The value is stored in SLF4J MDC for the request lifetime and is also
 * returned to the client as a response header. Request and response bodies are logged with basic
 * masking for sensitive values.
 */
@Component
@Slf4j
public class TransactionLoggingFilter extends OncePerRequestFilter {

  private static final int MAX_BODY_LOG_LENGTH = 2000;
  private static final Pattern SENSITIVE_JSON_FIELD_PATTERN =
      Pattern.compile(
          "(?i)(\"(?:password|newPassword|authorization|token)\"\\s*:\\s*\")([^\"]*)(\")");
  private static final Pattern SENSITIVE_CREDENTIAL_PATTERN =
      Pattern.compile("(?i)(password=|Authorization:|Bearer\\s+)([^\\s,;]+)");

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    ContentCachingRequestWrapper cachingRequest =
        new ContentCachingRequestWrapper(request, MAX_BODY_LOG_LENGTH);
    ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);
    String transactionId = resolveTransactionId(cachingRequest);
    long startedAt = System.currentTimeMillis();

    MDC.put(TRANSACTION_ID_MDC_KEY, transactionId);
    cachingResponse.setHeader(TRANSACTION_ID_HEADER, transactionId);

    try {
      log.info(
          "Transaction started: method={} endpoint={}",
          cachingRequest.getMethod(),
          resolveEndpoint(cachingRequest));
      filterChain.doFilter(cachingRequest, cachingResponse);
      log.info(
          "Transaction completed: method={} endpoint={} requestBody={} status={} responseBody={} "
              + "durationMs={}",
          cachingRequest.getMethod(),
          resolveEndpoint(cachingRequest),
          resolveRequestBody(cachingRequest),
          cachingResponse.getStatus(),
          resolveResponseBody(cachingResponse),
          System.currentTimeMillis() - startedAt);
    } catch (Exception exception) {
      log.warn(
          "Transaction failed: method={} endpoint={} requestBody={} status={} responseBody={} "
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
      MDC.remove(TRANSACTION_ID_MDC_KEY);
    }
  }

  private String resolveTransactionId(HttpServletRequest request) {
    String transactionId = request.getHeader(TRANSACTION_ID_HEADER);
    if (StringUtils.hasText(transactionId)) {
      return transactionId;
    }
    return UUID.randomUUID().toString();
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
