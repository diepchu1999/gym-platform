# ADR-0002: Dùng Spring Boot cho Backend

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0002-use-spring-boot.md`](../../en/decisions/adr-0002-use-spring-boot.md).

## Status
Accepted (Đã chấp nhận)

## Bối cảnh
Dự án cần một backend phù hợp cho nền tảng quản lý gym đa chi nhánh lớn, với các module contract, payment, booking, check-in, inventory, staff và báo cáo.

Owner quen thuộc Java/Spring Boot và muốn một nền backend dễ bảo trì.

## Quyết định
Dùng Java + Spring Boot để phát triển backend.

## Hệ quả
Tích cực:
- Hệ sinh thái mạnh cho web API, security, quản lý transaction, testing, scheduling và truy cập database.
- Phù hợp Modular Monolith.
- Hợp với các miền nghiệp vụ kiểu doanh nghiệp.

Đánh đổi:
- Cần kỷ luật kiến trúc để tránh các service class phình to, rối.
- Cần ranh giới module nghiêm ngặt.

## Quy tắc
- Tuân thủ nguyên tắc SOLID.
- Dùng phong cách Clean/Hexagonal nơi phù hợp.
- Không để business logic trong controller.
- Application service sở hữu việc điều phối use case và ranh giới transaction.
