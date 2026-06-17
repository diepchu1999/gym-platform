# Architecture Overview

## Direction
The project uses a Spring Boot Modular Monolith for the first phase.

The business domain is broad but highly connected. Member, membership, contract, payment, booking, check-in, quota, and inventory require transactional consistency. Starting with microservices would add unnecessary distributed transaction and deployment complexity too early.

## High-Level System

```text
React + TypeScript Web Application
        |
        v
Spring Boot Modular Monolith API
        |
        v
PostgreSQL
```

Future supporting infrastructure may include background jobs and a managed object-storage / CDN.

> **Adopted supporting infrastructure** (see [`solution-architecture.md`](solution-architecture.md) + ADR-0006…0010). Still a **Modular Monolith** (one deployable):
> - **Keycloak** — authentication / OIDC; branch-scoped authorization stays in the app `identity` module (ADR-0006).
> - **PostgreSQL** — source of truth (transactions, atomic SQL, constraints) + `outbox_event`.
> - **Redis** — cache + short-lived locks: QR token TTL, one-time nonce, duplicate-scan lock, rate limiting; durable uniqueness stays in PostgreSQL (ADR-0009).
> - **Object Storage (S3-compatible)** — documents/images (CCCD, contract PDF, invoices, media); DB stores object key only (ADR-0010).
> - **Transactional Outbox** now; **Kafka** async backbone **later** (ADR-0007).
> - **Prometheus + Grafana** (metrics) and **Zipkin** (tracing); **Loki/ELK** logs later (ADR-0008).

## Backend Architecture Style
Use SOLID principles and a practical Clean/Hexagonal Architecture style.

Preferred flow:

```text
Controller
-> Application Use Case / Application Service
-> Domain Model / Domain Service
-> Port / Repository Interface
-> Infrastructure Adapter / Native SQL Repository
-> PostgreSQL
```

## Backend Module Structure

```text
com.gym
 ├── identity
 ├── branch
 ├── staff
 ├── member
 ├── kyc
 ├── membership
 ├── contract
 ├── payment
 ├── finance
 ├── checkin
 ├── booking
 ├── groupclass
 ├── pt
 ├── privateroom
 ├── massage
 ├── inventory
 ├── pantry
 ├── equipment
 ├── crm
 ├── rating
 ├── promotion
 ├── notification
 ├── report
 ├── audit
 └── shared
```

## Layer Responsibilities

### Controller
- Handles HTTP mapping.
- Validates request shape/basic input.
- Converts request DTO to command/query object.
- Does not contain business logic.
- Does not call repositories directly.

### Application Service / Use Case
- Owns use case orchestration.
- Owns transaction boundary.
- Calls domain services and repository ports.
- Coordinates other modules through interfaces/events.

### Domain
- Owns business rules.
- Contains entities, value objects, domain policies, and domain services.
- Should not know HTTP, SQL, or framework details.

### Repository Port
- Interface required by application/domain.
- Describes persistence need without exposing SQL implementation details.

### Native SQL Repository Adapter
- Implements repository ports using Native SQL.
- Prefer `NamedParameterJdbcTemplate` initially unless later ADR changes it.
- Uses parameter binding.
- Handles row mapping.
- Does not contain business decisions beyond persistence concerns.

## Database
PostgreSQL is the source of truth.

Use Native SQL for business persistence. Do not use JPA repositories unless a later explicit decision changes this.

Use Flyway for schema migration unless a later decision changes this.

## Frontend
Frontend uses React + TypeScript as web application first.

Initial products:
- Admin web.
- Staff web screens inside admin or separate route group.
- Member web later if needed.

Mobile app is not part of the first technical baseline.

## Future Extraction Candidates
Only extract services after real scale pressure or team-size pressure appears.

Easier to extract later:
- Notification service.
- Reporting service.
- File service.
- Payment integration service.
- Partner/product integration service.

Core modules should stay in the monolith longer:
- Member.
- Membership.
- Contract.
- Booking.
- Check-in.
- Payment core.
