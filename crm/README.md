# CRM Module Description

This module manages CRM information of trainers and trainees.

Authentication is enforced by the gateway. CRM reads the authenticated username from the
`X-Authenticated-User` header for secured operations.

## Spring profiles

The application supports the following environments through Spring profiles:

- `local`: default profile for local development. Uses `postgres/postgres` and `jdbc:postgresql://localhost:5433/crm` unless overridden.
- `dev`: development deployment profile. Requires `DB_USERNAME`, `DB_PASSWORD`, and `DB_URL`.
- `stg`: staging deployment profile. Requires `DB_USERNAME`, `DB_PASSWORD`, and `DB_URL`.
- `prod`: production deployment profile. Requires `DB_USERNAME`, `DB_PASSWORD`, and `DB_URL`; Swagger UI and API docs are disabled.

Eureka remains publicly reachable in `local` and `dev`.

Run with a specific profile:

```bash
SPRING_PROFILES_ACTIVE=dev java -jar target/crm-0.0.1-SNAPSHOT.jar
```

For Docker Compose, `local` is active by default. Override it when needed:

```bash
SPRING_PROFILES_ACTIVE=stg docker compose up --build
```

## Actuator

The CRM service exposes the following Spring Boot Actuator endpoints:

- `/actuator/health`
- `/actuator/info`
- `/actuator/prometheus`
- `/actuator/crm-metrics`
- `/actuator/error-logs`
- `/actuator/health/liveness`
- `/actuator/health/readiness`

CRM health group indicators:

- `db`
- `liquibaseMigration`

With the `local` profile, custom health indicators are visible in:

- `/actuator/health`
- `/actuator/health/crm`

Custom CRM metrics response:

```json
{
  "activeUsers": 13,
  "trainersBySpecialization": {
    "Non-technical": 2,
    "Technical": 2
  }
}
```

Error log summary response:

```json
{
  "errorCount": 1,
  "lastError": {
    "timestamp": "2026-06-04T08:15:30Z",
    "logger": "com.epam.jym.crm.service.impl.TrainingServiceImpl",
    "message": "Failed to create training",
    "exceptionClass": "java.lang.IllegalStateException",
    "exceptionMessage": "Training type is not active"
  }
}
```
