package com.epam.jym.crm.service;

import com.epam.jym.crm.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtProperties jwtProperties;

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
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername())
        && claims(token).getExpiration().after(new Date());
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
}
