package com.epam.jym.crm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.exception.InvalidCredentialsException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;

class CredentialsHeaderParserTest {

  @Test
  void parseShouldReturnCredentialsWhenHeaderHasUsernameAndPassword() {
    CredentialsDto credentials = CredentialsHeaderParser.parse("john.doe:password123");

    assertEquals("john.doe", credentials.username());
    assertEquals("password123", credentials.password());
  }

  @Test
  void parseShouldKeepColonInsidePassword() {
    CredentialsDto credentials = CredentialsHeaderParser.parse("john.doe:pass:word");

    assertEquals("john.doe", credentials.username());
    assertEquals("pass:word", credentials.password());
  }

  @Test
  void parseShouldReturnCredentialsWhenHeaderHasBasicCredentials() {
    String encodedCredentials =
        Base64.getEncoder().encodeToString("john.doe:password123".getBytes(StandardCharsets.UTF_8));

    CredentialsDto credentials = CredentialsHeaderParser.parse("Basic " + encodedCredentials);

    assertEquals("john.doe", credentials.username());
    assertEquals("password123", credentials.password());
  }

  @Test
  void parseShouldThrowExceptionWhenHeaderIsNull() {
    InvalidCredentialsException exception =
        assertThrows(InvalidCredentialsException.class, () -> CredentialsHeaderParser.parse(null));

    assertEquals("Authorization header is required", exception.getMessage());
  }

  @Test
  void parseShouldThrowExceptionWhenHeaderDoesNotContainSeparator() {
    InvalidCredentialsException exception =
        assertThrows(
            InvalidCredentialsException.class,
            () -> CredentialsHeaderParser.parse("john.doe-password123"));

    assertEquals(
        "Authorization header must be in username:password format", exception.getMessage());
  }

  @Test
  void parseShouldThrowExceptionWhenBasicHeaderCannotBeDecoded() {
    InvalidCredentialsException exception =
        assertThrows(
            InvalidCredentialsException.class,
            () -> CredentialsHeaderParser.parse("Basic invalid-credentials"));

    assertEquals(
        "Authorization header must be in Basic username:password format", exception.getMessage());
  }
}

