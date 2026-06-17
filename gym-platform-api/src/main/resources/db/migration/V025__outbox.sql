-- P9 Transactional Outbox + consumer idempotency. Ref: data-model/p9-messaging-outbox.md, ADR-0007

CREATE TABLE outbox_event (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id       UUID        NOT NULL DEFAULT gen_random_uuid() UNIQUE,
    aggregate_type VARCHAR(40) NOT NULL,
    aggregate_id   BIGINT      NOT NULL,
    event_type     VARCHAR(60) NOT NULL,
    payload        JSONB       NOT NULL,
    occurred_at    timestamptz NOT NULL DEFAULT now(),
    status         VARCHAR(15) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','PUBLISHED','FAILED')),
    published_at   timestamptz,
    retry_count    INT         NOT NULL DEFAULT 0,
    created_at     timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_outbox_status    ON outbox_event(status, occurred_at);
CREATE INDEX ix_outbox_aggregate ON outbox_event(aggregate_type, aggregate_id);

CREATE TABLE processed_event (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    consumer_name VARCHAR(60) NOT NULL,
    event_id      UUID        NOT NULL,
    processed_at  timestamptz NOT NULL DEFAULT now(),
    UNIQUE (consumer_name, event_id)
);
