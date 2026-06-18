# Hướng dẫn Database

> Bản tiếng Việt (canonical). English: [`../../en/architecture/database-guideline.md`](../../en/architecture/database-guideline.md).

## Lựa chọn Database
PostgreSQL là database chính.

## Phong cách Persistence
Dùng Native SQL cho persistence nghiệp vụ.

Không dùng JPA repository cho business query trừ khi có ADR sau cho phép.

Triển khai ưu tiên ban đầu:
- Spring `NamedParameterJdbcTemplate`.
- File SQL rõ ràng hoặc SQL string nơi phù hợp.
- Row mapper rõ ràng.

## Migration
Dùng Flyway trừ khi đổi bởi ADR sau.

Quy tắc:
- Mọi thay đổi schema phải qua migration.
- Không sửa schema DB thủ công ngoài migration.
- Không sửa file migration cũ sau khi đã apply.
- Tạo file migration mới cho thay đổi.

Cách đặt tên khuyến nghị:

```text
V001__init_schema.sql
V002__create_member_tables.sql
V003__create_kyc_tables.sql
```

## Thiết kế Schema
**Schema-per-module (ADR-0011):** mỗi module sở hữu một schema PostgreSQL riêng (`identity`, `branch`, `member`, `booking`, …). FK chỉ được phép TRONG cùng schema; tham chiếu chéo module là cột ID logic (không FK ở DB), đảm bảo toàn vẹn ở tầng application. Cách này giữ module sẵn sàng tách thành microservices. Xem `data-model/module-schemas.md`. Native SQL phải schema-qualified.

Kiểu tiền tố bảng chấp nhận được:

```text
member_profile
kyc_verification
membership_package
booking_booking
booking_resource_slot
payment_transaction
inventory_stock
```

## Constraint bắt buộc
Dùng constraint DB để thực thi các quy tắc quan trọng.

Ví dụ:
- Unique CCCD cho KYC đã duyệt.
- Trial 1 lần/CCCD.
- Unique payment transaction id.
- Unique class booking theo member/session.
- Unique sử dụng QR nonce.
- Tồn kho không âm.
- Số dư quota không âm.

## Mẫu chống Race Condition
Dùng atomic update cho bộ đếm và số dư.

### Sức chứa lớp

```sql
UPDATE class_session
SET booked_count = booked_count + 1
WHERE id = :sessionId
  AND booked_count < capacity;
```

### Trừ tồn kho

```sql
UPDATE inventory_stock
SET quantity = quantity - :quantity
WHERE branch_id = :branchId
  AND product_id = :productId
  AND quantity >= :quantity;
```

### Trial check-in theo ngày
Dùng unique key:

```text
(member_id, checkin_date, checkin_type)
```

hoặc logic điều kiện trong transaction.

### Payment callback
Dùng idempotency key và unique provider transaction id.

```text
UNIQUE(provider, provider_transaction_id)
```

## Quy tắc Index
Thêm index cho các filter thường dùng:
- `member_id`
- `branch_id`
- `status`
- `created_at`
- `start_time`, `end_time`
- `booking_type`
- `payment_status`
- `contract_code`
- `phone_number`
- `identity_number_hash` nếu lưu key tìm kiếm CCCD đã hash.

## Dữ liệu nhạy cảm
CCCD và tài liệu cá nhân là nhạy cảm.

Hướng dẫn:
- Không lưu ảnh tài liệu thô trong DB.
- Chỉ lưu file path/object key — file nhị phân nằm ở Object Storage tương thích S3 (ADR-0010).
- Ưu tiên pre-signed URL ngắn hạn cho object nhạy cảm (CCCD, thẻ sinh viên).
- Cân nhắc mã hóa/tokenization cho số định danh.
- Giới hạn truy cập bằng RBAC.
- Audit mọi thao tác đọc/ghi dữ liệu nhạy cảm khi khả thi.
