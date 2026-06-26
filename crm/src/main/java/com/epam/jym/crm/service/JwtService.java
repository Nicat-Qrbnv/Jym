package com.epam.jym.crm.service;

import com.epam.jym.crm.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Map;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtProperties jwtProperties;
  private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

  public String generateToken(String username) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(jwtProperties.expiration());
    return Jwts.builder()
        .subject(username)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(signingKey())
        .compact();
  }

  public String extractUsername(String token) {
    return claims(token).getSubject();
  }

  public boolean isValid(String token, UserDetails userDetails) {
    removeExpiredRevokedTokens();
    try {
      String username = extractUsername(token);
      return username.equals(userDetails.getUsername())
          && claims(token).getExpiration().after(new Date())
          && !revokedTokens.containsKey(token);
    } catch (ExpiredJwtException ignored) {
      return false;
    }
  }

  public void revokeToken(String token) {
    revokedTokens.put(token, Instant.now().plus(jwtProperties.expiration()));
  }

  public long expirationSeconds() {
    return jwtProperties.expiration().toSeconds();
  }

  private Claims claims(String token) {
    return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey signingKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private void removeExpiredRevokedTokens() {
    Instant now = Instant.now();
    revokedTokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
  }
}
