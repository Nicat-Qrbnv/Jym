package com.epam.jym.trainerworkload.security;

import com.epam.jym.trainerworkload.config.JwtProperties;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String ACTUATOR_ENDPOINT_PREFIX = "/actuator";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtProperties jwtProperties;

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return HttpMethod.OPTIONS.matches(request.getMethod())
        || request.getRequestURI().startsWith(ACTUATOR_ENDPOINT_PREFIX);
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    String token = extractToken(authorizationHeader);
    if (token == null) {
      unauthorized(response, "Missing Bearer token");
      return;
    }

    try {
      Date expiration = claims(token).getExpiration();
      if (expiration == null || expiration.before(new Date())) {
        unauthorized(response, "Token expired");
        return;
      }
      filterChain.doFilter(request, response);
    } catch (JwtException | IllegalArgumentException exception) {
      unauthorized(response, "Invalid token");
    }
  }

  private String extractToken(String authorizationHeader) {
    if (!StringUtils.hasText(authorizationHeader)
        || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      return null;
    }
    return authorizationHeader.substring(BEARER_PREFIX.length());
  }

  private io.jsonwebtoken.Claims claims(String token) {
    return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey signingKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private void unauthorized(HttpServletResponse response, String message) throws IOException {
    response.sendError(HttpStatus.UNAUTHORIZED.value(), message);
  }
}
