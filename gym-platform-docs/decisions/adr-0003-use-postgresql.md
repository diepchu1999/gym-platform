# ADR-0003: Use PostgreSQL

## Status
Accepted

## Context
The system needs reliable relational consistency for booking, payment, contract, check-in, quota, and inventory.

## Decision
Use PostgreSQL as the main database.

## Consequences
Positive:
- Strong transactional consistency.
- Strong indexing and constraint support.
- Supports advanced constraints useful for booking and time-range conflicts.
- Good fit for Native SQL.

Trade-offs:
- Requires migration discipline.
- Requires query/index tuning as data grows.

## Rules
- Use Flyway migrations unless changed later.
- Use DB constraints for critical business invariants.
- Use atomic SQL updates for quota/stock/slot counters.
