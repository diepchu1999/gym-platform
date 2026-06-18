# ADR-0012: Chuẩn kiến trúc module Hexagonal (adapt cho Native SQL)

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0012-hexagonal-module-architecture.md`](../../en/decisions/adr-0012-hexagonal-module-architecture.md).

## Status
Accepted (Đã chấp nhận)

## Bối cảnh
Owner có một bộ khung Hexagonal/Clean đã chạy thực tế (tách read/write, port in/out, cross-module qua `api/`, ArchitectureRulesTest). Bộ khung gốc dùng JPA cho write và UUID cho khóa — trái với baseline gym-platform (ADR-0004 Native SQL, data-model PK = BIGINT + business code). Cần một chuẩn module thống nhất, enforce được bằng test.

## Quyết định
Adopt bộ khung làm **chuẩn bắt buộc** cho mọi module `com.gym.<module>`, tài liệu tại `architecture/module-architecture.md`, với các điều chỉnh:
- **Persistence = Native SQL** (`NamedParameterJdbcTemplate` + `RowMapper` + file `.sql` qua `SqlLoader`); **KHÔNG JPA** (giữ ADR-0004). Bỏ `*JpaEntity`/`*JpaRepository`/`*JpaSpecifications`.
- **`adapter/out` thay `infrastructure`**. Cây: `api/ · domain/ · application/{command,query,view,port/in,port/out,service} · adapter/{in/rest/{admin,client},in/cli,out/persistence,out/storage}`.
- **Cross-module qua `api/<B>Directory` + `<B>Ref`** (`id: BIGINT` + `code`, không UUID) — hiện thực logical reference của ADR-0011.
- Service & adapter **package-private**; command tự validate; reload sau ghi; verb đọc chuẩn `Get/Search/List/GetStats`.
- **ArchitectureRulesTest** (source-scan thuần JDK vì Java 26) khóa R1–R5 (R5: cấm JPA trong persistence).

## Hệ quả
Tích cực: chuẩn nhất quán, dễ review, bounded context rõ, enforce bằng build. Đánh đổi: nhiều file/lớp hơn cho mỗi feature; cần `shared/` toolkit (SqlLoader, Rows, PageResponse, Validations, Enums...) trước khi dựng module đầu.

## Quy tắc
- Mọi module mới theo `module-architecture.md`; PR vi phạm bị ArchitectureRulesTest chặn.
- Bổ sung ADR-0004 (không thay thế): tái khẳng định Native SQL, làm rõ áp dụng cho cả read lẫn write trong khung Hexagonal.
- Khác bản gốc đã ghi ở `module-architecture.md` §10.
