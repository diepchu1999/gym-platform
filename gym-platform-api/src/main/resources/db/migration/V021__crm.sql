-- P8 CRM (schema: crm). Ref: data-model/p8-crm-rating-promotion-notification-audit.md
-- Intra FK giữ: care_task -> lead. Logical refs: member, staff, branch, booking.

CREATE TABLE crm.crm_lead (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code                 VARCHAR(30)  NOT NULL UNIQUE,
    full_name            VARCHAR(150) NOT NULL,
    phone                VARCHAR(20),
    email                VARCHAR(255),
    source               VARCHAR(60),
    interested_branch_id BIGINT,                     -- logical ref -> branch
    interested_service   VARCHAR(60),
    status               VARCHAR(20)  NOT NULL DEFAULT 'NEW'
        CHECK (status IN ('NEW','CONTACTED','INTERESTED','VISITED','TRIAL_REGISTERED','CONVERTED','LOST')),
    assigned_to          BIGINT,                     -- logical ref -> staff
    next_follow_up_at    timestamptz,
    converted_member_id  BIGINT,                     -- logical ref -> member
    created_at           timestamptz  NOT NULL DEFAULT now(),
    updated_at           timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE crm.crm_care_task (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id         BIGINT,                        -- logical ref -> member
    lead_id           BIGINT      REFERENCES crm.crm_lead(id),  -- intra
    task_type         VARCHAR(30) NOT NULL CHECK (task_type IN ('TRIAL_FOLLOWUP','NO_SHOW_CALL','RENEWAL','COMPLAINT_FOLLOWUP','WELCOME')),
    related_booking_id BIGINT,                       -- logical ref -> booking
    assigned_to       BIGINT,                        -- logical ref -> staff
    due_at            timestamptz,
    status            VARCHAR(15) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN','IN_PROGRESS','DONE','CANCELLED')),
    result            TEXT,
    note              TEXT,
    created_at        timestamptz NOT NULL DEFAULT now(),
    updated_at        timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_care_task_member ON crm.crm_care_task(member_id);

CREATE TABLE crm.crm_care_note (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id       BIGINT      NOT NULL,            -- logical ref -> member
    author_staff_id BIGINT,                          -- logical ref -> staff
    note_type       VARCHAR(40),
    note            TEXT        NOT NULL,
    created_at      timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_care_note_member ON crm.crm_care_note(member_id);

CREATE TABLE crm.crm_ticket (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_code VARCHAR(30) NOT NULL UNIQUE,
    member_id   BIGINT,                              -- logical ref -> member
    branch_id   BIGINT,                              -- logical ref -> branch
    category    VARCHAR(60),
    priority    VARCHAR(10) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW','MEDIUM','HIGH','URGENT')),
    status      VARCHAR(20) NOT NULL DEFAULT 'NEW'
        CHECK (status IN ('NEW','ASSIGNED','IN_PROGRESS','WAITING_CUSTOMER','RESOLVED','CLOSED')),
    assigned_to BIGINT,                              -- logical ref -> staff
    description TEXT,
    resolution  TEXT,
    created_at  timestamptz NOT NULL DEFAULT now(),
    updated_at  timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_crm_ticket_member ON crm.crm_ticket(member_id);

SELECT public.apply_updated_at_triggers();
