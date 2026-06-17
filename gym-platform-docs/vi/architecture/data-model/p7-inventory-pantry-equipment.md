# P7 — Inventory / POS / Pantry / Equipment & Maintenance

Nguồn: `modules/inventory-pantry.md`, `equipment-maintenance.md`, `business-rules.md` (BR-047…054).

## Phạm vi
Inventory/POS: `product_partner`, `product`, `inventory_stock`, `stock_movement`, `product_batch`, `purchase_order(+item)`, `stock_transfer`, `stock_adjustment`.
Pantry: dùng lại `product` (type PANTRY) + `product_batch`. POS sale dùng lại `order`/`order_item` (P3).
Equipment: `equipment_asset`, `maintenance_ticket`, `maintenance_history`.

## ERD
```mermaid
erDiagram
    product_partner ||--o{ product : supplies
    product ||--o{ inventory_stock : stocked
    branch_branch ||--o{ inventory_stock : at
    product ||--o{ product_batch : batched
    product ||--o{ stock_movement : ledger
    equipment_asset ||--o{ maintenance_ticket : reported
    maintenance_ticket ||--o{ maintenance_history : logs
```

## Inventory / POS

### `product_partner`
id · code UNIQUE · name · type CHECK IN ('PARTNER','BRAND') · contact · active BOOLEAN · created_at/updated_at.

### `product`
id · sku UNIQUE · name · category · product_type CHECK IN ('GYM_SUPPORT','SUPPLEMENT','PANTRY') · partner_id FK NULL · price NUMERIC(14,2) · currency · is_pantry BOOLEAN DEFAULT false · track_batch BOOLEAN DEFAULT false (pantry=true) · active BOOLEAN · created_at/updated_at.

### `inventory_stock` (tồn theo chi nhánh — BR-048)
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| id | BIGINT | PK identity |
| product_id | BIGINT | FK product |
| branch_id | BIGINT | FK branch_branch |
| quantity | INT | NOT NULL DEFAULT 0, **CHECK (quantity>=0)** |
| low_stock_threshold | INT | DEFAULT 0 |
| version | BIGINT | NOT NULL DEFAULT 0 |
| created_at/updated_at | timestamptz | trigger |
- `UNIQUE(product_id, branch_id)`.
- **Trừ kho atomic (BR-049)**: `UPDATE inventory_stock SET quantity=quantity-:q WHERE product_id=:p AND branch_id=:b AND quantity>=:q;` (0 dòng ⇒ `OUT_OF_STOCK`).

### `stock_movement` (sổ cái)
id · product_id FK · branch_id FK · movement_type CHECK IN ('IMPORT','SALE','TRANSFER_IN','TRANSFER_OUT','ADJUSTMENT','RETURN') · quantity INT (có dấu) · reference_type · reference_id · created_by FK staff · created_at.

### `product_batch` (pantry hạn dùng/lô — BR-051)
id · product_id FK · branch_id FK · batch_no · expiry_date DATE · quantity INT CHECK(>=0) · created_at/updated_at. `UNIQUE(product_id, branch_id, batch_no)`. Trừ theo batch atomic tương tự (FEFO theo expiry).

### `purchase_order` + `purchase_order_item`
purchase_order: id · po_code UNIQUE · partner_id FK · branch_id FK · status CHECK IN ('DRAFT','ORDERED','RECEIVED','CANCELLED') · total_amount · created_at/updated_at.
purchase_order_item: id · purchase_order_id FK · product_id FK · quantity CHECK(>0) · unit_cost · line_amount.

### `stock_transfer`
id · code UNIQUE · product_id FK · from_branch_id FK · to_branch_id FK (CHECK from<>to) · quantity CHECK(>0) · status CHECK IN ('DRAFT','IN_TRANSIT','COMPLETED','CANCELLED') · created_at/updated_at.

### `stock_adjustment`
id · code UNIQUE · product_id FK · branch_id FK · quantity_delta INT · reason TEXT · created_by FK staff · created_at. (Mọi điều chỉnh ghi audit — P8.)

> **POS sale**: tạo `order`(order_type POS_PRODUCT/PANTRY) + `order_item` + `payment` (P3); khi PAID → ghi `stock_movement(SALE)` + atomic trừ `inventory_stock`/`product_batch` trong 1 transaction. Pantry kiểm giờ 06:00–22:00 ở application (BR-050).

## Equipment & Maintenance

### `equipment_asset`
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| id | BIGINT | PK identity |
| asset_code | VARCHAR(40) | UNIQUE NOT NULL |
| name | VARCHAR(150) | NOT NULL |
| category | VARCHAR(60) | |
| branch_id | BIGINT | FK branch_branch |
| room_id | BIGINT | FK branch_room NULL |
| area | VARCHAR(60) | NULL |
| status | VARCHAR(20) | NOT NULL DEFAULT 'ACTIVE', CHECK IN ('ACTIVE','NEED_MAINTENANCE','UNDER_MAINTENANCE','BROKEN','RETIRED') |
| purchase_date | DATE | NULL |
| supplier | VARCHAR(150) | NULL |
| next_maintenance_date | DATE | NULL |
| qr_code | VARCHAR(80) | NULL (quét báo hỏng) |
| created_at/updated_at | timestamptz | trigger |

### `maintenance_ticket`
id · ticket_code UNIQUE · equipment_id FK · branch_id FK · reporter_type CHECK IN ('STAFF','MEMBER') · reported_by BIGINT · assigned_to FK staff NULL · issue_description TEXT · image_url VARCHAR(255) NULL (S3) · status CHECK IN ('NEW','ASSIGNED','IN_PROGRESS','WAITING_CUSTOMER','RESOLVED','CLOSED') · priority CHECK IN ('LOW','MEDIUM','HIGH','URGENT') · cost NUMERIC(14,2) NULL · resolved_at · created_at/updated_at. (status-flow Ticket)

### `maintenance_history`
id · equipment_id FK · ticket_id FK NULL · action VARCHAR(60) · note TEXT · cost NUMERIC(14,2) NULL · performed_by FK staff · performed_at timestamptz · created_at.

## Race-condition (P7)
- `inventory_stock.quantity>=0` + atomic trừ; `product_batch.quantity>=0`.
- Payment POS idempotent (P3) → tránh trừ kho 2 lần.
- `UNIQUE(product_id, branch_id)` cho tồn.

## Migration dự kiến
`V018__product_inventory.sql` · `V019__purchase_transfer_adjust.sql` · `V020__equipment_maintenance.sql`.
