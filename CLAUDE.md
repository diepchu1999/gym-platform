# CLAUDE.md

## Project
This repository contains a multi-branch GYM management platform for Ho Chi Minh City.

Business baseline is already agreed. Do not reinterpret or silently change business rules. If a business rule is unclear, ask the owner before coding.

The system supports:
- Multi-branch membership access across all branches.
- Member registration, CCCD KYC, student verification, and 7-day trial.
- Monthly, quarterly, yearly, and VIP packages.
- QR check-in for gym access.
- Group class pass booking.
- PT 1-on-1 booking.
- VIP private room booking with monthly quota.
- VIP massage booking with weekly free quota.
- Product, supplement, and pantry sales.
- Equipment and maintenance management.
- Staff, RBAC, contract, payment, reporting, CRM, notification, and audit log.

## Required Reading Before Coding
Before implementing any feature, read this file first. Then read only the docs relevant to the current task.

Core business docs:
- `gym-platform-docs/vi/business/GYM_Business_Requirement_Document_v1.md`
- `gym-platform-docs/vi/business/domain-map.md`
- `gym-platform-docs/vi/business/business-rules.md`
- `gym-platform-docs/vi/business/status-flow.md`
- `gym-platform-docs/vi/business/glossary.md`

Core architecture docs:
- `gym-platform-docs/vi/architecture/architecture-overview.md`
- `gym-platform-docs/vi/architecture/modular-monolith.md`
- `gym-platform-docs/vi/architecture/backend-guideline.md`
- `gym-platform-docs/vi/architecture/database-guideline.md`
- `gym-platform-docs/vi/architecture/frontend-guideline.md`
- `gym-platform-docs/vi/architecture/development-guideline.md`
- `gym-platform-docs/vi/architecture/api-guideline.md`
- `gym-platform-docs/vi/architecture/security-guideline.md`

Module docs:
- `gym-platform-docs/vi/modules/member-kyc.md`
- `gym-platform-docs/vi/modules/package-contract-payment.md`
- `gym-platform-docs/vi/modules/checkin.md`
- `gym-platform-docs/vi/modules/booking-engine.md`
- `gym-platform-docs/vi/modules/pt-booking.md`
- `gym-platform-docs/vi/modules/group-class.md`
- `gym-platform-docs/vi/modules/private-room.md`
- `gym-platform-docs/vi/modules/massage.md`
- `gym-platform-docs/vi/modules/inventory-pantry.md`
- `gym-platform-docs/vi/modules/equipment-maintenance.md`
- `gym-platform-docs/vi/modules/staff-rbac.md`
- `gym-platform-docs/vi/modules/crm-customer-care.md`

Technical decisions:
- `gym-platform-docs/vi/decisions/adr-0001-use-modular-monolith.md`
- `gym-platform-docs/vi/decisions/adr-0002-use-spring-boot.md`
- `gym-platform-docs/vi/decisions/adr-0003-use-postgresql.md`
- `gym-platform-docs/vi/decisions/adr-0004-use-native-sql-instead-of-jpa.md`
- `gym-platform-docs/vi/decisions/adr-0005-use-react-typescript-web-first.md`

## Architecture Rule
Use Modular Monolith first.

Do not create microservices unless explicitly requested.

Backend must follow SOLID principles and a Clean/Hexagonal Architecture style where practical.

Backend flow:

```text
HTTP Controller
-> Application Use Case / Application Service
-> Domain Model / Domain Service
-> Port / Repository Interface
-> Adapter / Native SQL Repository Implementation
-> PostgreSQL
```

Modules must not directly access repositories of other modules. Communicate through application services, domain services, interfaces, query services, or domain events.

## Locked Technical Baseline
Backend:
- Java.
- Spring Boot.
- Modular Monolith.
- SOLID principles.
- Clean Architecture / Hexagonal Architecture style.
- PostgreSQL as the main database.
- Native SQL for business persistence.
- Do not use JPA repositories for business queries unless explicitly approved later.
- Prefer `NamedParameterJdbcTemplate` at the beginning unless a later ADR changes it.
- Use Flyway for database migration unless a later ADR changes it.

Supporting infrastructure (approved — still Modular Monolith, one deployable):
- Keycloak for authentication / OIDC; branch-scoped authorization stays in the app `identity` module (ADR-0006).
- Redis for cache + short-lived locks (QR token TTL, one-time nonce, duplicate-scan lock, rate limiting); durable uniqueness stays in PostgreSQL (ADR-0009).
- S3-compatible Object Storage for documents/images (CCCD, contract PDF, invoices, media); DB stores object key only (ADR-0010).
- Transactional Outbox now; Kafka as async backbone later (ADR-0007).
- Observability: Prometheus + Grafana (metrics) and Zipkin (tracing) via Micrometer; Loki/ELK for logs later (ADR-0008).
- Full design: `gym-platform-docs/vi/architecture/solution-architecture.md`.

Frontend:
- React.
- TypeScript.
- Web application first.
- No mobile app in the initial phase.
- Admin web and member web may be implemented as separate apps or route groups depending on project setup.

Documentation rule:
- Every new technical decision must update the relevant Markdown docs.
- If the decision is architectural or hard to reverse, create or update an ADR in `gym-platform-docs/vi/decisions/`.
- Do not leave important rules only in chat.

## Critical Business Rules
- All main gym packages can be used across all branches.
- Monthly, quarterly, and yearly packages have unlimited check-in.
- Trial package is free for 7 days, requires CCCD KYC, and allows only 1 check-in per day.
- Trial includes 1 group class trial session.
- VIP can use private rooms but must book first.
- Private room booking max duration is 2 hours.
- VIP private room usage is quota-based per month.
- VIP gets 3 free massage bookings per week.
- Group class is an add-on sold by number of sessions.
- PT is 1-on-1, 90 minutes per session, available from 06:00 to 22:00.
- Pantry sells to all members from 06:00 to 22:00.
- Installment payment is only for quarterly/yearly packages and is handled via finance providers.
- Customer can cancel booking at least 10 hours before start time to get refund/session/quota back.
- If customer does not arrive on time, CSKH calls and may hold the slot for up to 30 minutes. After that, mark as NO_SHOW.

## Race Condition Protection
Always protect:
- Class slot booking.
- PT booking.
- Private room booking.
- Massage booking.
- QR duplicate scan.
- Payment callback.
- Trial CCCD uniqueness.
- Class pass/session deduction.
- VIP quota deduction.
- Product/pantry stock deduction.

Use PostgreSQL constraints, transactions, atomic SQL updates, exclusion constraints, optimistic locking columns, or idempotency keys where appropriate.

## Output Expectation
When implementing a feature:
1. Explain the business rule being implemented.
2. Identify affected modules.
3. Propose DB changes/migration.
4. Propose API endpoints.
5. Implement service logic.
6. Add validation and edge cases.
7. Add tests.
8. Update docs if behavior or technical decisions change.

## Development Discipline
Do not change business rules silently.

Do not put business logic in controllers.

Do not expose database rows directly as API responses.

Do not use JPA repositories unless explicitly approved.

Do not modify old migrations after they have been applied. Create new migrations.

Do not remove audit logs.

Do not implement payment, booking, check-in, quota, or inventory deduction without transaction/race-condition protection.
