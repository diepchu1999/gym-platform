# ADR-0009: Use Redis for Cache and Short-Lived Locks

> English version. Vietnamese (canonical): [`../../vi/decisions/adr-0009-use-redis-cache-and-locks.md`](../../vi/decisions/adr-0009-use-redis-cache-and-locks.md).

## Status
Proposed

## Context
QR check-in needs a short-lived, one-time token (TTL 30–60s), duplicate-scan suppression within a few minutes, and the platform needs rate limiting and caching of hot read-mostly data. Doing all of this purely in PostgreSQL adds write churn and latency for inherently ephemeral state.

## Decision
Use **Redis** for **ephemeral, performance-critical** concerns: **QR token TTL**, **one-time nonce**, **duplicate-scan lock**, and **rate limiting**, plus optional caching. **Redis does NOT replace durable database constraints.** Authoritative race protection — 1 trial per CCCD, payment idempotency, class booking uniqueness, stock/quota non-negativity, QR nonce final consumption — remains enforced in **PostgreSQL** (constraints, atomic SQL, transactions). Redis is a fast first gate; PostgreSQL is the source of truth.

## Consequences
Positive: low latency for ephemeral state, natural TTL, simple distributed short locks, rate limiting. Trade-offs: extra infra; Redis is not the system of record (data may be evicted); must avoid relying on Redis alone for correctness-critical uniqueness.

## Rules
- Redis use cases: QR token TTL + one-time nonce, duplicate-scan window lock, rate limiting, read cache.
- Every correctness-critical invariant MUST also be backed by a PostgreSQL constraint/atomic update.
- Locks are short-lived and time-bounded (TTL) to avoid deadlocks on crash.
- Do not store sensitive data (CCCD, tokens) in Redis beyond required TTL.
