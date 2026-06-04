package com.epam.jym.crm.actuator;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LiquibaseMigrationHealthIndicator implements HealthIndicator {

  private static final String CHANGELOG_COUNT_QUERY = "SELECT COUNT(*) FROM databasechangelog";

  private static final String LOCK_COUNT_QUERY =
      "SELECT COUNT(*) FROM databasechangeloglock WHERE locked = true";

  private final JdbcTemplate jdbcTemplate;

  public LiquibaseMigrationHealthIndicator(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Health health() {
    try {
      Long appliedChanges = jdbcTemplate.queryForObject(CHANGELOG_COUNT_QUERY, Long.class);
      Long activeLocks = jdbcTemplate.queryForObject(LOCK_COUNT_QUERY, Long.class);

      Health.Builder status = activeLocks == null || activeLocks == 0 ? Health.up() : Health.down();

      return status
          .withDetail("appliedChanges", appliedChanges)
          .withDetail("activeLocks", activeLocks)
          .build();
    } catch (Exception exception) {
      return Health.down(exception)
          .withDetail("changelogTable", "databasechangelog")
          .withDetail("lockTable", "databasechangeloglock")
          .build();
    }
  }
}
