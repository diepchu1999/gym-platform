-- P7 Equipment + Maintenance. Ref: data-model/p7-inventory-pantry-equipment.md

CREATE TABLE equipment_asset (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    asset_code            VARCHAR(40)  NOT NULL UNIQUE,
    name                  VARCHAR(150) NOT NULL,
    category              VARCHAR(60),
    branch_id             BIGINT       NOT NULL REFERENCES branch_branch(id),
    room_id               BIGINT       REFERENCES branch_room(id),
    area                  VARCHAR(60),
    status                VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE','NEED_MAINTENANCE','UNDER_MAINTENANCE','BROKEN','RETIRED')),
    purchase_date         DATE,
    supplier              VARCHAR(150),
    next_maintenance_date DATE,
    qr_code               VARCHAR(80),
    created_at            timestamptz  NOT NULL DEFAULT now(),
    updated_at            timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_equipment_branch ON equipment_asset(branch_id);

CREATE TABLE maintenance_ticket (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_code       VARCHAR(30) NOT NULL UNIQUE,
    equipment_id      BIGINT      NOT NULL REFERENCES equipment_asset(id),
    branch_id         BIGINT      NOT NULL REFERENCES branch_branch(id),
    reporter_type     VARCHAR(10) NOT NULL CHECK (reporter_type IN ('STAFF','MEMBER')),
    reported_by       BIGINT,
    assigned_to       BIGINT      REFERENCES staff_staff(id),
    issue_description TEXT,
    image_url         VARCHAR(255),
    status            VARCHAR(20) NOT NULL DEFAULT 'NEW'
        CHECK (status IN ('NEW','ASSIGNED','IN_PROGRESS','WAITING_CUSTOMER','RESOLVED','CLOSED')),
    priority          VARCHAR(10) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW','MEDIUM','HIGH','URGENT')),
    cost              NUMERIC(14,2) CHECK (cost IS NULL OR cost >= 0),
    resolved_at       timestamptz,
    created_at        timestamptz NOT NULL DEFAULT now(),
    updated_at        timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_ticket_equipment ON maintenance_ticket(equipment_id);

CREATE TABLE maintenance_history (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    equipment_id BIGINT      NOT NULL REFERENCES equipment_asset(id),
    ticket_id    BIGINT      REFERENCES maintenance_ticket(id),
    action       VARCHAR(60) NOT NULL,
    note         TEXT,
    cost         NUMERIC(14,2) CHECK (cost IS NULL OR cost >= 0),
    performed_by BIGINT      REFERENCES staff_staff(id),
    performed_at timestamptz,
    created_at   timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_maint_history_equipment ON maintenance_history(equipment_id);

SELECT apply_updated_at_triggers();
