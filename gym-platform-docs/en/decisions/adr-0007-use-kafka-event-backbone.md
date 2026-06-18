# ADR-0007: Async Eventing — Transactional Outbox now, Kafka later

> English version. Vietnamese (canonical): [`../../vi/decisions/adr-0007-use-kafka-event-backbone.md`](../../vi/decisions/adr-0007-use-kafka-event-backbone.md).

## Status
Proposed — Outbox adopted now; Kafka deferred.

## Context
Side-effects (notifications, audit, reporting, CRM follow-up) must not endanger core transactional flows (payment, booking, check-in, quota, stock), which need strong consistency in PostgreSQL. We want to capture domain events reliably from day one, but introducing a Kafka broker now adds operational cost before async consumers are actually needed.

## Decision
Adopt the **Transactional Outbox now**: business modules append events to an `outbox_event` table within the same DB transaction as the business change. **Kafka is deferred ("later")**: when async delivery is needed, add an **Outbox Relay** (polling first, **Debezium CDC** later) that publishes committed events to **Kafka** for idempotent consumers. Core consistency-critical decisions remain transactional in PostgreSQL and are never moved into Kafka.

## Consequences
Positive: events are durable from day one; no broker to operate yet; the outbox is a stable seam, so adding Kafka later is non-breaking; consistency preserved. Trade-offs: a relay must be built when Kafka arrives; consumers need idempotency/dedupe; projections become eventually consistent once Kafka is live.

## Rules
- The `outbox_event` row is written in the **same transaction** as the aggregate change (no event without commit).
- Each event has a unique id; future consumers dedupe (idempotency table).
- Do NOT move payment/booking/quota/stock decisions into eventing — keep them transactional in PostgreSQL (`CLAUDE.md` Race Condition Protection).
- Topics (when Kafka lands) keyed by aggregate id for ordering; propagate trace context via headers (ADR-0008).
- Supersedes the earlier "Kafka as immediate backbone" framing.
