-- P1 Identity + RBAC. Ref: data-model/p1-identity-org.md
-- identity_user_account: ánh xạ principal nội bộ <-> Keycloak (ADR-0006), KHÔNG lưu mật khẩu.

CREATE TABLE identity_user_account (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    keycloak_user_id UUID        NOT NULL,
    account_type     VARCHAR(20) NOT NULL CHECK (account_type IN ('STAFF','MEMBER')),
    username         VARCHAR(100),
    email            VARCHAR(255),
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','DISABLED','LOCKED')),
    last_login_at    timestamptz,
    created_at       timestamptz NOT NULL DEFAULT now(),
    updated_at       timestamptz NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_user_account_kc       ON identity_user_account(keycloak_user_id);
CREATE UNIQUE INDEX ux_user_account_username ON identity_user_account(username) WHERE username IS NOT NULL;
CREATE UNIQUE INDEX ux_user_account_email    ON identity_user_account(email)    WHERE email IS NOT NULL;

CREATE TABLE rbac_role (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    scope       VARCHAR(20)  NOT NULL DEFAULT 'BRANCH' CHECK (scope IN ('GLOBAL','BRANCH')),
    is_system   BOOLEAN      NOT NULL DEFAULT false,
    created_at  timestamptz  NOT NULL DEFAULT now(),
    updated_at  timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE rbac_permission (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code        VARCHAR(80) NOT NULL UNIQUE,
    module      VARCHAR(50),
    description TEXT,
    created_at  timestamptz NOT NULL DEFAULT now(),
    updated_at  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE rbac_role_permission (
    role_id       BIGINT NOT NULL REFERENCES rbac_role(id)       ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES rbac_permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);
CREATE INDEX ix_role_permission_perm ON rbac_role_permission(permission_id);

SELECT apply_updated_at_triggers();
