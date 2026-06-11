package com.epam.jym.crm.util;

import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.exception.InvalidCredentialsException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CredentialsHeaderParser {
  private static final String BASIC_PREFIX = "Basic ";

  public static CredentialsDto parse(String credentialsHeader) {
    if (credentialsHeader == null) {
      throw new InvalidCredentialsException("Authorization header is required");
    }

    String credentials = credentialsHeader;
    if (credentialsHeader.startsWith(BASIC_PREFIX)) {
      credentials = decodeBasicCredentials(credentialsHeader.substring(BASIC_PREFIX.length()));
    }

    String[] parts = credentials.split(":", 2);
    if (parts.length != 2) {
      throw new InvalidCredentialsException(
          "Authorization header must be in username:password format");
    }
    return new CredentialsDto(parts[0], parts[1]);
  }

  private static String decodeBasicCredentials(String encodedCredentials) {
    try {
      byte[] decodedCredentials = Base64.getDecoder().decode(encodedCredentials);
      return new String(decodedCredentials, StandardCharsets.UTF_8);
    } catch (IllegalArgumentException exception) {
      throw new InvalidCredentialsException(
          "Authorization header must be in Basic username:password format");
    }
  }
}
