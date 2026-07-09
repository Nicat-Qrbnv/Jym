package com.epam.jym.jwthandler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.epam.jym.jwthandler.config.JwtProperties;
import com.epam.jym.jwthandler.service.impl.JwtServiceImpl;
import io.jsonwebtoken.JwtException;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private static final String SECRET =
      "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
  private static final String USERNAME = "john.doe";

  @Test
  @DisplayName("generateToken + extractUsername should preserve username")
  void generateTokenShouldPreserveUsername() {
    JwtService jwtService = createJwtService(Duration.ofMinutes(30));

    String token = jwtService.generateToken(USERNAME);

    assertThat(jwtService.extractUsername(token)).isEqualTo(USERNAME);
  }

  @Test
  void isValidShouldReturnTrueForMatchingUserAndActiveToken() {
    JwtService jwtService = createJwtService(Duration.ofMinutes(30));
    String token = jwtService.generateToken(USERNAME);

    assertThat(jwtService.isValid(token, USERNAME)).isTrue();
  }

  @Test
  void isValidShouldReturnFalseForDifferentUser() {
    JwtService jwtService = createJwtService(Duration.ofMinutes(30));
    String token = jwtService.generateToken(USERNAME);

    assertThat(jwtService.isValid(token, "jane.doe")).isFalse();
  }

  @Test
  void isValidShouldReturnFalseForExpiredToken() {
    JwtService jwtService = createJwtService(Duration.ofSeconds(-10));
    String token = jwtService.generateToken(USERNAME);

    assertThat(jwtService.isValid(token, USERNAME)).isFalse();
  }

  @Test
  void isValidShouldReturnFalseForRevokedToken() {
    JwtService jwtService = createJwtService(Duration.ofMinutes(30));
    String token = jwtService.generateToken(USERNAME);

    assertThat(jwtService.isValid(token, USERNAME)).isTrue();

    jwtService.revokeToken(token);

    assertThat(jwtService.isValid(token, USERNAME)).isFalse();
  }

  @Test
  void expirationSecondsShouldReturnConfiguredDurationInSeconds() {
    JwtService jwtService = createJwtService(Duration.ofMinutes(5));

    assertThat(jwtService.expirationSeconds()).isEqualTo(300L);
  }

  @Test
  void extractUsernameShouldThrowForMalformedToken() {
    JwtService jwtService = createJwtService(Duration.ofMinutes(30));

    assertThatThrownBy(() -> jwtService.extractUsername("not-a-jwt"))
        .isInstanceOfAny(JwtException.class, IllegalArgumentException.class);
  }

  private JwtService createJwtService(Duration expiration) {
    return new JwtServiceImpl(new JwtProperties(SECRET, expiration));
  }
}
