-- P6 Group Class. Ref: data-model/p6-booking-verticals.md (A)

CREATE TABLE class_type (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code        VARCHAR(40)  NOT NULL UNIQUE,
    name        VARCHAR(150) NOT NULL,
    description TEXT,
    created_at  timestamptz  NOT NULL DEFAULT now(),
    updated_at  timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE class_session (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    class_type_id BIGINT      NOT NULL REFERENCES class_type(id),
    branch_id     BIGINT      NOT NULL REFERENCES branch_branch(id),
    room_id       BIGINT      NOT NULL REFERENCES branch_room(id),
    instructor_id BIGINT      NOT NULL REFERENCES staff_staff(id),
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
-- BR-029: không trùng phòng / HLV (trừ lớp đã huỷ)
ALTER TABLE class_session ADD CONSTRAINT ex_class_room
    EXCLUDE USING gist (room_id WITH =, tstzrange(start_time, end_time) WITH &&) WHERE (status <> 'CANCELLED');
ALTER TABLE class_session ADD CONSTRAINT ex_class_instructor
    EXCLUDE USING gist (instructor_id WITH =, tstzrange(start_time, end_time) WITH &&) WHERE (status <> 'CANCELLED');
CREATE INDEX ix_class_session_branch_start ON class_session(branch_id, start_time);

CREATE TABLE class_pass (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code               VARCHAR(30)  NOT NULL UNIQUE,
    member_id          BIGINT       NOT NULL REFERENCES member_profile(id),
    class_type_scope   BIGINT       REFERENCES class_type(id),   -- NULL = mọi lớp
    total_sessions     INT          NOT NULL CHECK (total_sessions > 0),
    remaining_sessions INT          NOT NULL CHECK (remaining_sessions >= 0),
    valid_from         timestamptz,
    valid_to           timestamptz,
    status             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','EXPIRED','USED_UP','CANCELLED')),
    source_order_id    BIGINT       REFERENCES customer_order(id),
    created_at         timestamptz  NOT NULL DEFAULT now(),
    updated_at         timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_class_pass_member ON class_pass(member_id);

CREATE TABLE class_booking (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id        BIGINT      NOT NULL UNIQUE REFERENCES booking(id) ON DELETE CASCADE,
    class_session_id  BIGINT      NOT NULL REFERENCES class_session(id),
    member_id         BIGINT      NOT NULL REFERENCES member_profile(id),
    class_pass_id     BIGINT      REFERENCES class_pass(id),    -- NULL = quyền trial
    attendance_status VARCHAR(15) NOT NULL DEFAULT 'BOOKED' CHECK (attendance_status IN ('BOOKED','ATTENDED','NO_SHOW','CANCELLED')),
    created_at        timestamptz NOT NULL DEFAULT now()
);
-- 1 member chỉ đặt 1 chỗ / session
CREATE UNIQUE INDEX ux_class_booking_member_session ON class_booking(class_session_id, member_id);

SELECT apply_updated_at_triggers();
