# ADR-0004: Dùng Native SQL thay vì JPA Repository

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0004-use-native-sql-instead-of-jpa.md`](../../en/decisions/adr-0004-use-native-sql-instead-of-jpa.md).

## Status
Accepted (Đã chấp nhận)

## Bối cảnh
Dự án có nhiều thao tác quan trọng về nghiệp vụ cần kiểm soát SQL chặt: đặt slot booking, trừ quota, trừ stock, idempotency thanh toán, chống trùng check-in và báo cáo.

Owner ưu tiên Native SQL thay vì JPA cho dự án này.

## Quyết định
Dùng Native SQL cho persistence nghiệp vụ. Không dùng JPA repository cho business query trừ khi có quyết định sau cho phép.

Triển khai ưu tiên ban đầu là Spring `NamedParameterJdbcTemplate`.

## Hệ quả
Tích cực:
- Kiểm soát hoàn toàn SQL và các update nhạy cảm với transaction.
- Dễ tối ưu query và index.
- Dễ hiện thực các mẫu atomic update.
- Tránh lazy loading vô tình và query ẩn của ORM.

Đánh đổi:
- Phải tự map row nhiều hơn.
- Nhiều boilerplate cho CRUD.
- Dev phải giữ SQL và mapping miền gọn gàng.

## Quy tắc
- Dùng parameter binding.
- Không nối chuỗi input người dùng vào SQL.
- Giữ SQL dễ đọc.
- Thêm test cho các repository method quan trọng.
- Giữ business rule ở application/domain; repository chỉ lo persistence.
