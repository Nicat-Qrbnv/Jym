package com.epam.jym.crm.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.epam.jym.crm.actuator.LiquibaseMigrationHealthIndicator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

class LiquibaseMigrationHealthIndicatorIT extends AbstractIntegrationTest {

  @Autowired private LiquibaseMigrationHealthIndicator indicator;

  @Test
  void health_afterMigrations_isUpWithExpectedDetails() {
    Health health = indicator.health();

    assertThat(health.getStatus()).isEqualTo(Status.UP);
    assertThat(health.getDetails()).containsEntry("activeLocks", 0L);
    assertThat((Long) health.getDetails().get("appliedChanges")).isEqualTo(9L);
  }
}
