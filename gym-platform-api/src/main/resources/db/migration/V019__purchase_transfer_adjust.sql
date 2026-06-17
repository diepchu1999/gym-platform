-- P7 Purchase / Transfer / Adjust (schema: inventory). Ref: data-model/p7-inventory-pantry-equipment.md
-- Intra FK giữ: po_item->po, po/po_item/transfer/adjust->product/partner. Logical refs: branch, staff.

CREATE TABLE inventory.purchase_order (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    po_code      VARCHAR(30)  NOT NULL UNIQUE,
    partner_id   BIGINT       NOT NULL REFERENCES inventory.product_partner(id), -- intra
    branch_id    BIGINT       NOT NULL,              -- logical ref -> branch
    status       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','ORDERED','RECEIVED','CANCELLED')),
    total_amount NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (total_amount >= 0),
    created_at   timestamptz  NOT NULL DEFAULT now(),
    updated_at   timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE inventory.purchase_order_item (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    purchase_order_id BIGINT       NOT NULL REFERENCES inventory.purchase_order(id) ON DELETE CASCADE, -- intra
    product_id        BIGINT       NOT NULL REFERENCES inventory.product(id),                          -- intra
    quantity          INT          NOT NULL CHECK (quantity > 0),
    unit_cost         NUMERIC(14,2) NOT NULL CHECK (unit_cost >= 0),
    line_amount       NUMERIC(14,2) NOT NULL CHECK (line_amount >= 0),
    created_at        timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_po_item_po ON inventory.purchase_order_item(purchase_order_id);

CREATE TABLE inventory.stock_transfer (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code          VARCHAR(30) NOT NULL UNIQUE,
    product_id    BIGINT      NOT NULL REFERENCES inventory.product(id), -- intra
    from_branch_id BIGINT     NOT NULL,              -- logical ref -> branch
    to_branch_id  BIGINT      NOT NULL,              -- logical ref -> branch
    quantity      INT         NOT NULL CHECK (quantity > 0),
    status        VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','IN_TRANSIT','COMPLETED','CANCELLED')),
    created_at    timestamptz NOT NULL DEFAULT now(),
    updated_at    timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_transfer_branch CHECK (from_branch_id <> to_branch_id)
);

CREATE TABLE inventory.stock_adjustment (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code           VARCHAR(30) NOT NULL UNIQUE,
    product_id     BIGINT      NOT NULL REFERENCES inventory.product(id), -- intra
    branch_id      BIGINT      NOT NULL,             -- logical ref -> branch
    quantity_delta INT         NOT NULL,
    reason         TEXT,
    created_by     BIGINT,                           -- logical ref -> staff
    created_at     timestamptz NOT NULL DEFAULT now()
);

SELECT public.apply_updated_at_triggers();
