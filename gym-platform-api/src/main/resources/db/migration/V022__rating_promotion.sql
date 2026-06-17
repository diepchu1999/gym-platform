-- P8 Rating + Promotion. Ref: data-model/p8-crm-rating-promotion-notification-audit.md

CREATE TABLE rating (
    id                         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    subject_type               VARCHAR(15) NOT NULL CHECK (subject_type IN ('PT','CLASS','BRANCH','SERVICE','EQUIPMENT','SUPPORT')),
    subject_id                 BIGINT      NOT NULL,
    author_member_id           BIGINT      NOT NULL REFERENCES member_profile(id),
    booking_id                 BIGINT      REFERENCES booking(id),
    rating                     SMALLINT    NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment                    TEXT,
    author_visible_to_subject  BOOLEAN     NOT NULL DEFAULT true,
    created_at                 timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_rating_subject ON rating(subject_type, subject_id);

CREATE TABLE coupon (
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

CREATE TABLE coupon_redemption (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    coupon_id  BIGINT      NOT NULL REFERENCES coupon(id),
    member_id  BIGINT      NOT NULL REFERENCES member_profile(id),
    order_id   BIGINT      NOT NULL REFERENCES customer_order(id),
    redeemed_at timestamptz NOT NULL DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (coupon_id, order_id)
);

CREATE TABLE campaign (
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

CREATE TABLE referral (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    referrer_member_id  BIGINT      NOT NULL REFERENCES member_profile(id),
    referred_member_id  BIGINT      NOT NULL UNIQUE REFERENCES member_profile(id),
    status              VARCHAR(15) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','REWARDED','VOID')),
    reward              NUMERIC(14,2) CHECK (reward IS NULL OR reward >= 0),
    created_at          timestamptz NOT NULL DEFAULT now()
);

-- Gắn FK coupon cho customer_order (đã tạo cột ở V010)
ALTER TABLE customer_order ADD CONSTRAINT fk_order_coupon FOREIGN KEY (coupon_id) REFERENCES coupon(id);

SELECT apply_updated_at_triggers();
