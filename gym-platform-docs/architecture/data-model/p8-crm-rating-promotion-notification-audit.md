# P8 — CRM, Rating, Promotion, Notification, Report, Audit

Nguồn: `modules/crm-customer-care.md`, `business/domain-map.md`, `business-rules.md`, `status-flow.md`.

## Phạm vi
CRM: `lead`, `care_task`, `care_note`, `ticket`. Rating: `rating`. Promotion: `coupon`, `coupon_redemption`, `campaign`, `referral`. Notification: `notification_message`. Report: views (không bảng gốc). Audit: `audit_log`.

## CRM / Customer Care

### `lead`
id · code UNIQUE · full_name · phone · email NULL · source · interested_branch_id FK NULL · interested_service · status CHECK IN ('NEW','CONTACTED','INTERESTED','VISITED','TRIAL_REGISTERED','CONVERTED','LOST') · assigned_to FK staff NULL · next_follow_up_at timestamptz NULL · converted_member_id FK member_profile NULL · created_at/updated_at.

### `care_task`
id · member_id FK NULL · lead_id FK NULL · task_type CHECK IN ('TRIAL_FOLLOWUP','NO_SHOW_CALL','RENEWAL','COMPLAINT_FOLLOWUP','WELCOME') · related_booking_id FK booking NULL · assigned_to FK staff · due_at timestamptz · status CHECK IN ('OPEN','IN_PROGRESS','DONE','CANCELLED') · result TEXT · note TEXT · created_at/updated_at.
- No-show workflow (BR-021): tạo `care_task(NO_SHOW_CALL)` tại giờ bắt đầu booking khi member chưa check-in; CSKH giữ slot ≤30' (đổi booking sang WAITING_CUSTOMER_CONFIRMATION ở P5).

### `care_note` (timeline hội viên)
id · member_id FK · author_staff_id FK · note_type VARCHAR(40) · note TEXT · created_at.

### `ticket` (khiếu nại/hỗ trợ/refund request)
id · ticket_code UNIQUE · member_id FK NULL · branch_id FK NULL · category VARCHAR(60) · priority CHECK IN ('LOW','MEDIUM','HIGH','URGENT') · status CHECK IN ('NEW','ASSIGNED','IN_PROGRESS','WAITING_CUSTOMER','RESOLVED','CLOSED') · assigned_to FK staff NULL · description TEXT · resolution TEXT · created_at/updated_at.

## Rating / Feedback

### `rating`
id · subject_type CHECK IN ('PT','CLASS','BRANCH','SERVICE','EQUIPMENT','SUPPORT') · subject_id BIGINT · author_member_id FK member_profile · booking_id FK booking NULL · rating SMALLINT CHECK (1..5) · comment TEXT · author_visible_to_subject BOOLEAN NOT NULL DEFAULT true · created_at.
- PT dùng bảng riêng `pt_rating` (P6) vì có rule ẩn danh đặc thù; `rating` này cho CLASS/BRANCH/SERVICE/EQUIPMENT/SUPPORT.
- Quyền xem tác giả: `RATING_VIEW_AUTHOR`.

## Promotion / Coupon

### `coupon`
| Cột | Kiểu | Ràng buộc |
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
- **Chống dùng vượt mức (atomic)**: `UPDATE coupon SET used_count=used_count+1 WHERE id=:id AND (usage_limit IS NULL OR used_count<usage_limit);`

### `coupon_redemption`
id · coupon_id FK · member_id FK · order_id FK · redeemed_at timestamptz · created_at.
- `UNIQUE(coupon_id, order_id)`; chống 1 member dùng quá: `UNIQUE(coupon_id, member_id)` nếu `per_member_limit=1`.

### `campaign`
id · code UNIQUE · name · type · start_at · end_at · status CHECK IN ('DRAFT','RUNNING','ENDED').

### `referral`
id · referrer_member_id FK · referred_member_id FK · status CHECK IN ('PENDING','REWARDED','VOID') · reward NUMERIC(14,2) NULL · created_at. `UNIQUE(referred_member_id)` (1 người chỉ được giới thiệu 1 lần).

## Notification

### `notification_message`
id · member_id FK NULL · channel CHECK IN ('EMAIL','SMS','PUSH','ZALO') · template_code · payload JSONB · status CHECK IN ('PENDING','SENT','FAILED') · scheduled_at · sent_at · retry_count INT DEFAULT 0 · created_at.
- Sinh ra từ consumer của outbox (P-messaging) khi Kafka có; trước đó có thể xử lý in-process từ `outbox_event`.

## Report / Analytics
- Không có bảng gốc mới. Dùng **read-only query/SQL view** trên các bảng nghiệp vụ (doanh thu, booking, PT, tồn kho, conversion).
- Khi Kafka có (later): bảng **projection** riêng do consumer cập nhật (eventual consistency). Thiết kế khi triển khai.

## Audit

### `audit_log` (append-only — KHÔNG xoá, theo CLAUDE.md)
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| id | BIGINT | PK identity |
| actor_type | VARCHAR(10) | CHECK IN ('STAFF','MEMBER','SYSTEM') |
| actor_id | BIGINT | NULL |
| action | VARCHAR(60) | NOT NULL |
| entity_type | VARCHAR(60) | NOT NULL |
| entity_id | BIGINT | NULL |
| before_data | JSONB | NULL |
| after_data | JSONB | NULL |
| ip_address | VARCHAR(45) | NULL |
| created_at | timestamptz | NOT NULL DEFAULT now() |
- Ghi cho hành động nhạy cảm: contract, payment, refund, package activation, permission change, KYC approval, inventory adjustment (domain-map Audit Log).
- Index: `(entity_type, entity_id)`, `(actor_type, actor_id)`, `(created_at)`. Append-only — không UPDATE/DELETE.

## Migration dự kiến
`V021__crm.sql` · `V022__rating_promotion.sql` · `V023__notification.sql` · `V024__audit.sql`.
