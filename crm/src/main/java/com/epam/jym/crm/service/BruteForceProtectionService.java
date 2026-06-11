package com.epam.jym.crm.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BruteForceProtectionService {

  private static final int MAX_FAILED_ATTEMPTS = 3;
  private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);

  private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();
  private final Clock clock;

  public BruteForceProtectionService() {
    this(Clock.systemUTC());
  }

  BruteForceProtectionService(Clock clock) {
    this.clock = clock;
  }

  public boolean isBlocked(String username) {
    if (!StringUtils.hasText(username)) {
      return false;
    }

    LoginAttempt attempt = attempts.get(username);
    if (attempt == null || attempt.blockedUntil() == null) {
      return false;
    }
    if (Instant.now(clock).isBefore(attempt.blockedUntil())) {
      return true;
    }

    attempts.remove(username, attempt);
    return false;
  }

  public void registerFailure(String username) {
    if (!StringUtils.hasText(username)) {
      return;
    }

    attempts.compute(
        username,
        (_, currentAttempt) -> {
          if (isCurrentlyBlocked(currentAttempt)) {
            return currentAttempt;
          }

          int failedAttempts = 1 + LoginAttempt.getFailedAttempts(currentAttempt);
          Instant blockedUntil = calculateBlockedUntil(failedAttempts);
          return new LoginAttempt(failedAttempts, blockedUntil);
        });
  }

  private @Nullable Instant calculateBlockedUntil(int failedAttempts) {
    return failedAttempts >= MAX_FAILED_ATTEMPTS ? Instant.now(clock).plus(BLOCK_DURATION) : null;
  }

  private boolean isCurrentlyBlocked(LoginAttempt currentAttempt) {
    return currentAttempt != null
        && currentAttempt.blockedUntil() != null
        && Instant.now(clock).isBefore(currentAttempt.blockedUntil());
  }

  public void registerSuccess(String username) {
    if (StringUtils.hasText(username)) {
      attempts.remove(username);
    }
  }

  private record LoginAttempt(int failedAttempts, Instant blockedUntil) {
    static int getFailedAttempts(LoginAttempt attempt) {
      return attempt == null ? 0 : attempt.failedAttempts();
    }
  }
}
