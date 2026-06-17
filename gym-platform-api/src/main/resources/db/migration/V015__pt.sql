-- P6 PT (schema: pt). Ref: data-model/p6-booking-verticals.md (B)
-- Intra FK giữ: availability/booking/rating -> trainer_profile. Logical refs: staff, branch, booking, member.

CREATE TABLE pt.trainer_profile (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    staff_id          BIGINT       NOT NULL UNIQUE,  -- logical ref -> staff (unique 1-1)
    branch_id         BIGINT       NOT NULL,         -- logical ref -> branch
    level             VARCHAR(40),
    specialties       TEXT,
    price_per_session NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (price_per_session >= 0),
    currency          VARCHAR(3)   NOT NULL DEFAULT 'VND',
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE')),
    created_at        timestamptz  NOT NULL DEFAULT now(),
    updated_at        timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE pt.trainer_availability (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    trainer_id  BIGINT   NOT NULL REFERENCES pt.trainer_profile(id) ON DELETE CASCADE, -- intra
    branch_id   BIGINT   NOT NULL,                   -- logical ref -> branch
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    start_time  TIME     NOT NULL,
    end_time    TIME     NOT NULL,
    CONSTRAINT ck_pt_hours CHECK (start_time >= TIME '06:00' AND end_time <= TIME '22:00' AND end_time > start_time)
);
CREATE INDEX ix_trainer_avail_trainer ON pt.trainer_availability(trainer_id);

CREATE TABLE pt.pt_booking (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id              BIGINT       NOT NULL UNIQUE,  -- logical ref -> booking.booking
    trainer_id              BIGINT       NOT NULL REFERENCES pt.trainer_profile(id), -- intra
    duration_minutes        INT          NOT NULL DEFAULT 90 CHECK (duration_minutes > 0),
    price                   NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (price >= 0),
    currency                VARCHAR(3)   NOT NULL DEFAULT 'VND',
    completed_by_trainer_at timestamptz,
    created_at              timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_pt_booking_trainer ON pt.pt_booking(trainer_id);

CREATE TABLE pt.pt_rating (
    id                        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id                BIGINT      NOT NULL UNIQUE,  -- logical ref -> booking.booking
    member_id                 BIGINT      NOT NULL,         -- logical ref -> member
    trainer_id                BIGINT      NOT NULL REFERENCES pt.trainer_profile(id), -- intra
    rating                    SMALLINT    NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment                   TEXT,
    author_visible_to_trainer BOOLEAN     NOT NULL DEFAULT false,   -- BR-035
    created_at                timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_pt_rating_trainer ON pt.pt_rating(trainer_id);

SELECT public.apply_updated_at_triggers();
