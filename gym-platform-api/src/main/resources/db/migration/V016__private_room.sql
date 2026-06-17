-- P6 Private Room (schema: privateroom). Ref: data-model/p6-booking-verticals.md (C)
-- Intra FK giữ: private_room_booking -> private_room. Logical refs: branch, room, member, booking.

CREATE TABLE privateroom.private_room (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code         VARCHAR(30)  NOT NULL UNIQUE,
    branch_id    BIGINT       NOT NULL,              -- logical ref -> branch
    room_id      BIGINT,                             -- logical ref -> branch.branch_room
    name         VARCHAR(100),
    capacity     INT          CHECK (capacity IS NULL OR capacity >= 0),
    hourly_price NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (hourly_price >= 0),
    status       VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE'
        CHECK (status IN ('AVAILABLE','BOOKED','IN_USE','CLEANING','MAINTENANCE','CLOSED')),
    created_at   timestamptz  NOT NULL DEFAULT now(),
    updated_at   timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_private_room_branch ON privateroom.private_room(branch_id);

CREATE TABLE privateroom.private_room_quota (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id     BIGINT NOT NULL,                   -- logical ref -> member
    year_month    DATE   NOT NULL,
    total_minutes INT    NOT NULL CHECK (total_minutes >= 0),
    used_minutes  INT    NOT NULL DEFAULT 0 CHECK (used_minutes >= 0 AND used_minutes <= total_minutes),
    created_at    timestamptz NOT NULL DEFAULT now(),
    updated_at    timestamptz NOT NULL DEFAULT now(),
    UNIQUE (member_id, year_month)
);

CREATE TABLE privateroom.private_room_booking (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id       BIGINT       NOT NULL UNIQUE,   -- logical ref -> booking.booking
    private_room_id  BIGINT       NOT NULL REFERENCES privateroom.private_room(id), -- intra
    duration_minutes INT          NOT NULL CHECK (duration_minutes > 0 AND duration_minutes <= 120), -- BR-014
    quota_used_minutes INT        NOT NULL DEFAULT 0 CHECK (quota_used_minutes >= 0),
    paid_extra_amount  NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (paid_extra_amount >= 0),
    created_at       timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_private_room_booking_room ON privateroom.private_room_booking(private_room_id);

SELECT public.apply_updated_at_triggers();
