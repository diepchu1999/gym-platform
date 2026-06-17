-- P6 Private Room (<=2h, quota tháng VIP). Ref: data-model/p6-booking-verticals.md (C)

CREATE TABLE private_room (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code         VARCHAR(30)  NOT NULL UNIQUE,
    branch_id    BIGINT       NOT NULL REFERENCES branch_branch(id),
    room_id      BIGINT       REFERENCES branch_room(id),
    name         VARCHAR(100),
    capacity     INT          CHECK (capacity IS NULL OR capacity >= 0),
    hourly_price NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (hourly_price >= 0),
    status       VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE'
        CHECK (status IN ('AVAILABLE','BOOKED','IN_USE','CLEANING','MAINTENANCE','CLOSED')),
    created_at   timestamptz  NOT NULL DEFAULT now(),
    updated_at   timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_private_room_branch ON private_room(branch_id);

CREATE TABLE private_room_quota (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id     BIGINT NOT NULL REFERENCES member_profile(id),
    year_month    DATE   NOT NULL,                 -- ngày 01 của tháng
    total_minutes INT    NOT NULL CHECK (total_minutes >= 0),
    used_minutes  INT    NOT NULL DEFAULT 0 CHECK (used_minutes >= 0 AND used_minutes <= total_minutes),
    created_at    timestamptz NOT NULL DEFAULT now(),
    updated_at    timestamptz NOT NULL DEFAULT now(),
    UNIQUE (member_id, year_month)
);

CREATE TABLE private_room_booking (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id       BIGINT       NOT NULL UNIQUE REFERENCES booking(id) ON DELETE CASCADE,
    private_room_id  BIGINT       NOT NULL REFERENCES private_room(id),
    duration_minutes INT          NOT NULL CHECK (duration_minutes > 0 AND duration_minutes <= 120), -- BR-014: <=2h
    quota_used_minutes INT        NOT NULL DEFAULT 0 CHECK (quota_used_minutes >= 0),
    paid_extra_amount  NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (paid_extra_amount >= 0),
    created_at       timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_private_room_booking_room ON private_room_booking(private_room_id);

SELECT apply_updated_at_triggers();
