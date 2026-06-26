package com.epam.jym.crm.service;

import com.epam.jym.crm.config.JwtProperties;
import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

  @Test
  void isValidShouldReturnFalseForRevokedToken() {
    JwtProperties jwtProperties =
        new JwtProperties("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=", Duration.ofMinutes(30));
    JwtService jwtService = new JwtService(jwtProperties);
    String token = jwtService.generateToken("john.doe");
    UserDetails userDetails = User.withUsername("john.doe").password("password").roles("USER").build();

    Assertions.assertThat(jwtService.isValid(token, userDetails)).isTrue();

    jwtService.revokeToken(token);

    Assertions.assertThat(jwtService.isValid(token, userDetails)).isFalse();
  }
}
