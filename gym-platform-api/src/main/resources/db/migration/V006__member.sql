-- P2 Member. Ref: data-model/p2-member-kyc.md

CREATE TABLE member_profile (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code            VARCHAR(30)  NOT NULL UNIQUE,
    user_account_id BIGINT       REFERENCES identity_user_account(id),
    full_name       VARCHAR(150) NOT NULL,
    phone           VARCHAR(20),
    email           VARCHAR(255),
    gender          VARCHAR(10)  CHECK (gender IN ('MALE','FEMALE','OTHER')),
    date_of_birth   DATE,
    home_branch_id  BIGINT       NOT NULL REFERENCES branch_branch(id),
    is_student      BOOLEAN      NOT NULL DEFAULT false,
    status          VARCHAR(20)  NOT NULL DEFAULT 'REGISTERED'
                    CHECK (status IN ('LEAD','REGISTERED','KYC_PENDING','ACTIVE','INACTIVE','SUSPENDED','BLACKLISTED')),
    created_at      timestamptz  NOT NULL DEFAULT now(),
    updated_at      timestamptz  NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_member_account ON member_profile(user_account_id) WHERE user_account_id IS NOT NULL;
CREATE UNIQUE INDEX ux_member_phone   ON member_profile(phone) WHERE phone IS NOT NULL;
CREATE UNIQUE INDEX ux_member_email   ON member_profile(email) WHERE email IS NOT NULL;
CREATE INDEX ix_member_home_branch ON member_profile(home_branch_id);
CREATE INDEX ix_member_status       ON member_profile(status);

SELECT apply_updated_at_triggers();
