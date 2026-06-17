-- P3 Package catalog. Ref: data-model/p3-package-contract-payment.md

CREATE TABLE package_plan (
    id                              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code                            VARCHAR(40)  NOT NULL UNIQUE,
    name                            VARCHAR(150) NOT NULL,
    package_type                    VARCHAR(30)  NOT NULL
        CHECK (package_type IN ('TRIAL','MONTHLY','QUARTERLY','YEARLY','VIP','STUDENT','CLASS_PASS','PT_SESSION','MASSAGE_ADDON','PRIVATE_ROOM_EXTRA')),
    duration_days                   INT          CHECK (duration_days IS NULL OR duration_days >= 0),
    price                           NUMERIC(14,2) NOT NULL CHECK (price >= 0),
    currency                        VARCHAR(3)   NOT NULL DEFAULT 'VND',
    is_vip                          BOOLEAN      NOT NULL DEFAULT false,
    is_student_only                 BOOLEAN      NOT NULL DEFAULT false,
    total_sessions                  INT          CHECK (total_sessions IS NULL OR total_sessions >= 0),
    daily_checkin_limit             INT          CHECK (daily_checkin_limit IS NULL OR daily_checkin_limit >= 0),
    private_room_minutes_per_month  INT          CHECK (private_room_minutes_per_month IS NULL OR private_room_minutes_per_month >= 0),
    massage_free_per_week           INT          CHECK (massage_free_per_week IS NULL OR massage_free_per_week >= 0),
    installment_allowed             BOOLEAN      NOT NULL DEFAULT false,
    is_active                       BOOLEAN      NOT NULL DEFAULT true,
    created_at                      timestamptz  NOT NULL DEFAULT now(),
    updated_at                      timestamptz  NOT NULL DEFAULT now()
);

SELECT apply_updated_at_triggers();
