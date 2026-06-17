-- P8 Audit log (schema: audit, append-only, KHÔNG xoá - CLAUDE.md). Ref: data-model/p8-...md
-- Không FK (entity_id/actor_id là ID logic xuyên module).

CREATE TABLE audit.audit_log (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    actor_type  VARCHAR(10) NOT NULL CHECK (actor_type IN ('STAFF','MEMBER','SYSTEM')),
    actor_id    BIGINT,
    action      VARCHAR(60) NOT NULL,
    entity_type VARCHAR(60) NOT NULL,
    entity_id   BIGINT,
    before_data JSONB,
    after_data  JSONB,
    ip_address  VARCHAR(45),
    created_at  timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_audit_entity ON audit.audit_log(entity_type, entity_id);
CREATE INDEX ix_audit_actor  ON audit.audit_log(actor_type, actor_id);
CREATE INDEX ix_audit_created ON audit.audit_log(created_at);
