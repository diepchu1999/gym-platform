# Data Model — Overview & Conventions

Tài liệu thiết kế database cho gym-platform, bám theo `business/*` và `architecture/database-guideline.md`.

> Quy trình: **thiết kế ở đây trước → owner duyệt → mới viết Flyway migration**.
> Migration đã apply thì KHÔNG sửa (tạo migration mới). Xem `development-guideline.md`.

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
| FK | Đặt FK rõ ràng + index cột FK. `ON DELETE` mặc định `RESTRICT`; chỉ `CASCADE` cho bảng phụ thuộc thuần (vd `rbac_role_permission`). |
| Module boundary | Mỗi module sở hữu bảng của mình. Module khác **không** query trực tiếp bảng — đi qua application/query service (xem `modular-monolith.md`). FK xuyên module được phép ở tầng DB, nhưng truy cập code phải qua service. |

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
| **P1** | **Identity, RBAC, Branch, Staff** | [`p1-identity-org.md`](p1-identity-org.md) |
| P2 | Member, KYC, Student verify, Trial usage | _chưa làm_ |
| P3 | Package, Membership, Contract, Order, Payment, Installment | _chưa làm_ |
| P4 | Check-in (token, log) | _chưa làm_ |
| P5 | Booking core (booking, hold, event, resource) | _chưa làm_ |
| P6 | Group class / PT / Private room / Massage (+ quota) | _chưa làm_ |
| P7 | Inventory / Pantry / Equipment-Maintenance | _chưa làm_ |
| P8 | CRM / Rating / Promotion / Notification / Report / Audit | _chưa làm_ |

## Mapping phase → file migration (dự kiến)

`V001` baseline · `V002` rbac+identity · `V003` branch · `V004` staff · `V005` seed rbac roles · ... (số hiệu chốt khi viết migration).
