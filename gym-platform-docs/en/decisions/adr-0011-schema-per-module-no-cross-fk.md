# ADR-0011: Schema-per-Module, No Cross-Module FK

> English version. Vietnamese (canonical): [`../../vi/decisions/adr-0011-schema-per-module-no-cross-fk.md`](../../vi/decisions/adr-0011-schema-per-module-no-cross-fk.md).

## Status
Accepted

## Context
The platform is a Modular Monolith now but is expected to extract some modules into microservices later. With all tables in one `public` schema and ~84 cross-module foreign keys, modules were tightly coupled at the DB level — a hard blocker for extraction (a microservice cannot hold a FK into another service's database).

## Decision
Each module owns its own PostgreSQL **schema** (`identity`, `branch`, `staff`, `member`, … `messaging`). **Foreign keys are allowed only within the same schema/module.** All cross-module references are **logical references** — plain `BIGINT` ID columns (indexed), with integrity enforced in the application layer (and via domain events / outbox where appropriate). `flyway_schema_history` stays in `public`.

## Consequences
Positive: modules are extraction-ready; clear bounded contexts; no DB-level cross-service coupling. Trade-offs: loses DB referential integrity for cross-module refs (must validate in app); SQL must be schema-qualified; orphan cleanup needs app/event handling.

## Rules
- Intra-module FK: keep. Cross-module FK: forbidden → use ID column + app validation.
- Native SQL must be schema-qualified (`FROM member.member_profile`).
- Verified invariant: count of FKs where child schema ≠ parent schema must be **0**.
- Schema map + logical reference inventory: `data-model/module-schemas.md`.
- Supersedes the earlier "single public schema + table prefix" note in `data-model/README.md` / `database-guideline.md`.
