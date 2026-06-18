# ADR-0011: Mỗi module một schema, không FK chéo module

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0011-schema-per-module-no-cross-fk.md`](../../en/decisions/adr-0011-schema-per-module-no-cross-fk.md).

## Status
Accepted (Đã chấp nhận)

## Bối cảnh
Hệ thống hiện là Modular Monolith nhưng dự kiến sau này tách một số module thành microservices. Khi để mọi bảng trong một schema `public` với ~84 FK chéo module, các module bị dính chặt ở tầng DB — chặn việc tách (một microservice không thể giữ FK vào DB của service khác).

## Quyết định
Mỗi module sở hữu một **schema** PostgreSQL riêng (`identity`, `branch`, `staff`, `member`, … `messaging`). **Chỉ cho phép FK trong cùng schema/module.** Mọi tham chiếu chéo module là **logical reference** — cột ID `BIGINT` (có index), toàn vẹn do application layer đảm bảo (+ domain event/outbox khi cần). `flyway_schema_history` đặt ở `public`.

## Hệ quả
Tích cực: module sẵn sàng tách; bounded context rõ ràng; không coupling chéo service ở DB. Đánh đổi: mất referential integrity ở DB cho ref chéo module (phải validate ở app); SQL phải schema-qualified; dọn orphan cần xử lý ở app/event.

## Quy tắc
- FK nội bộ module: giữ. FK chéo module: cấm → dùng cột ID + validate ở app.
- Native SQL phải schema-qualified (`FROM member.member_profile`).
- Bất biến đã verify: số FK có schema con ≠ schema cha phải bằng **0**.
- Bản đồ schema + danh sách logical reference: `data-model/module-schemas.md`.
- Thay thế ghi chú cũ "một schema public + tiền tố bảng" trong `data-model/README.md` / `database-guideline.md`.
