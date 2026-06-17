-- P6 Group Class (schema: groupclass). Ref: data-model/p6-booking-verticals.md (A)
-- Intra FK giữ: class_session->class_type, class_pass->class_type, class_booking->class_session/class_pass.
-- Logical refs: branch, room, instructor(staff), member, booking, order.

CREATE TABLE groupclass.class_type (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code        VARCHAR(40)  NOT NULL UNIQUE,
    name        VARCHAR(150) NOT NULL,
    description TEXT,
    created_at  timestamptz  NOT NULL DEFAULT now(),
    updated_at  timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE groupclass.class_session (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    class_type_id BIGINT      NOT NULL REFERENCES groupclass.class_type(id), -- intra
    branch_id     BIGINT      NOT NULL,              -- logical ref -> branch
    room_id       BIGINT      NOT NULL,              -- logical ref -> branch.branch_room
    instructor_id BIGINT      NOT NULL,              -- logical ref -> staff
    start_time    timestamptz NOT NULL,
    end_time      timestamptz NOT NULL,
    capacity      INT         NOT NULL CHECK (capacity > 0),
    booked_count  INT         NOT NULL DEFAULT 0 CHECK (booked_count >= 0 AND booked_count <= capacity),
    status        VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED'
        CHECK (status IN ('SCHEDULED','OPEN_FOR_BOOKING','FULL','ONGOING','COMPLETED','CANCELLED')),
    created_at    timestamptz NOT NULL DEFAULT now(),
    updated_at    timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_class_session_time CHECK (end_time > start_time)
);
-- BR-029: không trùng phòng / HLV (room_id, instructor_id là ID logic)
ALTER TABLE groupclass.class_session ADD CONSTRAINT ex_class_room
    EXCLUDE USING gist (room_id WITH =, tstzrange(start_time, end_time) WITH &&) WHERE (status <> 'CANCELLED');
ALTER TABLE groupclass.class_session ADD CONSTRAINT ex_class_instructor
    EXCLUDE USING gist (instructor_id WITH =, tstzrange(start_time, end_time) WITH &&) WHERE (status <> 'CANCELLED');
CREATE INDEX ix_class_session_branch_start ON groupclass.class_session(branch_id, start_time);

CREATE TABLE groupclass.class_pass (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code               VARCHAR(30)  NOT NULL UNIQUE,
    member_id          BIGINT       NOT NULL,        -- logical ref -> member
    class_type_scope   BIGINT       REFERENCES groupclass.class_type(id),   -- intra (NULL = mọi lớp)
    total_sessions     INT          NOT NULL CHECK (total_sessions > 0),
    remaining_sessions INT          NOT NULL CHECK (remaining_sessions >= 0),
    valid_from         timestamptz,
    valid_to           timestamptz,
    status             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','EXPIRED','USED_UP','CANCELLED')),
    source_order_id    BIGINT,                       -- logical ref -> payment.customer_order
    created_at         timestamptz  NOT NULL DEFAULT now(),
    updated_at         timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_class_pass_member ON groupclass.class_pass(member_id);

CREATE TABLE groupclass.class_booking (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id        BIGINT      NOT NULL UNIQUE,   -- logical ref -> booking.booking
    class_session_id  BIGINT      NOT NULL REFERENCES groupclass.class_session(id), -- intra
    member_id         BIGINT      NOT NULL,          -- logical ref -> member
    class_pass_id     BIGINT      REFERENCES groupclass.class_pass(id),    -- intra (NULL = trial)
    attendance_status VARCHAR(15) NOT NULL DEFAULT 'BOOKED' CHECK (attendance_status IN ('BOOKED','ATTENDED','NO_SHOW','CANCELLED')),
    created_at        timestamptz NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_class_booking_member_session ON groupclass.class_booking(class_session_id, member_id);

SELECT public.apply_updated_at_triggers();
