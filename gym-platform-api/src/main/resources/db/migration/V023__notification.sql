-- P8 Notification. Ref: data-model/p8-crm-rating-promotion-notification-audit.md

CREATE TABLE notification_message (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id     BIGINT      REFERENCES member_profile(id),
    channel       VARCHAR(10) NOT NULL CHECK (channel IN ('EMAIL','SMS','PUSH','ZALO')),
    template_code VARCHAR(60),
    payload       JSONB,
    status        VARCHAR(15) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','SENT','FAILED')),
    scheduled_at  timestamptz,
    sent_at       timestamptz,
    retry_count   INT         NOT NULL DEFAULT 0,
    created_at    timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_notification_member ON notification_message(member_id);
CREATE INDEX ix_notification_status ON notification_message(status, scheduled_at);
