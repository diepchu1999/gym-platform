-- P3 Installment application (FE Credit / Home Credit). Ref: data-model/p3-package-contract-payment.md

CREATE TABLE installment_application (
    id                        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_code          VARCHAR(30)  NOT NULL UNIQUE,
    order_id                  BIGINT       NOT NULL REFERENCES customer_order(id),
    provider                  VARCHAR(40)  NOT NULL,
    status                    VARCHAR(30)  NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT','SUBMITTED','PENDING_PROVIDER_APPROVAL','APPROVED','REJECTED','CANCELLED','DISBURSED')),
    provider_application_code VARCHAR(100),
    amount                    NUMERIC(14,2) NOT NULL CHECK (amount >= 0),
    submitted_at              timestamptz,
    approved_at               timestamptz,
    rejected_reason           TEXT,
    created_at                timestamptz  NOT NULL DEFAULT now(),
    updated_at                timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX ix_installment_order ON installment_application(order_id);

SELECT apply_updated_at_triggers();
