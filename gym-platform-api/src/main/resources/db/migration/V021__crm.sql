-- P8 CRM / Customer Care. Ref: data-model/p8-crm-rating-promotion-notification-audit.md
-- 'lead'/'ticket' đặt tiền tố crm_ để rõ ràng + tránh nhập nhằng từ khoá.

CREATE TABLE crm_lead (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code                 VARCHAR(30)  NOT NULL UNIQUE,
    full_name            VARCHAR(150) NOT NULL,
    phone                VARCHAR(20),
    email                VARCHAR(255),
    source               VARCHAR(60),
    interested_branch_id BIGINT       REFERENCES branch_branch(id),
    interested_service   VARCHAR(60),
    status               VARCHAR(20)  NOT NULL DEFAULT 'NEW'
        CHECK (status IN ('NEW','CONTACTED','INTERESTED','VISITED','TRIAL_REGISTERED','CONVERTED','LOST')),
    assigned_to          BIGINT       REFERENCES staff_staff(id),
    next_follow_up_at    timestamptz,
    converted_member_id  BIGINT       REFERENCES member_profile(id),
    created_at           timestamptz  NOT NULL DEFAULT now(),
    updated_at           timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE crm_care_task (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id         BIGINT      REFERENCES member_profile(id),
    lead_id           BIGINT      REFERENCES crm_lead(id),
    task_type         VARCHAR(30) NOT NULL CHECK (task_type IN ('TRIAL_FOLLOWUP','NO_SHOW_CALL','RENEWAL','COMPLAINT_FOLLOWUP','WELCOME')),
    related_booking_id BIGINT     REFERENCES booking(id),
    assigned_to       BIGINT      REFERENCES staff_staff(id),
    due_at            timestamptz,
    status            VARCHAR(15) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN','IN_PROGRESS','DONE','CANCELLED')),
    result            TEXT,
    note              TEXT,
    created_at        timestamptz NOT NULL DEFAULT now(),
    updated_at        timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_care_task_member ON crm_care_task(member_id);

CREATE TABLE crm_care_note (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id       BIGINT      NOT NULL REFERENCES member_profile(id),
    author_staff_id BIGINT      REFERENCES staff_staff(id),
    note_type       VARCHAR(40),
    note            TEXT        NOT NULL,
    created_at      timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_care_note_member ON crm_care_note(member_id);

CREATE TABLE crm_ticket (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_code VARCHAR(30) NOT NULL UNIQUE,
    member_id   BIGINT      REFERENCES member_profile(id),
    branch_id   BIGINT      REFERENCES branch_branch(id),
    category    VARCHAR(60),
    priority    VARCHAR(10) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW','MEDIUM','HIGH','URGENT')),
    status      VARCHAR(20) NOT NULL DEFAULT 'NEW'
        CHECK (status IN ('NEW','ASSIGNED','IN_PROGRESS','WAITING_CUSTOMER','RESOLVED','CLOSED')),
    assigned_to BIGINT      REFERENCES staff_staff(id),
    description TEXT,
    resolution  TEXT,
    created_at  timestamptz NOT NULL DEFAULT now(),
    updated_at  timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_crm_ticket_member ON crm_ticket(member_id);

SELECT apply_updated_at_triggers();
