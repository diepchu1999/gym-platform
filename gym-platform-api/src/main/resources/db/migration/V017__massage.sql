-- P6 Massage (VIP 3 free/tuần, phòng + nhân viên). Ref: data-model/p6-booking-verticals.md (D)

CREATE TABLE massage_service (
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

CREATE TABLE massage_room (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    branch_id  BIGINT      NOT NULL REFERENCES branch_branch(id),
    room_id    BIGINT      REFERENCES branch_room(id),
    name       VARCHAR(100),
    status     VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','CLEANING','MAINTENANCE','CLOSED')),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_massage_room_branch ON massage_room(branch_id);

CREATE TABLE massage_staff_availability (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    staff_id    BIGINT   NOT NULL REFERENCES staff_staff(id),
    branch_id   BIGINT   NOT NULL REFERENCES branch_branch(id),
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    start_time  TIME     NOT NULL,
    end_time    TIME     NOT NULL,
    CONSTRAINT ck_massage_avail_time CHECK (end_time > start_time)
);
CREATE INDEX ix_massage_avail_staff ON massage_staff_availability(staff_id);

CREATE TABLE massage_weekly_usage (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id       BIGINT NOT NULL REFERENCES member_profile(id),
    week_start_date DATE   NOT NULL,                       -- Thứ 2 đầu tuần
    free_used_count INT    NOT NULL DEFAULT 0 CHECK (free_used_count >= 0),
    created_at      timestamptz NOT NULL DEFAULT now(),
    updated_at      timestamptz NOT NULL DEFAULT now(),
    UNIQUE (member_id, week_start_date)
);

CREATE TABLE massage_booking (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id         BIGINT       NOT NULL UNIQUE REFERENCES booking(id) ON DELETE CASCADE,
    massage_service_id BIGINT       NOT NULL REFERENCES massage_service(id),
    massage_room_id    BIGINT       NOT NULL REFERENCES massage_room(id),
    massage_staff_id   BIGINT       NOT NULL REFERENCES staff_staff(id),
    free_quota_used    BOOLEAN      NOT NULL DEFAULT false,
    paid_amount        NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (paid_amount >= 0),
    created_at         timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_massage_booking_room  ON massage_booking(massage_room_id);
CREATE INDEX ix_massage_booking_staff ON massage_booking(massage_staff_id);

SELECT apply_updated_at_triggers();
