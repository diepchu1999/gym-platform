-- P3 Order + Payment + Refund. Ref: data-model/p3-package-contract-payment.md
-- NOTE: 'order' là từ khoá SQL -> bảng đặt tên customer_order / customer_order_item.

CREATE TABLE customer_order (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_code   VARCHAR(30)  NOT NULL UNIQUE,
    member_id    BIGINT       REFERENCES member_profile(id),
    branch_id    BIGINT       NOT NULL REFERENCES branch_branch(id),
    order_type   VARCHAR(20)  NOT NULL
        CHECK (order_type IN ('PACKAGE','POS_PRODUCT','PANTRY','CLASS_PASS','PT_SESSION','MASSAGE','PRIVATE_ROOM','BOOKING_EXTRA')),
    contract_id  BIGINT       REFERENCES contract(id),
    coupon_id    BIGINT,      -- FK thêm ở V022 (sau khi tạo bảng coupon)
    status       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT','PENDING_PAYMENT','PAID','CANCELLED','REFUNDED','PARTIALLY_REFUNDED')),
    total_amount NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (total_amount >= 0),
    currency     VARCHAR(3)   NOT NULL DEFAULT 'VND',
    created_at   timestamptz  NOT NULL DEFAULT now(),
    updated_at   timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_order_member ON customer_order(member_id);
CREATE INDEX ix_order_branch ON customer_order(branch_id);

CREATE TABLE customer_order_item (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id    BIGINT       NOT NULL REFERENCES customer_order(id) ON DELETE CASCADE,
    item_type   VARCHAR(20)  NOT NULL CHECK (item_type IN ('PACKAGE','PRODUCT','PANTRY','SESSION')),
    ref_id      BIGINT,
    description VARCHAR(255),
    quantity    INT          NOT NULL DEFAULT 1 CHECK (quantity > 0),
    unit_price  NUMERIC(14,2) NOT NULL CHECK (unit_price >= 0),
    line_amount NUMERIC(14,2) NOT NULL CHECK (line_amount >= 0),
    created_at  timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_order_item_order ON customer_order_item(order_id);

CREATE TABLE payment (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    payment_code            VARCHAR(30)  NOT NULL UNIQUE,
    order_id                BIGINT       NOT NULL REFERENCES customer_order(id),
    payment_method          VARCHAR(20)  NOT NULL CHECK (payment_method IN ('ONLINE','COUNTER_CASH','COUNTER_CARD','INSTALLMENT')),
    payment_status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING_PAYMENT'
        CHECK (payment_status IN ('UNPAID','PENDING_PAYMENT','PAID','FAILED','EXPIRED','REFUNDED','PARTIALLY_REFUNDED')),
    provider                VARCHAR(40),
    provider_transaction_id VARCHAR(100),
    idempotency_key         VARCHAR(100),
    amount                  NUMERIC(14,2) NOT NULL CHECK (amount >= 0),
    currency                VARCHAR(3)   NOT NULL DEFAULT 'VND',
    paid_at                 timestamptz,
    created_at              timestamptz  NOT NULL DEFAULT now(),
    updated_at              timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_payment_order ON payment(order_id);
-- Idempotency cho payment callback
CREATE UNIQUE INDEX ux_payment_provider_txn ON payment(provider, provider_transaction_id) WHERE provider_transaction_id IS NOT NULL;
CREATE UNIQUE INDEX ux_payment_idem         ON payment(idempotency_key) WHERE idempotency_key IS NOT NULL;

CREATE TABLE refund (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    refund_code VARCHAR(30)  NOT NULL UNIQUE,
    payment_id  BIGINT       NOT NULL REFERENCES payment(id),
    amount      NUMERIC(14,2) NOT NULL CHECK (amount >= 0),
    reason      TEXT,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','COMPLETED','FAILED')),
    refunded_at timestamptz,
    created_at  timestamptz  NOT NULL DEFAULT now(),
    updated_at  timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_refund_payment ON refund(payment_id);

SELECT apply_updated_at_triggers();
