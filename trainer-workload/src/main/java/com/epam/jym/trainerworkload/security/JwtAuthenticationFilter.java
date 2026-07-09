package com.epam.jym.trainerworkload.security;

import com.epam.jym.jwthandler.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String PROTECTED_PATH = "/v1/trainer-workloads";

  private final JwtService jwtService;

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    String servletPath = request.getServletPath();
    boolean isProtectedPath =
        PROTECTED_PATH.equals(servletPath) || servletPath.startsWith(PROTECTED_PATH + "/");
    return !isProtectedPath;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    String token = extractToken(authorizationHeader);
    if (!StringUtils.hasText(token)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing bearer token");
      return;
    }

    try {
      if (!jwtService.isValid(token)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid bearer token");
        return;
      }
      String username = jwtService.extractUsername(token);
      if (!StringUtils.hasText(username)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid bearer token");
        return;
      }
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(username, null, List.of());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);
    } catch (JwtException | IllegalArgumentException exception) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid bearer token");
    } finally {
      SecurityContextHolder.clearContext();
    }
  }

  private String extractToken(String authorizationHeader) {
    if (!StringUtils.hasText(authorizationHeader)
        || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      return null;
    }
    return authorizationHeader.substring(BEARER_PREFIX.length());
  }
}
