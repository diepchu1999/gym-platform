-- P5 Booking core (schema: booking). Ref: data-model/p5-booking-core.md
-- Intra FK giữ: resource_slot/hold/event -> booking. Logical refs: member, branch.

CREATE TABLE booking.booking (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_code       VARCHAR(30)  NOT NULL UNIQUE,
    booking_type       VARCHAR(20)  NOT NULL CHECK (booking_type IN ('PT','GROUP_CLASS','PRIVATE_ROOM','MASSAGE')),
    member_id          BIGINT       NOT NULL,        -- logical ref -> member
    branch_id          BIGINT       NOT NULL,        -- logical ref -> branch
    start_time         timestamptz  NOT NULL,
    end_time           timestamptz  NOT NULL,
    status             VARCHAR(30)  NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT','PENDING_PAYMENT','CONFIRMED','WAITING_CUSTOMER_CONFIRMATION','CHECKED_IN','IN_PROGRESS','COMPLETED','CANCELLED','NO_SHOW','EXPIRED','REFUNDED')),
    payment_status     VARCHAR(20)  CHECK (payment_status IN ('UNPAID','PENDING_PAYMENT','PAID','REFUNDED')),
    used_quota_type    VARCHAR(20)  CHECK (used_quota_type IN ('PRIVATE_ROOM_MINUTES','MASSAGE_FREE','CLASS_SESSION','PT_SESSION')),
    used_quota_amount  NUMERIC(10,2),
    cancellation_reason TEXT,
    cancelled_by       VARCHAR(20)  CHECK (cancelled_by IN ('MEMBER','GYM','SYSTEM')),
    no_show_at         timestamptz,
    version            BIGINT       NOT NULL DEFAULT 0,
    created_at         timestamptz  NOT NULL DEFAULT now(),
    updated_at         timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT ck_booking_time CHECK (end_time > start_time)
);
-- BR-018: member không tự đặt trùng giờ
ALTER TABLE booking.booking ADD CONSTRAINT ex_member_overlap
    EXCLUDE USING gist (member_id WITH =, tstzrange(start_time, end_time) WITH &&)
    WHERE (status IN ('CONFIRMED','WAITING_CUSTOMER_CONFIRMATION','CHECKED_IN','IN_PROGRESS'));
CREATE INDEX ix_booking_member_start ON booking.booking(member_id, start_time);
CREATE INDEX ix_booking_branch_start ON booking.booking(branch_id, start_time);
CREATE INDEX ix_booking_status       ON booking.booking(status);

-- BR-017: chống double-book tài nguyên theo khoảng giờ. resource_id là ID logic (trainer/room/staff).
CREATE TABLE booking.booking_resource_slot (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id    BIGINT      NOT NULL REFERENCES booking.booking(id) ON DELETE CASCADE, -- intra
    resource_type VARCHAR(20) NOT NULL CHECK (resource_type IN ('TRAINER','PRIVATE_ROOM','MASSAGE_ROOM','MASSAGE_STAFF')),
    resource_id   BIGINT      NOT NULL,              -- logical ref -> pt/privateroom/massage/staff
    start_time    timestamptz NOT NULL,
    end_time      timestamptz NOT NULL,
    CONSTRAINT ck_slot_time CHECK (end_time > start_time)
);
ALTER TABLE booking.booking_resource_slot ADD CONSTRAINT ex_resource_overlap
    EXCLUDE USING gist (resource_type WITH =, resource_id WITH =, tstzrange(start_time, end_time) WITH &&);
CREATE INDEX ix_slot_booking ON booking.booking_resource_slot(booking_id);

CREATE TABLE booking.booking_hold (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id BIGINT      NOT NULL UNIQUE REFERENCES booking.booking(id) ON DELETE CASCADE, -- intra
    expires_at timestamptz NOT NULL,
    status     VARCHAR(15) NOT NULL DEFAULT 'HELD' CHECK (status IN ('HELD','RELEASED','CONSUMED')),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE booking.booking_event (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id BIGINT      NOT NULL REFERENCES booking.booking(id) ON DELETE CASCADE, -- intra
    event_type VARCHAR(40) NOT NULL,
    actor_type VARCHAR(10) CHECK (actor_type IN ('MEMBER','STAFF','SYSTEM')),
    actor_id   BIGINT,
    note       TEXT,
    created_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_booking_event_booking ON booking.booking_event(booking_id);

SELECT public.apply_updated_at_triggers();
