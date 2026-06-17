-- P3 Contract + Membership. Ref: data-model/p3-package-contract-payment.md

CREATE TABLE contract (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_code   VARCHAR(30)  NOT NULL UNIQUE,
    member_id       BIGINT       NOT NULL REFERENCES member_profile(id),
    package_plan_id BIGINT       NOT NULL REFERENCES package_plan(id),
    sale_branch_id  BIGINT       NOT NULL REFERENCES branch_branch(id),
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT','PENDING_SIGNATURE','ACTIVE','EXPIRED','TERMINATED','CANCELLED','SUSPENDED')),
    signed_at       timestamptz,
    effective_from  timestamptz,
    effective_to    timestamptz,
    total_amount    NUMERIC(14,2) NOT NULL CHECK (total_amount >= 0),
    currency        VARCHAR(3)   NOT NULL DEFAULT 'VND',
    document_url    VARCHAR(255),
    created_at      timestamptz  NOT NULL DEFAULT now(),
    updated_at      timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_contract_member ON contract(member_id);

CREATE TABLE membership (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code            VARCHAR(30)  NOT NULL UNIQUE,
    member_id       BIGINT       NOT NULL REFERENCES member_profile(id),
    package_plan_id BIGINT       NOT NULL REFERENCES package_plan(id),
    contract_id     BIGINT       REFERENCES contract(id),
    sale_branch_id  BIGINT       NOT NULL REFERENCES branch_branch(id),
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING_PAYMENT'
        CHECK (status IN ('PENDING_PAYMENT','ACTIVE','EXPIRED','SUSPENDED','CANCELLED')),
    effective_from  timestamptz,
    effective_to    timestamptz,
    created_at      timestamptz  NOT NULL DEFAULT now(),
    updated_at      timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_membership_member_status ON membership(member_id, status);
CREATE INDEX ix_membership_effective_to  ON membership(effective_to);

SELECT apply_updated_at_triggers();
