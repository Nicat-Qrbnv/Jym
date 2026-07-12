package com.epam.jym.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.jwthandler.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

class GatewayAuthFilterTest {

  private static final String SECRET =
      "VGhpcy1pcy1hLWxvY2FsLWRldi1qd3Qtc2VjcmV0LWtleS0zMi1ieXRlcyE=";

  @Test
  void shouldForwardAuthenticatedUsernameForProtectedRequests() {
    GatewayAuthFilter filter = newFilter();
    GatewayFilterChain chain = mock(GatewayFilterChain.class);
    AtomicReference<ServerWebExchange> forwardedExchange = new AtomicReference<>();
    when(chain.filter(any())).thenAnswer(invocation -> {
      forwardedExchange.set(invocation.getArgument(0));
      return Mono.empty();
    });

    String token = createToken();
    MockServerWebExchange exchange =
        MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/crm/v1/trainers/john.doe")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build());

    filter.filter(exchange, chain).block();

    assertThat(forwardedExchange.get()).isNotNull();
    assertThat(forwardedExchange.get().getRequest().getHeaders().getFirst("X-Authenticated-User"))
        .isEqualTo("john.doe");
  }

  @Test
  void shouldRejectProtectedRequestsWithoutToken() {
    GatewayAuthFilter filter = newFilter();
    GatewayFilterChain chain = mock(GatewayFilterChain.class);
    MockServerWebExchange exchange =
        MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/crm/v1/trainers/john.doe").build());

    filter.filter(exchange, chain).block();

    assertThat(exchange.getResponse().getStatusCode()).isNotNull();
    assertThat(exchange.getResponse().getStatusCode().value()).isEqualTo(401);
    verifyNoInteractions(chain);
  }

  private GatewayAuthFilter newFilter() {
    JwtService jwtService = mock(JwtService.class);
    when(jwtService.extractUsername(any())).thenAnswer(invocation -> {
      String token = invocation.getArgument(0);
      return extractUsernameFromToken(token);
    });
    when(jwtService.isValid(any())).thenAnswer(invocation -> {
      String token = invocation.getArgument(0);
      try {
        return isTokenValid(token);
      } catch (Exception e) {
        return false;
      }
    });
    return new GatewayAuthFilter(jwtService);
  }

  private String extractUsernameFromToken(String token) {
    return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  private boolean isTokenValid(String token) {
    try {
      Jwts.parser()
          .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private String createToken() {
    Instant now = Instant.now();
    SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    return Jwts.builder()
        .subject("john.doe")
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(3600)))
        .signWith(signingKey)
        .compact();
  }
}
