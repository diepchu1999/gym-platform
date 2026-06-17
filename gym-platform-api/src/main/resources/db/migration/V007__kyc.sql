-- P2 KYC + Student verification + Trial usage. Ref: data-model/p2-member-kyc.md

CREATE TABLE kyc_request (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id               BIGINT      NOT NULL REFERENCES member_profile(id),
    identity_type           VARCHAR(20) NOT NULL DEFAULT 'CCCD' CHECK (identity_type IN ('CCCD')),
    identity_number_masked  VARCHAR(20) NOT NULL,
    identity_number_hash    VARCHAR(64) NOT NULL,
    front_image_url         VARCHAR(255),
    back_image_url          VARCHAR(255),
    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('NOT_SUBMITTED','PENDING','APPROVED','REJECTED','REQUEST_RESUBMIT','EXPIRED')),
    submitted_at            timestamptz,
    reviewed_by             BIGINT      REFERENCES staff_staff(id),
    reviewed_at             timestamptz,
    rejection_reason        TEXT,
    created_at              timestamptz NOT NULL DEFAULT now(),
    updated_at              timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_kyc_member ON kyc_request(member_id);
-- 1 CCCD chỉ APPROVED cho 1 người
CREATE UNIQUE INDEX ux_kyc_cccd_approved ON kyc_request(identity_number_hash) WHERE status = 'APPROVED';

CREATE TABLE student_verification (
    id                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id              BIGINT      NOT NULL REFERENCES member_profile(id),
    school_name            VARCHAR(150) NOT NULL,
    student_card_image_url VARCHAR(255),
    status                 VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                           CHECK (status IN ('PENDING','APPROVED','REJECTED','EXPIRED')),
    expired_at             timestamptz,
    reviewed_by            BIGINT      REFERENCES staff_staff(id),
    reviewed_at            timestamptz,
    created_at             timestamptz NOT NULL DEFAULT now(),
    updated_at             timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_student_member ON student_verification(member_id);

CREATE TABLE trial_usage (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id            BIGINT      NOT NULL REFERENCES member_profile(id),
    identity_number_hash VARCHAR(64) NOT NULL UNIQUE,   -- BR-007: 1 CCCD trial 1 lần
    trial_started_at     timestamptz,
    trial_ended_at       timestamptz,
    status               VARCHAR(20) NOT NULL DEFAULT 'KYC_PENDING'
                         CHECK (status IN ('KYC_PENDING','ACTIVE','EXPIRED','CONVERTED','CANCELLED')),
    created_at           timestamptz NOT NULL DEFAULT now(),
    updated_at           timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_trial_member ON trial_usage(member_id);

SELECT apply_updated_at_triggers();
