# ADR-0011: Schema-per-Module, No Cross-Module FK / Mỗi module một schema, không FK chéo module

## Status
Accepted / Đã chấp nhận

## Context / Bối cảnh
**EN —** The platform is a Modular Monolith now but is expected to extract some modules into microservices later. With all tables in one `public` schema and ~84 cross-module foreign keys, modules were tightly coupled at the DB level — a hard blocker for extraction (a microservice cannot hold a FK into another service's database).

**VI —** Hệ thống hiện là Modular Monolith nhưng dự kiến sau này tách một số module thành microservices. Khi để mọi bảng trong một schema `public` với ~84 FK chéo module, các module bị dính chặt ở tầng DB — chặn việc tách (một microservice không thể giữ FK vào DB của service khác).

## Decision / Quyết định
**EN —** Each module owns its own PostgreSQL **schema** (`identity`, `branch`, `staff`, `member`, … `messaging`). **Foreign keys are allowed only within the same schema/module.** All cross-module references are **logical references** — plain `BIGINT` ID columns (indexed), with integrity enforced in the application layer (and via domain events / outbox where appropriate). `flyway_schema_history` stays in `public`.

**VI —** Mỗi module sở hữu một **schema** PostgreSQL riêng (`identity`, `branch`, `staff`, `member`, … `messaging`). **Chỉ cho phép FK trong cùng schema/module.** Mọi tham chiếu chéo module là **logical reference** — cột ID `BIGINT` (có index), toàn vẹn do application layer đảm bảo (+ domain event/outbox khi cần). `flyway_schema_history` đặt ở `public`.

## Consequences / Hệ quả
**EN —** Positive: modules are extraction-ready; clear bounded contexts; no DB-level cross-service coupling. Trade-offs: loses DB referential integrity for cross-module refs (must validate in app); SQL must be schema-qualified; orphan cleanup needs app/event handling.

**VI —** Tích cực: module sẵn sàng tách; bounded context rõ ràng; không coupling chéo service ở DB. Đánh đổi: mất referential integrity ở DB cho ref chéo module (phải validate ở app); SQL phải schema-qualified; dọn orphan cần xử lý ở app/event.

## Rules / Quy tắc
- Intra-module FK: keep. Cross-module FK: forbidden → use ID column + app validation.
- Native SQL must be schema-qualified (`FROM member.member_profile`).
- Verified invariant: `SELECT count(*) ... WHERE child_schema <> parent_schema AND contype='f'` must be **0**.
- Schema map + logical reference inventory: `data-model/module-schemas.md`.
- Supersedes the earlier "single public schema + table prefix" note in `data-model/README.md` / `database-guideline.md`.
