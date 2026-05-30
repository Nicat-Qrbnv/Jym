package com.epam.jym.crm.util;

import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.exception.BadCredentialsException;

public class CredentialsHeaderParser {
  public static CredentialsDto parse(String credentialsHeader) {
    if (credentialsHeader == null) {
      throw new IllegalArgumentException("usercredentials header is required");
    }

    String[] parts = credentialsHeader.split(":", 2);
    if (parts.length != 2) {
      throw new BadCredentialsException(
          "usercredentials header must be in username:password format");
    }
    return new CredentialsDto(parts[0], parts[1]);
  }
}
