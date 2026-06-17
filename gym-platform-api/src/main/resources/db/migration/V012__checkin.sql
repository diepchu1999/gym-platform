-- P4 Check-in (schema: checkin). Ref: data-model/p4-checkin.md
-- Logical refs: member, branch, membership (KHÔNG FK chéo module).

CREATE TABLE checkin.checkin_token (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id  BIGINT      NOT NULL,                 -- logical ref -> member
    nonce      VARCHAR(64) NOT NULL UNIQUE,
    expires_at timestamptz NOT NULL,
    used_at    timestamptz,
    status     VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','USED','EXPIRED')),
    created_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_checkin_token_member ON checkin.checkin_token(member_id);

CREATE TABLE checkin.checkin_log (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id     BIGINT      NOT NULL,              -- logical ref -> member
    branch_id     BIGINT      NOT NULL,              -- logical ref -> branch
    membership_id BIGINT,                            -- logical ref -> membership
    checkin_type  VARCHAR(20) NOT NULL CHECK (checkin_type IN ('TRIAL','PAID')),
    checkin_time  timestamptz NOT NULL DEFAULT now(),
    checkin_date  DATE        NOT NULL,
    result        VARCHAR(10) NOT NULL CHECK (result IN ('ALLOWED','DENIED')),
    denied_reason VARCHAR(30) CHECK (denied_reason IN
        ('QR_EXPIRED','QR_ALREADY_USED','DUPLICATE_SCAN','MEMBER_BLOCKED','PACKAGE_EXPIRED','TRIAL_DAILY_LIMIT_REACHED','KYC_REQUIRED','BRANCH_UNAVAILABLE')),
    device_id     VARCHAR(60),
    created_at    timestamptz NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_trial_daily ON checkin.checkin_log(member_id, checkin_date)
    WHERE checkin_type = 'TRIAL' AND result = 'ALLOWED';
CREATE INDEX ix_checkin_member_date ON checkin.checkin_log(member_id, checkin_date);
CREATE INDEX ix_checkin_branch_time ON checkin.checkin_log(branch_id, checkin_time);
