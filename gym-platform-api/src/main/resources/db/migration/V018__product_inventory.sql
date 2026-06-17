-- P7 Product / Inventory (schema: inventory). Ref: data-model/p7-inventory-pantry-equipment.md
-- Intra FK giữ: product->partner, stock/movement/batch->product. Logical refs: branch, staff.

CREATE TABLE inventory.product_partner (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code       VARCHAR(40)  NOT NULL UNIQUE,
    name       VARCHAR(150) NOT NULL,
    type       VARCHAR(20)  NOT NULL CHECK (type IN ('PARTNER','BRAND')),
    contact    VARCHAR(150),
    active     BOOLEAN      NOT NULL DEFAULT true,
    created_at timestamptz  NOT NULL DEFAULT now(),
    updated_at timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE inventory.product (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sku          VARCHAR(40)  NOT NULL UNIQUE,
    name         VARCHAR(150) NOT NULL,
    category     VARCHAR(60),
    product_type VARCHAR(20)  NOT NULL CHECK (product_type IN ('GYM_SUPPORT','SUPPLEMENT','PANTRY')),
    partner_id   BIGINT       REFERENCES inventory.product_partner(id), -- intra
    price        NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (price >= 0),
    currency     VARCHAR(3)   NOT NULL DEFAULT 'VND',
    is_pantry    BOOLEAN      NOT NULL DEFAULT false,
    track_batch  BOOLEAN      NOT NULL DEFAULT false,
    active       BOOLEAN      NOT NULL DEFAULT true,
    created_at   timestamptz  NOT NULL DEFAULT now(),
    updated_at   timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE inventory.inventory_stock (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id          BIGINT NOT NULL REFERENCES inventory.product(id), -- intra
    branch_id           BIGINT NOT NULL,             -- logical ref -> branch
    quantity            INT    NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    low_stock_threshold INT    NOT NULL DEFAULT 0,
    version             BIGINT NOT NULL DEFAULT 0,
    created_at          timestamptz NOT NULL DEFAULT now(),
    updated_at          timestamptz NOT NULL DEFAULT now(),
    UNIQUE (product_id, branch_id)
);

CREATE TABLE inventory.stock_movement (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id     BIGINT      NOT NULL REFERENCES inventory.product(id), -- intra
    branch_id      BIGINT      NOT NULL,             -- logical ref -> branch
    movement_type  VARCHAR(20) NOT NULL CHECK (movement_type IN ('IMPORT','SALE','TRANSFER_IN','TRANSFER_OUT','ADJUSTMENT','RETURN')),
    quantity       INT         NOT NULL,
    reference_type VARCHAR(40),
    reference_id   BIGINT,
    created_by     BIGINT,                           -- logical ref -> staff
    created_at     timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_stock_movement_product ON inventory.stock_movement(product_id, branch_id);

CREATE TABLE inventory.product_batch (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id  BIGINT      NOT NULL REFERENCES inventory.product(id), -- intra
    branch_id   BIGINT      NOT NULL,                -- logical ref -> branch
    batch_no    VARCHAR(60) NOT NULL,
    expiry_date DATE,
    quantity    INT         NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    created_at  timestamptz NOT NULL DEFAULT now(),
    updated_at  timestamptz NOT NULL DEFAULT now(),
    UNIQUE (product_id, branch_id, batch_no)
);

SELECT public.apply_updated_at_triggers();
