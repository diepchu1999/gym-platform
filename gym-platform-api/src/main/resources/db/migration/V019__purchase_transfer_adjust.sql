-- P7 Purchase order / Stock transfer / Adjustment. Ref: data-model/p7-inventory-pantry-equipment.md

CREATE TABLE purchase_order (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    po_code      VARCHAR(30)  NOT NULL UNIQUE,
    partner_id   BIGINT       NOT NULL REFERENCES product_partner(id),
    branch_id    BIGINT       NOT NULL REFERENCES branch_branch(id),
    status       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','ORDERED','RECEIVED','CANCELLED')),
    total_amount NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (total_amount >= 0),
    created_at   timestamptz  NOT NULL DEFAULT now(),
    updated_at   timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE purchase_order_item (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    purchase_order_id BIGINT       NOT NULL REFERENCES purchase_order(id) ON DELETE CASCADE,
    product_id        BIGINT       NOT NULL REFERENCES product(id),
    quantity          INT          NOT NULL CHECK (quantity > 0),
    unit_cost         NUMERIC(14,2) NOT NULL CHECK (unit_cost >= 0),
    line_amount       NUMERIC(14,2) NOT NULL CHECK (line_amount >= 0),
    created_at        timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_po_item_po ON purchase_order_item(purchase_order_id);

CREATE TABLE stock_transfer (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code          VARCHAR(30) NOT NULL UNIQUE,
    product_id    BIGINT      NOT NULL REFERENCES product(id),
    from_branch_id BIGINT     NOT NULL REFERENCES branch_branch(id),
    to_branch_id  BIGINT      NOT NULL REFERENCES branch_branch(id),
    quantity      INT         NOT NULL CHECK (quantity > 0),
    status        VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','IN_TRANSIT','COMPLETED','CANCELLED')),
    created_at    timestamptz NOT NULL DEFAULT now(),
    updated_at    timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_transfer_branch CHECK (from_branch_id <> to_branch_id)
);

CREATE TABLE stock_adjustment (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code           VARCHAR(30) NOT NULL UNIQUE,
    product_id     BIGINT      NOT NULL REFERENCES product(id),
    branch_id      BIGINT      NOT NULL REFERENCES branch_branch(id),
    quantity_delta INT         NOT NULL,
    reason         TEXT,
    created_by     BIGINT      REFERENCES staff_staff(id),
    created_at     timestamptz NOT NULL DEFAULT now()
);

SELECT apply_updated_at_triggers();
