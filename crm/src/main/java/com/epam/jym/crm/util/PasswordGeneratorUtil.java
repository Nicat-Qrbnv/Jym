package com.epam.jym.crm.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.NonNull;

public final class PasswordGeneratorUtil {

  private static final int MIN_PASSWORD_LENGTH = 4;
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final String CONSONANTS = "bcdfghjklmnpqrstvwxyz";
  private static final String VOWELS = "aeiou";
  private static final String DIGITS = "0123456789";
  private static final String SPECIAL_CHARACTERS = "!@#$%^&*_";

  public static String generateRandomPassword(int length) {
    if (length < MIN_PASSWORD_LENGTH) {
      throw new IllegalArgumentException(
          "password length must be at least " + MIN_PASSWORD_LENGTH);
    }

    List<String> passwordPieces = new ArrayList<>(5);
    passwordPieces.add(String.valueOf(randomChar(DIGITS)));
    passwordPieces.add(String.valueOf(randomChar(SPECIAL_CHARACTERS)));
    passwordPieces.add(getRandomWord(length));
    passwordPieces.add(getRandomWord(length));
    if (length % 2 == 1) {
      passwordPieces.add(String.valueOf(randomChar(SPECIAL_CHARACTERS)));
    }

    Collections.shuffle(passwordPieces, RANDOM);

    return passwordPieces.stream()
        .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
        .toString();
  }

  private static @NonNull String getRandomWord(int length) {
    StringBuilder word = new StringBuilder(length);
    int pronounceableLength = (length - 2) / 2;

    for (int i = 0; i < pronounceableLength; i++) {
      char randomChar = randomChar(i % 2 == 0 ? CONSONANTS : VOWELS);
      word.append(RANDOM.nextBoolean() ? Character.toUpperCase(randomChar) : randomChar);
    }
    return word.toString();
  }

  private static char randomChar(String characters) {
    return characters.charAt(RANDOM.nextInt(characters.length()));
  }
}
