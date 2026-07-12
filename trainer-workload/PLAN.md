# Trainer workload integration plan

## Problem and target

Implement `trainer-workload` as a secondary microservice that receives trainer workload changes
from CRM on training create/delete, aggregates monthly trainer hours, stores them in Redis, and
returns monthly summaries via REST. Integrate with existing gateway + Eureka architecture with JWT
between services, circuit breaker, and end-to-end trace logging.

## Confirmed decisions

- Microservice name: `trainer-workload` (no `-service` suffix)
- Communication: synchronous REST from CRM to trainer-workload
- Storage: Redis
- Trace: gateway creates `X-Trace-Id`; downstream services only propagate
- Failure behavior: if trainer-workload update fails, CRM request fails
- Delete reversal key: training `id` is mandatory for DELETE action
- Security: JWT Bearer token is enforced between CRM and trainer-workload

## Current baseline already done

- Gateway now generates and propagates `X-Trace-Id`
- CRM transaction naming migrated to trace naming
- `trainer-workload` module scaffolded
- Stub endpoints exist with static responses
- Gateway route and docker-compose entry for trainer-workload were added

## Next implementation stages

1. **Define workload API contract and DTOs**
   - POST endpoint payload: trainer username/firstName/lastName/isActive/trainingDate/duration/actionType/trainingId
   - GET monthly summary response model: trainer + years[] + months[] + duration summary
   - Add enum `ActionType` (`ADD`, `DELETE`) and validation constraints

2. **Implement Redis-backed domain model**
   - Store monthly aggregates keyed by trainer + year + month
   - Keep trainer profile fields with each aggregate (name, active status)
   - Keep processed training IDs for idempotency and safe delete reversal

3. **Implement workload business logic**
   - `ADD`: create/update month summary and register trainingId
   - `DELETE`: reverse only if trainingId exists; reject invalid reverse attempts
   - Guard against negative totals and duplicate processing

4. **Secure trainer-workload service**
   - Reuse JWT validation approach consistent with current gateway/CRM logic
   - Require Bearer token on workload endpoints
   - Keep actuator/public endpoints aligned with existing conventions

5. **Integrate CRM with trainer-workload**
   - Add CRM outbound client (service discovery URL via `lb://trainer-workload`)
   - Invoke workload update right after successful training create
   - Add/update training deletion flow in CRM and emit DELETE workload event with trainingId

6. **Apply circuit breaker and strict failure policy**
   - Wrap CRM -> trainer-workload update call with circuit breaker
   - If call fails/open circuit: CRM operation returns error (as decided)
   - Keep no silent fallback for workload synchronization

7. **Complete trace logging propagation**
   - Ensure CRM outbound requests include `X-Trace-Id` from current MDC
   - Ensure trainer-workload logs include same trace ID for request lifecycle

8. **Testing and docs**
   - Unit tests for workload aggregation/idempotency/delete reversal
   - Controller tests for contract responses and validation errors
   - CRM integration tests for create/delete + workload call behavior
   - Circuit-breaker failure-path tests
   - Update project docs and compose/env config for Redis and new endpoints

## Risks and notes

- Current CRM has create-training flow but no explicit training-delete endpoint in the same style;
  deletion/cancel flow must be formalized first to emit DELETE action reliably.
- Contract should include `trainingId` even if initial requirement text does not list it, otherwise
  safe DELETE reversal cannot be guaranteed.
