-- P1 Staff + Branch Assignment. Ref: data-model/p1-identity-org.md

CREATE TABLE staff_staff (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_account_id BIGINT      REFERENCES identity_user_account(id),
    employee_code   VARCHAR(30) NOT NULL UNIQUE,
    full_name       VARCHAR(150) NOT NULL,
    phone           VARCHAR(20),
    email           VARCHAR(255),
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE','TERMINATED')),
    created_at      timestamptz  NOT NULL DEFAULT now(),
    updated_at      timestamptz  NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_staff_user_account ON staff_staff(user_account_id) WHERE user_account_id IS NOT NULL;

CREATE TABLE staff_branch_assignment (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    staff_id    BIGINT      NOT NULL REFERENCES staff_staff(id) ON DELETE CASCADE,
    branch_id   BIGINT      REFERENCES branch_branch(id),   -- NULL = tất cả chi nhánh (role GLOBAL)
    role_id     BIGINT      NOT NULL REFERENCES rbac_role(id),
    active      BOOLEAN     NOT NULL DEFAULT true,
    assigned_at timestamptz NOT NULL DEFAULT now(),
    created_at  timestamptz NOT NULL DEFAULT now(),
    updated_at  timestamptz NOT NULL DEFAULT now()
);
-- COALESCE để NULL branch (global) vẫn bị chặn trùng
CREATE UNIQUE INDEX ux_staff_assignment ON staff_branch_assignment(staff_id, COALESCE(branch_id, 0), role_id);
CREATE INDEX ix_assignment_branch ON staff_branch_assignment(branch_id);
CREATE INDEX ix_assignment_role   ON staff_branch_assignment(role_id);

SELECT apply_updated_at_triggers();
