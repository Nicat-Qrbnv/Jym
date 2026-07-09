package com.epam.jym.jwthandler.service.impl;

import com.epam.jym.jwthandler.config.JwtProperties;
import com.epam.jym.jwthandler.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;

public class JwtServiceImpl implements JwtService {
  private final String secret;
  private final Duration expiration;
  private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

  public JwtServiceImpl(JwtProperties jwtProperties) {
    secret = jwtProperties.secret();
    expiration = jwtProperties.expiration();
  }

  @Override
  public String generateToken(String username) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(expiration);
    return Jwts.builder()
        .subject(username)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(signingKey())
        .compact();
  }

  @Override
  public String extractUsername(String token) {
    return claims(token).getSubject();
  }

  @Override
  public boolean isValid(String token, String username) {
    removeExpiredRevokedTokens();
    try {
      Claims claims = claims(token);
      String usernameFromToken = claims.getSubject();
      return usernameFromToken.equals(username)
          && !isExpired(claims)
          && !revokedTokens.containsKey(token);
    } catch (ExpiredJwtException ignored) {
      return false;
    }
  }

  @Override
  public boolean isValid(String token) {
    return isValid(token, extractUsername(token));
  }

  private boolean isExpired(Claims claims) {
    return claims.getExpiration().before(new Date());
  }

  @Override
  public void revokeToken(String token) {
    revokedTokens.put(token, Instant.now().plus(expiration));
  }

  @Override
  public long expirationSeconds() {
    return expiration.toSeconds();
  }

  private Claims claims(String token) {
    return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey signingKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private void removeExpiredRevokedTokens() {
    Instant now = Instant.now();
    revokedTokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
  }
}
