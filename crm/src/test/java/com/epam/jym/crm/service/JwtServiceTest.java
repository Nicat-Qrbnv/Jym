package com.epam.jym.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.epam.jym.crm.config.JwtProperties;
import io.jsonwebtoken.JwtException;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

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
    UserDetails userDetails = userDetails(USERNAME);

    assertThat(jwtService.isValid(token, userDetails)).isTrue();
  }

  @Test
  void isValidShouldReturnFalseForDifferentUser() {
    JwtService jwtService = createJwtService(Duration.ofMinutes(30));
    String token = jwtService.generateToken(USERNAME);
    UserDetails anotherUser = userDetails("jane.doe");

    assertThat(jwtService.isValid(token, anotherUser)).isFalse();
  }

  @Test
  void isValidShouldReturnFalseForExpiredToken() {
    JwtService jwtService = createJwtService(Duration.ofSeconds(-10));
    String token = jwtService.generateToken(USERNAME);
    UserDetails userDetails = userDetails(USERNAME);

    assertThat(jwtService.isValid(token, userDetails)).isFalse();
  }

  @Test
  void isValidShouldReturnFalseForRevokedToken() {
    JwtService jwtService = createJwtService(Duration.ofMinutes(30));
    String token = jwtService.generateToken(USERNAME);
    UserDetails userDetails = userDetails(USERNAME);

    assertThat(jwtService.isValid(token, userDetails)).isTrue();

    jwtService.revokeToken(token);

    assertThat(jwtService.isValid(token, userDetails)).isFalse();
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
    return new JwtService(new JwtProperties(SECRET, expiration));
  }

  private UserDetails userDetails(String username) {
    return User.withUsername(username).password("password").roles("USER").build();
  }
}
