-- P8 Rating (schema: rating) + Promotion (schema: promotion).
-- Intra FK giữ: coupon_redemption -> coupon. Logical refs: member, booking, order.
-- customer_order.coupon_id giờ là ID logic (KHÔNG còn ALTER ADD FK chéo module).

CREATE TABLE rating.rating (
    id                         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    subject_type               VARCHAR(15) NOT NULL CHECK (subject_type IN ('PT','CLASS','BRANCH','SERVICE','EQUIPMENT','SUPPORT')),
    subject_id                 BIGINT      NOT NULL,
    author_member_id           BIGINT      NOT NULL,    -- logical ref -> member
    booking_id                 BIGINT,                  -- logical ref -> booking
    rating                     SMALLINT    NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment                    TEXT,
    author_visible_to_subject  BOOLEAN     NOT NULL DEFAULT true,
    created_at                 timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_rating_subject ON rating.rating(subject_type, subject_id);

CREATE TABLE promotion.coupon (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code            VARCHAR(40)  NOT NULL UNIQUE,
    name            VARCHAR(150),
    discount_type   VARCHAR(10)  NOT NULL CHECK (discount_type IN ('PERCENT','FIXED')),
    discount_value  NUMERIC(14,2) NOT NULL CHECK (discount_value >= 0),
    applies_to      VARCHAR(20)  NOT NULL DEFAULT 'ALL' CHECK (applies_to IN ('PACKAGE','POS','ALL')),
    is_student_discount BOOLEAN  NOT NULL DEFAULT false,
    valid_from      timestamptz,
    valid_to        timestamptz,
    usage_limit     INT,
    used_count      INT          NOT NULL DEFAULT 0 CHECK (used_count >= 0),
    per_member_limit INT         NOT NULL DEFAULT 1,
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','PAUSED','EXPIRED')),
    created_at      timestamptz  NOT NULL DEFAULT now(),
    updated_at      timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE promotion.coupon_redemption (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    coupon_id  BIGINT      NOT NULL REFERENCES promotion.coupon(id), -- intra
    member_id  BIGINT      NOT NULL,                 -- logical ref -> member
    order_id   BIGINT      NOT NULL,                 -- logical ref -> payment.customer_order
    redeemed_at timestamptz NOT NULL DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (coupon_id, order_id)
);

CREATE TABLE promotion.campaign (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code       VARCHAR(40) NOT NULL UNIQUE,
    name       VARCHAR(150),
    type       VARCHAR(40),
    start_at   timestamptz,
    end_at     timestamptz,
    status     VARCHAR(15) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','RUNNING','ENDED')),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE promotion.referral (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    referrer_member_id  BIGINT      NOT NULL,        -- logical ref -> member
    referred_member_id  BIGINT      NOT NULL UNIQUE, -- logical ref -> member
    status              VARCHAR(15) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','REWARDED','VOID')),
    reward              NUMERIC(14,2) CHECK (reward IS NULL OR reward >= 0),
    created_at          timestamptz NOT NULL DEFAULT now()
);

SELECT public.apply_updated_at_triggers();
