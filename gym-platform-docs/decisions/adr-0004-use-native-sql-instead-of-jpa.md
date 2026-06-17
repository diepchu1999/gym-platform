# ADR-0004: Use Native SQL Instead of JPA Repositories

## Status
Accepted

## Context
The project has many business-critical operations where SQL control matters: booking slots, quota deduction, stock deduction, payment idempotency, check-in deduplication, and reporting.

The owner prefers Native SQL instead of JPA for this project.

## Decision
Use Native SQL for business persistence. Do not use JPA repositories for business queries unless explicitly approved by a later decision.

Initial preferred implementation is Spring `NamedParameterJdbcTemplate`.

## Consequences
Positive:
- Full control over SQL and transaction-sensitive updates.
- Easier to optimize queries and indexes.
- Easier to implement atomic update patterns.
- Avoids accidental ORM lazy loading and hidden queries.

Trade-offs:
- More manual row mapping.
- More boilerplate for CRUD.
- Developers must keep SQL and domain mapping clean.

## Rules
- Use parameter binding.
- Do not concatenate user input into SQL.
- Keep SQL readable.
- Add tests for critical repository methods.
- Keep business rules in application/domain; repository focuses on persistence.
