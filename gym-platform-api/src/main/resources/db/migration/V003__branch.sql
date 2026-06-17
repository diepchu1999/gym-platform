-- P1 Branch + Room. Ref: data-model/p1-identity-org.md

CREATE TABLE branch_branch (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code       VARCHAR(30)  NOT NULL UNIQUE,
    name       VARCHAR(150) NOT NULL,
    address    VARCHAR(255),
    district   VARCHAR(100),
    city       VARCHAR(100) NOT NULL DEFAULT 'Ho Chi Minh City',
    phone      VARCHAR(20),
    open_24h   BOOLEAN      NOT NULL DEFAULT true,
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE','CLOSED')),
    created_at timestamptz  NOT NULL DEFAULT now(),
    updated_at timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE branch_room (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    branch_id  BIGINT      NOT NULL REFERENCES branch_branch(id),
    code       VARCHAR(30) NOT NULL,
    name       VARCHAR(100),
    room_type  VARCHAR(30) NOT NULL CHECK (room_type IN ('GENERAL','CLASS_ROOM','PT_AREA','PRIVATE_ROOM','MASSAGE_ROOM')),
    capacity   INT         CHECK (capacity IS NULL OR capacity >= 0),
    status     VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','CLEANING','MAINTENANCE','CLOSED')),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (branch_id, code)
);
CREATE INDEX ix_branch_room_branch ON branch_room(branch_id);

SELECT apply_updated_at_triggers();
