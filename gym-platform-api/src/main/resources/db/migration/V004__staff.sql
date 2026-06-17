-- P1 Staff (schema: staff). Ref: data-model/p1-identity-org.md
-- Logical refs (KHÔNG FK chéo module): user_account_id -> identity, branch_id -> branch, role_id -> identity.

CREATE TABLE staff.staff_staff (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_account_id BIGINT,                          -- logical ref -> identity.identity_user_account
    employee_code   VARCHAR(30) NOT NULL UNIQUE,
    full_name       VARCHAR(150) NOT NULL,
    phone           VARCHAR(20),
    email           VARCHAR(255),
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE','TERMINATED')),
    created_at      timestamptz  NOT NULL DEFAULT now(),
    updated_at      timestamptz  NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_staff_user_account ON staff.staff_staff(user_account_id) WHERE user_account_id IS NOT NULL;

CREATE TABLE staff.staff_branch_assignment (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    staff_id    BIGINT      NOT NULL REFERENCES staff.staff_staff(id) ON DELETE CASCADE, -- intra: giữ FK
    branch_id   BIGINT,                              -- logical ref -> branch (NULL = mọi chi nhánh)
    role_id     BIGINT      NOT NULL,                -- logical ref -> identity.rbac_role
    active      BOOLEAN     NOT NULL DEFAULT true,
    assigned_at timestamptz NOT NULL DEFAULT now(),
    created_at  timestamptz NOT NULL DEFAULT now(),
    updated_at  timestamptz NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_staff_assignment ON staff.staff_branch_assignment(staff_id, COALESCE(branch_id, 0), role_id);
CREATE INDEX ix_assignment_branch ON staff.staff_branch_assignment(branch_id);
CREATE INDEX ix_assignment_role   ON staff.staff_branch_assignment(role_id);

SELECT public.apply_updated_at_triggers();
