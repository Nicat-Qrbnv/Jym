package com.epam.jym.crm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordGeneratorUtilTest {

  private static final int PASSWORD_LENGTH = 11;
  private static final int EVEN_PASSWORD_LENGTH = 10;
  private static final String ALLOWED_CHARACTERS = "bcdfghjklmnpqrstvwxyzaeiou0123456789!@#$%^&*_";
  private static final String DIGITS = "0123456789";
  private static final String SPECIAL_CHARACTERS = "!@#$%^&*_";

  @Test
  void generateRandomPasswordShouldReturnValidPassword() {
    String password = PasswordGeneratorUtil.generateRandomPassword(PASSWORD_LENGTH);

    assertEquals(PASSWORD_LENGTH, password.length());
    assertTrue(password.chars().allMatch(PasswordGeneratorUtilTest::isAllowedCharacter));
    assertTrue(password.chars().anyMatch(character -> DIGITS.indexOf(character) >= 0));
    assertTrue(password.chars().anyMatch(character -> SPECIAL_CHARACTERS.indexOf(character) >= 0));
  }

  @Test
  void generateRandomPasswordShouldReturnValidPasswordWhenLengthIsEven() {
    String password = PasswordGeneratorUtil.generateRandomPassword(EVEN_PASSWORD_LENGTH);

    assertEquals(EVEN_PASSWORD_LENGTH, password.length());
    assertTrue(password.chars().allMatch(PasswordGeneratorUtilTest::isAllowedCharacter));
    assertTrue(password.chars().anyMatch(character -> DIGITS.indexOf(character) >= 0));
    assertTrue(password.chars().anyMatch(character -> SPECIAL_CHARACTERS.indexOf(character) >= 0));
  }

  @Test
  void generateRandomPasswordShouldThrowExceptionWhenLengthIsTooShort() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> PasswordGeneratorUtil.generateRandomPassword(3));

    assertEquals("password length must be at least 4", exception.getMessage());
  }

  private static boolean isAllowedCharacter(int character) {
    return ALLOWED_CHARACTERS.indexOf(Character.toLowerCase(character)) >= 0;
  }
}
