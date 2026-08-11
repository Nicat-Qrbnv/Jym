# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Important behavioral rules

- Do not run any command unless explicitly asked.
- Do not suppress any warning unless explicitly asked.
- Do not go outside the context of the task.
- Do what the user says, not what seems best, unless explicitly asked otherwise.

## Build commands

Build entire multi-module project (runs Checkstyle + tests):
```bash
mvn verify
```

Build and skip tests:
```bash
mvn install -DskipTests
```

Run tests for a specific module:
```bash
mvn test -pl crm
mvn test -pl trainer-workload
```

Run a single test class:
```bash
mvn test -pl crm -Dtest=TrainerServiceImplTest
```

Checkstyle runs automatically at the `validate` phase using `google_checks.xml`. It covers test sources and fails the build on violations.

## Local infrastructure

Start PostgreSQL, Redis, and ActiveMQ (required for local development):
```bash
docker compose up postgres redis
```

The full stack (including app containers) can be started with:
```bash
APP_VERSION=<version> SECURITY_JWT_SECRET=<secret> docker compose up --build
```

## Architecture overview

This is a Spring Boot microservices project (Java 25, Spring Boot 4.0.6) with five Maven modules under a shared parent `pom.xml`.

### Modules

| Module | Port | Role |
|---|---|---|
| `eureka` | 8761 | Service discovery (Netflix Eureka server) |
| `gateway` | 8080 | API gateway (Spring Cloud Gateway WebFlux); validates JWT, injects `X-Authenticated-User` header |
| `crm` | 8020 | Core domain service; manages trainers, trainees, trainings; PostgreSQL + Liquibase |
| `trainer-workload` | 8030 | Aggregates trainer training duration; stores monthly summaries in Redis |
| `jwt-handler` | — | Shared library (no runnable app); provides `JwtService` used by gateway and crm |

### Request flow

1. Client → **gateway** (JWT validated here; downstream services do not do full JWT auth on HTTP)
2. gateway → **crm** (via Eureka load-balanced routing; username in `X-Authenticated-User`)
3. crm → **trainer-workload** via ActiveMQ JMS queue (`trainer-workload.update`) on training create/delete

### CRM internal structure

CRM uses a **Facade → Service → Repository** layering:
- `CrmFacadeImpl` is the single entry point for all controller operations; it coordinates multiple services and triggers workload notifications.
- Controllers delegate entirely to the facade.
- No MapStruct. A custom `CrmMapper` resolves `Mapper<S, T>` beans by source/target type and calls `mapper.map(source, context)`. Register new mappings by implementing `Mapper<S, T>` and declaring as a Spring bean.
- `@LogOperation` on a class or method triggers `OperationLoggingAspect`, which logs start/complete/failure and duration at DEBUG/WARN level.

### Trainer workload storage

`trainer-workload` has no SQL database. Monthly aggregates are stored in Redis hashes:
- Key: `trainer-workload:aggregates:{trainerUsername}`
- Field: `{year}-{month}`
- Value: JSON-serialized `MonthlyWorkloadAggregate`

`TrainerWorkloadAggregateRepository` manages serialization via `ObjectMapper`.

### Messaging (CRM → trainer-workload)

- CRM sends `TrainerWorkloadUpdateRequest` (JSON) to the ActiveMQ queue on training add/delete.
- The trace ID from `X-Trace-Id` MDC is propagated as a JMS header so logs can be correlated across services.
- `TrainerWorkloadListener` in `trainer-workload` processes messages; on repeated failure it routes to the DLQ (`trainer-workload.update.dlq`) after `messaging.trainer-workload.max-delivery-attempts` (default: 3) attempts.

### Authentication

- `jwt-handler` is a pure library; it is not a Spring Boot app (`spring-boot-maven-plugin` is skipped for it). Import as a Maven dependency.
- Gateway validates JWT and sets `X-Authenticated-User` header. CRM reads that header via `CredentialsHeaderParser` — CRM does not re-validate the JWT on HTTP paths.
- `trainer-workload` validates JWT itself via `JwtAuthenticationFilter` for its REST endpoints (it is independently deployed and not always behind the gateway).

### Spring profiles

CRM profiles (`local` | `dev` | `stg` | `prod`):
- `local` (default): PostgreSQL at `localhost:5433`, debug logging, full actuator detail visible.
- `prod`: Swagger and API docs are disabled.
- All non-local profiles require `DB_USERNAME`, `DB_PASSWORD`, `DB_URL` environment variables.

All services require `SECURITY_JWT_SECRET` at runtime.

## Commit message format

```
## <very compact heading describing the change>

- Key change one.
- Key change two.
```

Bullets should be short, specific, and omit implementation noise. Group related changes into one bullet.

# Response Style

- Short answers.
- Explain only if asked.
- After edits, output:

🪨 <file>
- Do: <3-8 words>
- Why: <1 short sentence>

- One block per changed file.
- Skip unchanged files.
- Caveman style.