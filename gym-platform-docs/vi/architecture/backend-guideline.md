# Hướng dẫn Backend

> Bản tiếng Việt (canonical). English: [`../../en/architecture/backend-guideline.md`](../../en/architecture/backend-guideline.md).

## Nền tảng kỹ thuật
- Java.
- Spring Boot.
- Modular Monolith.
- Nguyên tắc SOLID.
- Phong cách Clean Architecture / Hexagonal Architecture.
- PostgreSQL.
- Native SQL.
- Ưu tiên `NamedParameterJdbcTemplate` cho repository adapter ở giai đoạn đầu.
- Không dùng JPA repository cho business query trừ khi có ADR sau cho phép.

## Bố cục package mỗi module
Chuẩn bắt buộc nằm ở [`module-architecture.md`](module-architecture.md) (Hexagonal, đã adapt cho Native SQL — ADR-0012). Tóm tắt:

```text
com/gym/<module>/
 ├── api/                 # cổng public cross-module (<X>Directory, <X>Ref)
 ├── domain/             # aggregate, enum, value object (KHÔNG framework)
 ├── application/        # command, query, view, port/in, port/out, service
 └── adapter/            # in/rest/{admin,client}, in/cli, out/persistence (Native SQL), out/storage
```

> **`adapter/out` thay cho `infrastructure`** (không dùng package `infrastructure`). Persistence dùng `NamedParameterJdbcTemplate` + `RowMapper` + file `.sql`, **KHÔNG JPA**. Module nhỏ bỏ bớt layer không dùng; hướng phụ thuộc luôn hướng vào trong.

## Hướng phụ thuộc
Được phép:

```text
api -> application -> domain
application -> port
infrastructure -> port/domain mapping
```

Không được:
- Controller gọi thẳng repository.
- Module A dùng thẳng repository của Module B.
- Domain object phụ thuộc Spring, HTTP, SQL, hay annotation framework.
- Business rule chỉ nằm trong SQL mapper.

## Quy tắc Native SQL Repository
Repository nên:
- Dùng parameter binding.
- Giữ SQL dễ đọc.
- Ưu tiên named parameter thay vì nối chuỗi.
- Map row DB sang domain object hoặc persistence model rõ ràng.
- Giữ business validation ở tầng application/domain.

Ví dụ kiểu `NamedParameterJdbcTemplate`:

```java
String sql = """
    UPDATE class_session
    SET booked_count = booked_count + 1
    WHERE id = :sessionId
      AND booked_count < capacity
""";

int updated = jdbc.update(sql, Map.of("sessionId", sessionId));
```

## Quy tắc Transaction
Application service sở hữu ranh giới transaction.

Transaction bắt buộc cho:
- Xác nhận booking.
- Giữ và giải phóng slot.
- Cập nhật trạng thái thanh toán.
- Kích hoạt hợp đồng.
- Kích hoạt membership.
- Tạo bản ghi QR check-in.
- Trừ quota.
- Trừ buổi class pass.
- Trừ tồn kho.

## Quy tắc Race Condition
Không dùng read-then-write cho các bộ đếm nhạy cảm.

Ưu tiên atomic SQL:

```sql
UPDATE inventory_stock
SET quantity = quantity - :qty
WHERE product_id = :productId
  AND branch_id = :branchId
  AND quantity >= :qty;
```

Kiểm số dòng bị ảnh hưởng:
- `1` nghĩa là thành công.
- `0` nghĩa là không đủ tồn kho hoặc trạng thái không hợp lệ.

## Xử lý lỗi
Dùng business exception rõ ràng.

Ví dụ:
- `MEMBER_NOT_FOUND`
- `PACKAGE_EXPIRED`
- `TRIAL_DAILY_CHECKIN_LIMIT_REACHED`
- `BOOKING_SLOT_NOT_AVAILABLE`
- `PAYMENT_ALREADY_PROCESSED`
- `INSUFFICIENT_CLASS_PASS_BALANCE`
- `PRIVATE_ROOM_QUOTA_EXCEEDED`
- `OUT_OF_STOCK`

## Testing
Mỗi use case nhạy cảm phải có test cho:
- Đường thành công.
- Input không hợp lệ.
- Bị từ chối quyền.
- Vi phạm business rule.
- Request trùng/idempotency.
- Đường nhạy race-condition nếu khả thi.
