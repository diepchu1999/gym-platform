# P9 — Messaging: Transactional Outbox (now) + Consumer Idempotency

> English version. Vietnamese (canonical): [`../../../vi/architecture/data-model/p9-messaging-outbox.md`](../../../vi/architecture/data-model/p9-messaging-outbox.md).

Sources: ADR-0007 (Outbox now / Kafka later), `solution-architecture.md` §7.

## Scope
`outbox_event` (written from day one), `processed_event` (consumer idempotency once Kafka exists). This is the seam that lets Kafka be added later without breaking anything.

## `outbox_event`
| Column | Type | Constraint | Note |
|---|---|---|---|
| id | BIGINT | PK identity | write order |
| event_id | UUID | NOT NULL DEFAULT gen_random_uuid(), UNIQUE | global id (pgcrypto, V001) |
| aggregate_type | VARCHAR(40) | NOT NULL | 'payment','booking','contract',... |
| aggregate_id | BIGINT | NOT NULL | aggregate id (Kafka ordering key later) |
| event_type | VARCHAR(60) | NOT NULL | 'PaymentPaid','BookingConfirmed',... |
| payload | JSONB | NOT NULL | event data |
| occurred_at | timestamptz | NOT NULL DEFAULT now() | |
| status | VARCHAR(15) | NOT NULL DEFAULT 'PENDING', CHECK IN ('PENDING','PUBLISHED','FAILED') | |
| published_at | timestamptz | NULL | |
| retry_count | INT | NOT NULL DEFAULT 0 | |
| created_at | timestamptz | NOT NULL DEFAULT now() | |

- **Core rule**: write `outbox_event` in the **same transaction** as the business change (no event without commit).
- Indexes: `(status, occurred_at)` for relay polling; `(aggregate_type, aggregate_id)`.
- **Now**: an in-process dispatcher may read PENDING for in-monolith handlers (e.g. create `notification_message`).
- **Later**: an Outbox Relay (polling → Debezium CDC) publishes to Kafka and sets `PUBLISHED`.

## `processed_event` (consumer idempotency — once Kafka exists)
id · consumer_name VARCHAR(60) NOT NULL · event_id UUID NOT NULL · processed_at timestamptz NOT NULL DEFAULT now().
- `UNIQUE(consumer_name, event_id)` → exactly-once processing per consumer (insert before processing; duplicate ⇒ skip).

## Why split "now/later"
- `outbox_event` guarantees **no lost events** even before Kafka exists.
- When an async backbone is needed, just add the relay + consumers — **no change** to core business logic.
- Core decisions (payment/booking/quota/stock) stay transactional in PostgreSQL — Kafka only carries resulting facts.

## Planned migrations
`V025__outbox.sql` (outbox_event, processed_event).
