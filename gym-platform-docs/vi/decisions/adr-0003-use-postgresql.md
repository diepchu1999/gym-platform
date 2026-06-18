# ADR-0003: Dùng PostgreSQL

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0003-use-postgresql.md`](../../en/decisions/adr-0003-use-postgresql.md).

## Status
Accepted (Đã chấp nhận)

## Bối cảnh
Hệ thống cần nhất quán quan hệ đáng tin cậy cho booking, payment, contract, check-in, quota và inventory.

## Quyết định
Dùng PostgreSQL làm database chính.

## Hệ quả
Tích cực:
- Nhất quán transaction mạnh.
- Hỗ trợ index và constraint mạnh.
- Hỗ trợ các constraint nâng cao hữu ích cho booking và xung đột khoảng thời gian (time-range).
- Hợp với Native SQL.

Đánh đổi:
- Cần kỷ luật migration.
- Cần tinh chỉnh query/index khi dữ liệu lớn dần.

## Quy tắc
- Dùng Flyway migration trừ khi đổi sau này.
- Dùng DB constraint cho các bất biến nghiệp vụ quan trọng.
- Dùng atomic SQL update cho bộ đếm quota/stock/slot.
