# CRM Module Description

This module manages CRM information of trainers and trainees.

## Spring profiles

The application supports the following environments through Spring profiles:

- `local`: default profile for local development. Uses `postgres/postgres` and `jdbc:postgresql://localhost:5433/crm` unless overridden.
- `dev`: development deployment profile. Requires `DB_USERNAME`, `DB_PASSWORD`, and `DB_URL`.
- `stg`: staging deployment profile. Requires `DB_USERNAME`, `DB_PASSWORD`, and `DB_URL`.
- `prod`: production deployment profile. Requires `DB_USERNAME`, `DB_PASSWORD`, and `DB_URL`; Swagger UI and API docs are disabled.

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
- `/actuator/health/liveness`
- `/actuator/health/readiness`
