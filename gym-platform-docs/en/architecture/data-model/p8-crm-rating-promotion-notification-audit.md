# P8 — CRM, Rating, Promotion, Notification, Report, Audit

> English version. Vietnamese (canonical): [`../../../vi/architecture/data-model/p8-crm-rating-promotion-notification-audit.md`](../../../vi/architecture/data-model/p8-crm-rating-promotion-notification-audit.md).

Sources: `modules/crm-customer-care.md`, `business/domain-map.md`, `business-rules.md`, `status-flow.md`.

## Scope
CRM: `crm_lead`, `crm_care_task`, `crm_care_note`, `crm_ticket`. Rating: `rating`. Promotion: `coupon`, `coupon_redemption`, `campaign`, `referral`. Notification: `notification_message`. Report: views (no base tables). Audit: `audit_log`.

## CRM / Customer Care

### `crm_lead`
id · code UNIQUE · full_name · phone · email NULL · source · interested_branch_id (logical→branch) · interested_service · status CHECK IN ('NEW','CONTACTED','INTERESTED','VISITED','TRIAL_REGISTERED','CONVERTED','LOST') · assigned_to (logical→staff) · next_follow_up_at · converted_member_id (logical→member) · created_at/updated_at.

### `crm_care_task`
id · member_id (logical→member) · lead_id FK crm_lead (intra) · task_type CHECK IN ('TRIAL_FOLLOWUP','NO_SHOW_CALL','RENEWAL','COMPLAINT_FOLLOWUP','WELCOME') · related_booking_id (logical→booking) · assigned_to (logical→staff) · due_at · status CHECK IN ('OPEN','IN_PROGRESS','DONE','CANCELLED') · result · note · created_at/updated_at.
- No-show workflow (BR-021): create `crm_care_task(NO_SHOW_CALL)` at booking start time when the member has not checked in; CSKH holds the slot ≤30' (booking moves to WAITING_CUSTOMER_CONFIRMATION in P5).

### `crm_care_note` (member timeline)
id · member_id (logical→member) · author_staff_id (logical→staff) · note_type · note TEXT · created_at.

### `crm_ticket` (complaint/support/refund request)
id · ticket_code UNIQUE · member_id (logical→member) · branch_id (logical→branch) · category · priority CHECK IN ('LOW','MEDIUM','HIGH','URGENT') · status CHECK IN ('NEW','ASSIGNED','IN_PROGRESS','WAITING_CUSTOMER','RESOLVED','CLOSED') · assigned_to (logical→staff) · description · resolution · created_at/updated_at.

## Rating / Feedback

### `rating`
id · subject_type CHECK IN ('PT','CLASS','BRANCH','SERVICE','EQUIPMENT','SUPPORT') · subject_id BIGINT · author_member_id (logical→member) · booking_id (logical→booking) · rating SMALLINT CHECK (1..5) · comment · author_visible_to_subject BOOLEAN DEFAULT true · created_at.
- PT uses its own `pt_rating` (P6) for the anonymity rule; this `rating` covers CLASS/BRANCH/SERVICE/EQUIPMENT/SUPPORT.
- Author visibility permission: `RATING_VIEW_AUTHOR`.

## Promotion / Coupon

### `coupon`
| Column | Type | Constraint |
|---|---|---|
| id | BIGINT | PK identity |
| code | VARCHAR(40) | UNIQUE NOT NULL |
| name | VARCHAR(150) | |
| discount_type | VARCHAR(10) | CHECK IN ('PERCENT','FIXED') |
| discount_value | NUMERIC(14,2) | CHECK (>=0) |
| applies_to | VARCHAR(20) | CHECK IN ('PACKAGE','POS','ALL') |
| is_student_discount | BOOLEAN | DEFAULT false |
| valid_from / valid_to | timestamptz | |
| usage_limit | INT | NULL |
| used_count | INT | NOT NULL DEFAULT 0, CHECK (used_count>=0) |
| per_member_limit | INT | DEFAULT 1 |
| status | VARCHAR(20) | CHECK IN ('ACTIVE','PAUSED','EXPIRED') |
| created_at/updated_at | timestamptz | trigger |
- **Atomic over-use guard**: `UPDATE coupon SET used_count=used_count+1 WHERE id=:id AND (usage_limit IS NULL OR used_count<usage_limit);`

### `coupon_redemption`
id · coupon_id FK coupon (intra) · member_id (logical→member) · order_id (logical→payment.customer_order) · redeemed_at · created_at · UNIQUE(coupon_id, order_id).

### `campaign`
id · code UNIQUE · name · type · start_at · end_at · status CHECK IN ('DRAFT','RUNNING','ENDED').

### `referral`
id · referrer_member_id (logical→member) · referred_member_id (logical→member) UNIQUE · status CHECK IN ('PENDING','REWARDED','VOID') · reward NUMERIC(14,2) NULL · created_at.

## Notification

### `notification_message`
id · member_id (logical→member) · channel CHECK IN ('EMAIL','SMS','PUSH','ZALO') · template_code · payload JSONB · status CHECK IN ('PENDING','SENT','FAILED') · scheduled_at · sent_at · retry_count INT DEFAULT 0 · created_at.
- Produced by outbox consumers (P9) once Kafka exists; before that, may be handled in-process from `outbox_event`.

## Report / Analytics
- No new base tables. Use **read-only query/SQL views** over business tables (revenue, booking, PT, inventory, conversion).
- Once Kafka lands (later): dedicated **projection** tables updated by consumers (eventual consistency). Designed at implementation time.

## Audit

### `audit_log` (append-only — NEVER deleted, per CLAUDE.md)
id · actor_type CHECK IN ('STAFF','MEMBER','SYSTEM') · actor_id BIGINT NULL · action VARCHAR(60) · entity_type VARCHAR(60) · entity_id BIGINT NULL · before_data JSONB · after_data JSONB · ip_address · created_at.
- Written for sensitive actions: contract, payment, refund, package activation, permission change, KYC approval, inventory adjustment (domain-map Audit Log).
- Indexes: `(entity_type, entity_id)`, `(actor_type, actor_id)`, `(created_at)`. Append-only — no UPDATE/DELETE.

## Planned migrations
`V021__crm.sql` · `V022__rating_promotion.sql` · `V023__notification.sql` · `V024__audit.sql`.
