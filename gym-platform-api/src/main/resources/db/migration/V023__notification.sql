-- P8 Notification (schema: notification). Ref: data-model/p8-...md
-- Logical ref: member.

CREATE TABLE notification.notification_message (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id     BIGINT,                            -- logical ref -> member
    channel       VARCHAR(10) NOT NULL CHECK (channel IN ('EMAIL','SMS','PUSH','ZALO')),
    template_code VARCHAR(60),
    payload       JSONB,
    status        VARCHAR(15) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','SENT','FAILED')),
    scheduled_at  timestamptz,
    sent_at       timestamptz,
    retry_count   INT         NOT NULL DEFAULT 0,
    created_at    timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_notification_member ON notification.notification_message(member_id);
CREATE INDEX ix_notification_status ON notification.notification_message(status, scheduled_at);
