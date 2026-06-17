-- P6 Massage (schema: massage). Ref: data-model/p6-booking-verticals.md (D)
-- Intra FK giữ: massage_booking -> massage_room/massage_service. Logical refs: branch, room, staff, member, booking.

CREATE TABLE massage.massage_service (
    id                       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code                     VARCHAR(40)  NOT NULL UNIQUE,
    name                     VARCHAR(150) NOT NULL,
    internal_duration_minutes INT         NOT NULL CHECK (internal_duration_minutes > 0),
    price                    NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (price >= 0),
    currency                 VARCHAR(3)   NOT NULL DEFAULT 'VND',
    active                   BOOLEAN      NOT NULL DEFAULT true,
    created_at               timestamptz  NOT NULL DEFAULT now(),
    updated_at               timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE massage.massage_room (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    branch_id  BIGINT      NOT NULL,                 -- logical ref -> branch
    room_id    BIGINT,                               -- logical ref -> branch.branch_room
    name       VARCHAR(100),
    status     VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','CLEANING','MAINTENANCE','CLOSED')),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_massage_room_branch ON massage.massage_room(branch_id);

CREATE TABLE massage.massage_staff_availability (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    staff_id    BIGINT   NOT NULL,                   -- logical ref -> staff
    branch_id   BIGINT   NOT NULL,                   -- logical ref -> branch
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    start_time  TIME     NOT NULL,
    end_time    TIME     NOT NULL,
    CONSTRAINT ck_massage_avail_time CHECK (end_time > start_time)
);
CREATE INDEX ix_massage_avail_staff ON massage.massage_staff_availability(staff_id);

CREATE TABLE massage.massage_weekly_usage (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id       BIGINT NOT NULL,                 -- logical ref -> member
    week_start_date DATE   NOT NULL,
    free_used_count INT    NOT NULL DEFAULT 0 CHECK (free_used_count >= 0),
    created_at      timestamptz NOT NULL DEFAULT now(),
    updated_at      timestamptz NOT NULL DEFAULT now(),
    UNIQUE (member_id, week_start_date)
);

CREATE TABLE massage.massage_booking (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id         BIGINT       NOT NULL UNIQUE, -- logical ref -> booking.booking
    massage_service_id BIGINT       NOT NULL REFERENCES massage.massage_service(id), -- intra
    massage_room_id    BIGINT       NOT NULL REFERENCES massage.massage_room(id),    -- intra
    massage_staff_id   BIGINT       NOT NULL,        -- logical ref -> staff
    free_quota_used    BOOLEAN      NOT NULL DEFAULT false,
    paid_amount        NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (paid_amount >= 0),
    created_at         timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_massage_booking_room  ON massage.massage_booking(massage_room_id);
CREATE INDEX ix_massage_booking_staff ON massage.massage_booking(massage_staff_id);

SELECT public.apply_updated_at_triggers();
