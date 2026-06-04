package com.epam.jym.crm.util;

import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.exception.InvalidCredentialsException;

public class CredentialsHeaderParser {
  public static CredentialsDto parse(String credentialsHeader) {
    if (credentialsHeader == null) {
      throw new InvalidCredentialsException("Authorization header is required");
    }

    String[] parts = credentialsHeader.split(":", 2);
    if (parts.length != 2) {
      throw new InvalidCredentialsException(
          "Authorization header must be in username:password format");
    }
    return new CredentialsDto(parts[0], parts[1]);
  }
}
