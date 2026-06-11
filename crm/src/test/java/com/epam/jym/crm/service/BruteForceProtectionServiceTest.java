package com.epam.jym.crm.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class BruteForceProtectionServiceTest {

  private final MutableClock clock = new MutableClock();
  private final BruteForceProtectionService service = new BruteForceProtectionService(clock);

  @Test
  void registerFailureShouldBlockAfterThreeFailures() {
    service.registerFailure("john.doe");
    service.registerFailure("john.doe");

    assertThat(service.isBlocked("john.doe")).isFalse();

    service.registerFailure("john.doe");

    assertThat(service.isBlocked("john.doe")).isTrue();
  }

  @Test
  void registerSuccessShouldClearFailures() {
    service.registerFailure("john.doe");
    service.registerFailure("john.doe");

    service.registerSuccess("john.doe");
    service.registerFailure("john.doe");

    assertThat(service.isBlocked("john.doe")).isFalse();
  }

  @Test
  void isBlockedShouldReturnFalseAfterBlockExpires() {
    service.registerFailure("john.doe");
    service.registerFailure("john.doe");
    service.registerFailure("john.doe");

    clock.advance(Duration.ofMinutes(5).plusSeconds(1));

    assertThat(service.isBlocked("john.doe")).isFalse();
  }

  private static class MutableClock extends Clock {

    private Instant instant = Instant.parse("2026-06-11T00:00:00Z");

    @Override
    public ZoneId getZone() {
      return ZoneId.of("UTC");
    }

    @Override
    public Clock withZone(ZoneId zone) {
      return this;
    }

    @Override
    public Instant instant() {
      return instant;
    }

    private void advance(Duration duration) {
      instant = instant.plus(duration);
    }
  }
}
