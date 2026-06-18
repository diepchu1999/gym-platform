# Data Model — Overview & Conventions

Tài liệu thiết kế database cho gym-platform, bám theo `business/*` và `architecture/database-guideline.md`.

> Quy trình: **thiết kế ở đây trước → owner duyệt → mới viết Flyway migration**.
> Migration đã apply thì KHÔNG sửa (tạo migration mới). Xem `development-guideline.md`.
>
> 🧩 **Schema-per-module (ADR-0011):** mỗi module 1 PostgreSQL schema, **KHÔNG FK chéo module** (ref chéo = ID logic). Bản đồ schema + danh sách logical reference: [`module-schemas.md`](module-schemas.md). Trong các file phase, "FK" tới bảng module khác = logical reference (không có DB FK).

## Quy ước chung (áp dụng mọi bảng)

| Hạng mục | Quy ước |
|---|---|
| Naming | `snake_case`, tiền tố theo module: `identity_*`, `rbac_*`, `branch_*`, `staff_*`, `member_*`, `kyc_*`, `contract`, `payment_*`, `booking_*`, `checkin_*`, `inventory_*`, `audit_*`... |
| Primary key | `BIGINT GENERATED ALWAYS AS IDENTITY`. **Không** expose PK ra API — đối ngoại dùng cột `*_code` (business code). |
| Business code | Cột `code` / `*_code` `VARCHAR`, `UNIQUE`, người đọc được (vd `BR-Q1`, `STF-000123`). Dùng cho URL/đối ngoại. |
| Timestamp | `timestamptz` (lưu UTC). `created_at`/`updated_at` `NOT NULL DEFAULT now()`. `updated_at` auto qua trigger. |
| Tiền tệ | `numeric(14,2)` + cột `currency VARCHAR(3) DEFAULT 'VND'`. |
| Status / enum | `VARCHAR` + `CHECK (...)` đúng giá trị trong `business/status-flow.md`. Không dùng PostgreSQL `ENUM` (khó migrate). |
| Boolean cờ | `BOOLEAN NOT NULL DEFAULT ...`. |
| Soft delete | Không xóa mềm toàn cục. Dùng cột `status`. Hành động nhạy cảm ghi `audit_log` (P8). |
| FK | **Chỉ FK trong cùng schema/module.** FK chéo module bị CẤM → dùng cột ID logic (BIGINT, index), toàn vẹn ở app layer (ADR-0011, [`module-schemas.md`](module-schemas.md)). Intra FK: `ON DELETE RESTRICT` mặc định; `CASCADE` cho bảng phụ thuộc thuần. |
| Module boundary | Mỗi module sở hữu bảng của mình. Module khác **không** query trực tiếp bảng — đi qua application/query service (xem `modular-monolith.md`). FK xuyên module **không được phép** ở tầng DB; dùng logical reference và enforce ở application layer. |

## Quy ước chống race condition (rule bắt buộc)

| Cơ chế | Dùng cho |
|---|---|
| `UNIQUE` constraint | trial CCCD (1 lần/CCCD), QR nonce, provider transaction id, class booking/member-session, business code |
| `CHECK (col >= 0)` | stock quantity, quota balance |
| Atomic update (`... WHERE col >= :n`) | trừ stock, trừ quota, trừ buổi class pass, tăng `booked_count < capacity` |
| `EXCLUDE USING gist` (cần `btree_gist`) | chống trùng khung giờ cùng resource (PT, private room, massage, class room) |
| Cột `version BIGINT` | optimistic lock cho cập nhật nhạy cảm |
| Idempotency key | payment callback |

## P0 — Baseline migration (`V001`)

`V001__init_baseline.sql` sẽ gồm:

1. Extensions:
   - `CREATE EXTENSION IF NOT EXISTS pgcrypto;`  — hash CCCD, `gen_random_uuid()` khi cần token.
   - `CREATE EXTENSION IF NOT EXISTS btree_gist;` — phục vụ `EXCLUDE` chống trùng giờ booking.
2. Hàm + trigger dùng chung tự cập nhật `updated_at`:

```sql
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

(Mỗi bảng có `updated_at` sẽ gắn: `CREATE TRIGGER trg_set_updated_at BEFORE UPDATE ON <table> FOR EACH ROW EXECUTE FUNCTION set_updated_at();`)

Chưa tạo bảng nghiệp vụ ở `V001`.

## Lộ trình phase (theo dependency order)

| Phase | Nội dung | File thiết kế |
|---|---|---|
| P0 | Baseline: extensions + trigger | (trong README này) |
| P1 | Identity, RBAC, Branch, Staff | [`p1-identity-org.md`](p1-identity-org.md) ✅ |
| P2 | Member, KYC, Student verify, Trial usage | [`p2-member-kyc.md`](p2-member-kyc.md) ✅ |
| P3 | Package, Membership, Contract, Order, Payment, Installment | [`p3-package-contract-payment.md`](p3-package-contract-payment.md) ✅ |
| P4 | Check-in (token, log) | [`p4-checkin.md`](p4-checkin.md) ✅ |
| P5 | Booking core (booking, resource slot, hold, event) | [`p5-booking-core.md`](p5-booking-core.md) ✅ |
| P6 | Group class / PT / Private room / Massage (+ quota) | [`p6-booking-verticals.md`](p6-booking-verticals.md) ✅ |
| P7 | Inventory / Pantry / Equipment-Maintenance | [`p7-inventory-pantry-equipment.md`](p7-inventory-pantry-equipment.md) ✅ |
| P8 | CRM / Rating / Promotion / Notification / Report / Audit | [`p8-crm-rating-promotion-notification-audit.md`](p8-crm-rating-promotion-notification-audit.md) ✅ |
| P9 | Messaging: Transactional Outbox + consumer idempotency | [`p9-messaging-outbox.md`](p9-messaging-outbox.md) ✅ |

> Toàn bộ thiết kế bảng đã hoàn tất (P1–P9). Bước kế tiếp: viết Flyway migration khớp từng phase (sau khi owner duyệt).

## Mapping phase → file migration (dự kiến)

- P0: `V001` baseline (extensions + trigger)
- P1: `V002` identity_rbac · `V003` branch · `V004` staff · `V005` seed_rbac
- P2: `V006` member · `V007` kyc
- P3: `V008` package_plan · `V009` contract_membership · `V010` order_payment · `V011` installment
- P4: `V012` checkin
- P5: `V013` booking_core (+ EXCLUDE)
- P6: `V014` group_class · `V015` pt · `V016` private_room · `V017` massage
- P7: `V018` product_inventory · `V019` purchase_transfer_adjust · `V020` equipment_maintenance
- P8: `V021` crm · `V022` rating_promotion · `V023` notification · `V024` audit
- P9: `V025` outbox

(Số hiệu chốt lại khi viết migration; `outbox_event` có thể đẩy sớm nếu module nào cần phát event trước.)

## Hạ tầng đã duyệt chạm vào data ở đâu

Tham chiếu [`../solution-architecture.md`](../solution-architecture.md) + ADR-0006…0010.

- **Keycloak (ADR-0006)**: `identity_user_account` là bảng ánh xạ tới `keycloak_user_id` (không lưu mật khẩu). RBAC theo chi nhánh (`rbac_*`, `staff_branch_assignment`) vẫn ở DB.
- **Redis (ADR-0009)**: KHÔNG phải bảng. Lo state ephemeral (QR token TTL, nonce, lock chống quét trùng, rate limit). **Mọi invariant đúng-đắn vẫn phải có constraint/atomic update trong PostgreSQL** (vd trial 1/CCCD, payment txn, class booking uniqueness).
- **Object Storage (ADR-0010)**: cột ảnh/tài liệu (`kyc_request.front_image_url`, contract PDF, invoice, media) chỉ lưu **object key/URL**, không lưu bytes.
- **Outbox (ADR-0007)**: bảng `outbox_event` (thiết kế ở một phase riêng — "messaging") được ghi trong cùng transaction nghiệp vụ; Kafka nối vào sau.
