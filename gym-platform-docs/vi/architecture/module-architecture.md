# Khung kiến trúc module (Hexagonal / Clean Architecture)

> Bản tiếng Việt (canonical). English: [`../../en/architecture/module-architecture.md`](../../en/architecture/module-architecture.md).
>
> Đây là **chuẩn bắt buộc** cho mọi module backend trong `com.gym.<module>`.
> Tài liệu này được adapt từ một bộ khung Hexagonal đã chạy thực tế, chỉnh lại cho baseline của gym-platform:
> **Native SQL (NamedParameterJdbcTemplate), KHÔNG JPA** (ADR-0004) · **PK BIGINT + business code** (không UUID) · **schema-per-module, không FK chéo** (ADR-0011).

---

## 1. Nguyên tắc cốt lõi

- **Modular monolith**: mỗi module là một bounded context, có **schema Postgres riêng** (`member`, `booking`, `payment`, ...). **Không FK chéo schema** — tham chiếu giữa module chỉ bằng **giá trị** (`id: BIGINT` + `code: String`), không phải UUID. (ADR-0011)
- **Hexagonal**: `domain` + `application` (lõi nghiệp vụ) **không** phụ thuộc framework. Mọi thứ "bẩn" (web, JDBC, file, module khác) nằm ở `adapter`.
- **Dependency rule** — phụ thuộc chỉ hướng vào trong:
  ```
  adapter ──> application ──> domain
  adapter ──> application.port (in/out)
  ```
  `domain` không import gì từ `application`/`adapter`. `application` không import `adapter`. Chỉ adapter mới được import Spring/JDBC và `api` của module khác.
- **Persistence = Native SQL**: adapter dùng `NamedParameterJdbcTemplate` + `RowMapper` + file `.sql` (qua `SqlLoader`). **KHÔNG** `*JpaEntity`/`*JpaRepository`/`*JpaSpecifications` (khác với bản gốc — ta theo ADR-0004).

---

## 2. Cây thư mục chuẩn

```
com/gym/<module>/
├── api/                     # (tùy chọn) cổng PUBLIC cho module khác dùng
│   ├── <X>Directory.java        # interface tra cứu/kiểm tra cross-module
│   └── <X>Ref.java              # DTO chia sẻ cross-module (record): id(long) + code + field tối thiểu
├── domain/                  # lõi thuần: aggregate + enum + value object (KHÔNG framework)
│   ├── <X>.java                 # aggregate / entity nghiệp vụ
│   ├── <X>Status.java           # enum trạng thái
│   └── <X>StatusAction.java     # enum hành động (nếu có máy trạng thái)
├── application/
│   ├── command/             # input GHI đã validate:   <Verb><X>Command (+ <Resource>CommandValidation)
│   ├── query/               # input ĐỌC + sắp xếp:      Search<X>Query, List<X>Query
│   ├── view/                # read model TRẢ RA:        <X>Detail, <X>ListItem, <X>Summary, <X>Stats
│   ├── port/in/             # use case (cổng vào):      <Verb><X>UseCase
│   ├── port/out/            # SPI app cần (cổng ra):    Read<X>Port, Write<X>Port
│   └── service/             # impl use case:            <X>CommandService, <X>QueryService
└── adapter/
    ├── in/rest/             # REST tách theo audience (admin vs client)
    │   ├── admin/           # /api/v1/admin/**  → Admin<X>Controller
    │   │   ├── request/     # body request:  <Verb><X>Request
    │   │   └── response/    # body response: <X>...Response (+ static fromDomain)
    │   └── client/          # /api/v1/client/** → Client<X>Controller
    │       ├── request/
    │       └── response/
    ├── in/cli/              # (tùy chọn) ApplicationRunner cho import/seed
    └── out/
        ├── persistence/     # Native SQL adapter (KHÔNG JPA)
        │   ├── <X>ReadAdapter.java     # implements Read<X>Port (NamedParameterJdbcTemplate + RowMapper)
        │   ├── <X>WriteAdapter.java    # implements Write<X>Port (INSERT/UPDATE/atomic SQL)
        │   ├── <X>SqlPaths.java        # hằng số path file .sql (package-private)
        │   └── <X>RowMappers.java      # (tùy chọn) RowMapper<view>/RowMapper<aggregate>
        └── storage/         # (tùy chọn) adapter lưu file/đối tượng (S3)
```

> SQL native để ở `src/main/resources/sql/<module>/*.sql`, **không** viết inline trong Java.
> Layer nào không cần thì **bỏ luôn thư mục**. KHÔNG dùng package `infrastructure` — thay bằng `adapter/out`.

> **Tách admin/client chỉ ở REST adapter:** chia theo audience **chỉ** trong `adapter/in/rest/{admin,client}`. Tầng `application` và `domain` **dùng chung — KHÔNG nhân bản theo audience** (cả `Admin<X>Controller` lẫn `Client<X>Controller` cùng inject lại use case). Module chưa có endpoint cho audience nào thì bỏ luôn thư mục đó.

> **Khác bản gốc — đã loại JPA:** không còn `persistence/entity/` và `persistence/repository/`. Persistence model nếu cần để ở chính `persistence/` dưới dạng record/RowMapper, không phải `@Entity`.

---

## 3. Trách nhiệm & quy tắc đặt tên

| Layer | Trách nhiệm | Quy ước tên | Ví dụ (gym) |
|---|---|---|---|
| `domain` | Quy tắc nghiệp vụ thuần, không framework | `<X>`, `<X>Status` | `Member`, `MemberStatus` |
| `application/command` | Input ghi, **tự validate** trong `from(...)` | `<Verb><X>Command` | `CreateMemberCommand` |
| `application/query` | Tham số đọc/phân trang | `Search<X>Query` (paged), `List<X>Query` (non-paged) | `SearchMembersQuery` |
| `application/view` | Read model trả ra ngoài | `<X>Detail`, `<X>ListItem`, `<X>Summary`, `<X>Stats` | `MemberDetail` |
| `application/port/in` | Hợp đồng use case | `<Verb><X>UseCase` (verb §3.1) | `SearchMembersUseCase` |
| `application/port/out` | Hợp đồng SPI (DB, module khác) | `Read<X>Port`, `Write<X>Port` | `ReadMemberPort` / `WriteMemberPort` |
| `application/service` | Impl use case, điều phối port | `<X>CommandService`, `<X>QueryService` | `MemberQueryService` |
| `adapter/in/rest/admin` | Controller admin (`/api/v1/admin/**`) | `Admin<X>Controller` | `AdminMemberController` |
| `adapter/in/rest/client` | Controller khách (`/api/v1/client/**`) | `Client<X>Controller` | `ClientBookingController` |
| `adapter/in/rest/{admin,client}/request` | DTO request | `<Verb><X>Request` | `CreateMemberRequest` |
| `adapter/in/rest/{admin,client}/response` | DTO response + `fromDomain` | `<X>...Response` | `AdminMemberDetailResponse` |
| `adapter/out/persistence` | **Native SQL** (NamedParameterJdbcTemplate) | `<X>ReadAdapter`, `<X>WriteAdapter`, `<X>SqlPaths` | `MemberReadAdapter` |
| `api` | Cổng public cross-module | `<X>Directory`, `<X>Ref` | `MemberDirectory`, `MemberRef` |

**Quy ước chung:**
- Service & adapter để **package-private** (Spring vẫn quét được; ngăn module khác import nhầm nội bộ). Chỉ `port`, `view`, `command`, `query`, `api`, request/response để `public`.
- Read model luôn ở `application/view` — **không** để ở `domain` (domain chỉ giữ aggregate + enum + value object + factory thuần).
- Read/Write **đối xứng**: `Read<X>Port`↔`<X>ReadAdapter`↔`<X>QueryService`; `Write<X>Port`↔`<X>WriteAdapter`↔`<X>CommandService`. Một resource = **1 Read port gộp** + **1 Write port** (không tách Load/Page/Search/Stats). Port đặt theo **resource**, KHÔNG theo tên module. Guard đọc rẻ (`existsById`/by-id) thuộc `Read<X>Port` của resource đó.
- Map response bằng `static fromDomain(view)` ngay trên record Response — **không** tạo class `<X>ApiMapper` riêng.
- Command tự validate trong `from(...)`, ném `BusinessException.validation(...)` (→ 400). `from(...)` chỉ nhận **primitive/giá trị thô** — KHÔNG import Request DTO của adapter (dependency rule); controller trải `body.field()` vào `from(...)`. Dữ liệu con dùng nested record `<X>Input` ngay trong command. Helper **generic** dùng `shared/validation/Validations`; parse enum dùng `shared/validation/Enums`. Rule **riêng theo resource** gom vào `<Resource>CommandValidation` (package-private) trong `application/command`.

### 3.1. Verb chuẩn cho luồng đọc

| Verb | Nghĩa | Trả về | Ví dụ |
|---|---|---|---|
| `Get<X>` | 1 bản ghi theo id | `<X>Detail` | `GetMemberUseCase` |
| `Get<X>Stats` | thống kê tổng hợp | `<X>Stats` | `GetMemberStatsUseCase` |
| `List<X>` | collection **KHÔNG** phân trang (dropdown/all) | `List<...>` | `ListBranchesUseCase` |
| `Search<X>` | bảng **CÓ** phân trang + filter | `PageResponse<...>` | `SearchMembersUseCase` |

- KHÔNG dùng `Page<X>`/`Load<X>` cho luồng đọc.
- `List<X>` non-paged được phép trả thẳng **domain aggregate** khi không cần field join; cần read model phẳng/join thì dùng view.
- 1 resource có 2 luồng paged khác return type: bảng detail = `Search<X>`; picker tóm tắt = `Search<X>Summaries`.

---

## 4. Luồng một request (ngoài vào trong)

```
HTTP → Admin<X>Controller (adapter/in/rest/admin)
     → <Verb><X>UseCase (port/in)            ← interface
     → <X>Query/CommandService (service)     ← impl, điều phối
     → Read/Write<X>Port (port/out)          ← interface
     → <X>ReadAdapter/<X>WriteAdapter (adapter/out/persistence) → PostgreSQL (Native SQL)
return ← <X>Detail (view) → <X>Response (adapter/in/rest/admin/response) → JSON
```

Controller chỉ phụ thuộc `port/in` + DTO request/response. Service chỉ phụ thuộc `port/out` + view/command/query. Không tầng nào "nhảy cóc" xuống adapter.

---

## 5. Cross-module (module A cần dữ liệu module B)

- Module B expose **`api/<B>Directory`** (interface) + **`api/<B>Ref`** (record, gồm `id: long` + `code` + field tối thiểu).
- Module A import **chỉ** `com.gym.<B>.api.*`, không bao giờ import `<B>.domain`/`<B>.adapter`/`<B>.application`.
- Đây là cách hiện thực "logical reference" của [ADR-0011](../decisions/adr-0011-schema-per-module-no-cross-fk.md): không FK chéo schema; toàn vẹn kiểm ở application qua `Directory.existsById(...)`.
- Ví dụ: `booking` kiểm tra member tồn tại qua `member.api.MemberDirectory`; `kyc` resolve staff qua `staff.api.StaffDirectory`.

---

## 6. Checklist tạo module mới

1. Tạo `com/gym/<module>/` với cây ở §2 (chỉ giữ layer cần dùng).
2. Tạo migration Flyway `Vxxx__<module>.sql` — **schema riêng** cho module (đã có sẵn trong `db/migration`, xem `data-model/`).
3. Service & adapter để **package-private**.
4. Native SQL để file `resources/sql/<module>/*.sql`; adapter inject `SqlLoader` + `NamedParameterJdbcTemplate`.
5. Tái dùng tiện ích `shared/` (§8) — không tự viết lại.
6. `./mvnw -q test-compile` để chắc wiring + ArchitectureRulesTest pass.

---

## 7. Quy trình thêm 1 API mới (module có sẵn) — đi từ TRONG ra NGOÀI

**Xác định READ hay WRITE trước:**

| Loại | HTTP | Quy trình |
|---|---|---|
| **READ** | `GET` | `Query` + `Read<X>Port` + `QueryService(@Transactional(readOnly=true))`. KHÔNG Request/Command, KHÔNG reload. |
| **WRITE** | `POST/PATCH/PUT/DELETE` | `Request` + `Command(validate)` + `Write<X>Port` + `CommandService(@Transactional)`. Validate ở Command, **reload sau ghi**. |

### 7.1. READ (GET) — thứ tự
1. (FE contract) — hàm `GET` trong `admin-web/src/features/<module>/api/*`.
2. `application/query` — `Search<X>Query`/`List<X>Query` (`from(...)` chuẩn hóa bằng `QueryParams`+`PageParams`, `implements Paged` nếu phân trang). Get-by-id bỏ qua — truyền `long` thẳng.
3. `application/view` — `<X>Detail`/`<X>Summary`/`<X>ListItem`/`<X>Stats`.
4. `application/port/in` — `Get/Search/List/GetStats<X>UseCase` (`@FunctionalInterface`, `handle(...)`).
5. `application/port/out` *(nếu cần)* — thêm method vào `Read<X>Port` (1 resource = 1 Read port gộp).
6. `adapter/out/persistence` *(nếu cần)* — `<X>ReadAdapter`: native SQL từ file `.sql` qua `SqlLoader`, map bằng `RowMapper`, phân trang `PageResponse.ofPageIndex`.
7. `application/service` — `<X>QueryService implements ...UseCase`, `@Transactional(readOnly=true)`; get-by-id `.orElseThrow(BusinessException.notFound(...))`.
8. `adapter/in/rest/<audience>/response` — `<X>...Response` + `static fromDomain(view)`.
9. `adapter/in/rest/<audience>` — `@GetMapping`, map query → `Query.from(...)` (hoặc `long`) → `usecase.handle(...)` → `.map(Response::fromDomain)` → `ApiResponse.success(...)`.
10. Verify: `./mvnw -q test-compile` + smoke.

### 7.2. WRITE (POST/PATCH/PUT/DELETE) — thứ tự
1. (FE contract).
2. `adapter/in/rest/<audience>/request` *(nếu có body)* — `<Verb><X>Request` (record, kiểu thô; **không** validate ở đây).
3. `application/command` — `<Verb><X>Command` (+ `<Resource>CommandValidation` nếu có rule domain). `from(...)` nhận **primitive**, ném `BusinessException.validation(...)`. Generic dùng `Validations`, enum dùng `Enums`.
4. `application/port/in` — `<Verb><X>UseCase` (`handle(command)`).
5. `application/port/out` *(nếu cần)* — thêm method ghi vào `Write<X>Port`. Input ghi = **command + giá trị hệ thống/derived** (id, code sinh, timestamp, tên đã resolve) hoặc domain aggregate — **KHÔNG** truyền read view. Dữ liệu con derive gom vào write model `New<X>` nested trong port. Guard đọc rẻ lấy từ `Read<X>Port`.
6. `adapter/out/persistence` *(nếu cần)* — `<X>WriteAdapter`: INSERT/UPDATE bằng Native SQL; **atomic SQL** cho counter/quota/stock (`... WHERE qty >= :n`) theo `database-guideline.md`.
7. `application/service` — `<X>CommandService implements <Verb><X>UseCase`, `@Transactional`: load/guard → validate domain → save → **reload + return view**.
8. `adapter/in/rest/<audience>` — `@PostMapping/@PatchMapping/...`, trải `body.field()` vào `Command.from(...)` → `usecase.handle(...)` → `Response.fromDomain(...)` → `ApiResponse.success(...)`.
9. Verify: `./mvnw -q test-compile` + smoke.

### Quy tắc rút ra
- **READ ≠ WRITE**: GET không bao giờ có `Command`/`Request`/reload; QueryService `readOnly=true`. WRITE validate ở Command, reload sau ghi.
- **Validate ở command, không ở controller/request**.
- **Domain check trong service**, không trong adapter (vd vi phạm rule → `BusinessException.validation(...)` ở service, không để UPDATE chạy ra 0 row rồi đoán lỗi).
- **Input ghi KHÔNG phải read view**; dùng command + derived hoặc aggregate; dữ liệu con gom vào `New<X>`.
- **Tái dùng port out trước khi tạo mới**; ghi sang bảng khác thì thêm method `Write<X>Port` mới.
- **URL theo resource**, không theo entity con.

---

## 8. Tiện ích dùng chung (`com.gym.shared`)

> Đã có: `ApiResponse`, `ApiError`, `BusinessException`, `GlobalExceptionHandler`. Sẽ mở rộng theo bảng dưới (adapt sang Native SQL, không JPA).

| Tiện ích | Vị trí | Khi nào dùng |
|---|---|---|
| `SqlLoader` + `ClasspathSqlQueryLoader` | `shared/sql/` | Native SQL để file `resources/sql/<module>/*.sql`. Adapter inject `SqlLoader`, gọi `sql.load(<Module>SqlPaths.X)`. Mỗi module 1 class `<Module>SqlPaths`. |
| `Rows` | `shared/persistence/` | Map cột từ `ResultSet` trong `RowMapper`: `Rows.longValue(rs,"col")`, `string`, `dateTime`, `localDate`, `bigDecimal`, `bool`. (Thay `Tuples` của bản gốc — ta dùng JDBC `ResultSet`, không JPA `Tuple`.) |
| `PageResponse.ofPageIndex(items,total,pageIndex,size)` | `shared/api/` | Build kết quả phân trang từ pageIndex 0-based. |
| `PageParams.normalize(page,size,defaultSize,maxSize)` | `shared/api/` | Chuẩn hóa page/size trong `*Query.from(...)`. |
| `Paged` (interface) | `shared/api/` | `*Query` paged `implements Paged` → có sẵn `pageIndex()`. |
| `QueryParams` | `shared/api/` | `filterOrNull(v)` (null/blank/"all"→null) cho filter; `searchOrEmpty(v)` cho free-text. |
| `Validations` | `shared/validation/` | Helper generic cho `*Command.from(...)`: `requireText`, `requirePositive`, `optionalUuid`, `requirePhone`, `requireDate`... Sai → `BusinessException.validation` (400). |
| `Enums` | `shared/validation/` | `parseStrict(type,name,value)` (optional) / `requireStrict(...)` (bắt buộc); sai → 400 kèm danh sách giá trị hợp lệ. |
| `ApiResponse` / `BusinessException` / `ErrorCode` | `shared/api`, `shared/error` | Bọc response & ném lỗi. Bổ sung factory `BusinessException.validation(...)`, `.notFound(...)`, `.conflict(...)`. |

---

## 9. Rule được khóa bằng test

`src/test/java/com/gym/architecture/ArchitectureRulesTest` quét source (thuần JDK, **không ArchUnit** vì Java 26 — ASM của ArchUnit có thể chưa đọc class-file mới) và fail build nếu vi phạm:
- **R1** `application` ↛ `adapter` (lõi không import adapter).
- **R2** `domain` ↛ framework/lớp ngoài (Spring/JDBC/web).
- **R3** Cross-module chỉ qua `api` (không import `domain`/`application`/`adapter` của module khác).
- **R4** Write side (`Write*Port`/`*WriteAdapter`) không nhận read view (`*Detail`/`*ListItem`/`*Summary`).
- **R5 (ta thêm)** `adapter/out/persistence` không import `jakarta.persistence`/`org.springframework.data.jpa` (giữ ADR-0004: Native SQL, không JPA).

---

## 10. Khác biệt so với bản gốc (đã adapt cho gym-platform)

| Bản gốc | gym-platform | Lý do |
|---|---|---|
| JPA (`*JpaEntity`/`*JpaRepository`/`*JpaSpecifications`) cho write | **Native SQL** (`NamedParameterJdbcTemplate` + `RowMapper`) cho cả read/write | ADR-0004 |
| Cross-module ref bằng **UUID**/code | **`id: BIGINT` + `code`** | data-model PK = BIGINT identity + business code |
| `Tuples` (map JPA `Tuple`) | `Rows` (map JDBC `ResultSet`) | không JPA |
| `DomainException.validation/notFound` | `BusinessException.validation/notFound` | shared của ta |
| (R1–R4) | thêm **R5**: cấm JPA trong persistence | enforce ADR-0004 |

Giữ nguyên: cấu trúc thư mục, tách read/write, verb chuẩn, cross-module qua `api/`, package-private service/adapter, command tự validate, reload sau ghi, ArchitectureRulesTest source-scan.
