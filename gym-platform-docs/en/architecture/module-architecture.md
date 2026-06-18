# Module Architecture (Hexagonal / Clean Architecture)

> English version. Vietnamese (canonical): [`../../vi/architecture/module-architecture.md`](../../vi/architecture/module-architecture.md).
>
> The **mandatory standard** for every backend module under `com.gym.<module>`.
> Adapted from a battle-tested Hexagonal template, adjusted to the gym-platform baseline:
> **Native SQL (NamedParameterJdbcTemplate), NO JPA** (ADR-0004) · **PK BIGINT + business code** (not UUID) · **schema-per-module, no cross-FK** (ADR-0011).

---

## 1. Core principles

- **Modular monolith**: each module is a bounded context with its own **Postgres schema** (`member`, `booking`, `payment`, ...). **No cross-schema FK** — cross-module references are **values** (`id: BIGINT` + `code: String`), not UUIDs. (ADR-0011)
- **Hexagonal**: `domain` + `application` (business core) do **not** depend on the framework. Everything "dirty" (web, JDBC, files, other modules) lives in `adapter`.
- **Dependency rule** — dependencies point inward only:
  ```
  adapter ──> application ──> domain
  adapter ──> application.port (in/out)
  ```
  `domain` imports nothing from `application`/`adapter`. `application` does not import `adapter`. Only adapters may import Spring/JDBC and another module's `api`.
- **Persistence = Native SQL**: adapters use `NamedParameterJdbcTemplate` + `RowMapper` + `.sql` files (via `SqlLoader`). **NO** `*JpaEntity`/`*JpaRepository`/`*JpaSpecifications` (differs from the original — we follow ADR-0004).

---

## 2. Standard directory tree

```
com/gym/<module>/
├── api/                     # (optional) PUBLIC port for other modules
│   ├── <X>Directory.java        # cross-module lookup/check interface
│   └── <X>Ref.java              # shared cross-module DTO (record): id(long) + code + minimal fields
├── domain/                  # pure core: aggregate + enum + value object (NO framework)
│   ├── <X>.java
│   ├── <X>Status.java
│   └── <X>StatusAction.java     # action enum (if a state machine)
├── application/
│   ├── command/             # validated WRITE input:  <Verb><X>Command (+ <Resource>CommandValidation)
│   ├── query/               # READ input + sorting:   Search<X>Query, List<X>Query
│   ├── view/                # read models OUT:         <X>Detail, <X>ListItem, <X>Summary, <X>Stats
│   ├── port/in/             # use cases (inbound):     <Verb><X>UseCase
│   ├── port/out/            # SPI (outbound):          Read<X>Port, Write<X>Port
│   └── service/             # use-case impls:          <X>CommandService, <X>QueryService
└── adapter/
    ├── in/rest/             # REST split by audience (admin vs client)
    │   ├── admin/           # /api/v1/admin/**  → Admin<X>Controller
    │   │   ├── request/     # <Verb><X>Request
    │   │   └── response/    # <X>...Response (+ static fromDomain)
    │   └── client/          # /api/v1/client/** → Client<X>Controller
    │       ├── request/
    │       └── response/
    ├── in/cli/              # (optional) ApplicationRunner for import/seed
    └── out/
        ├── persistence/     # Native SQL adapter (NO JPA)
        │   ├── <X>ReadAdapter.java     # implements Read<X>Port (NamedParameterJdbcTemplate + RowMapper)
        │   ├── <X>WriteAdapter.java    # implements Write<X>Port (INSERT/UPDATE/atomic SQL)
        │   ├── <X>SqlPaths.java        # .sql path constants (package-private)
        │   └── <X>RowMappers.java      # (optional) RowMapper<view>/RowMapper<aggregate>
        └── storage/         # (optional) file/object storage adapter (S3)
```

> Native SQL lives in `src/main/resources/sql/<module>/*.sql`, **not** inline in Java.
> Drop unused layer folders entirely. Do NOT use an `infrastructure` package — use `adapter/out`.

> **Admin/client split only in the REST adapter:** split by audience **only** in `adapter/in/rest/{admin,client}`. `application` and `domain` are **shared — NOT duplicated per audience** (both `Admin<X>Controller` and `Client<X>Controller` reuse the same use cases). Drop the folder for an audience that has no endpoints yet.

> **Differs from the original — JPA removed:** no `persistence/entity/` or `persistence/repository/`. A persistence model, if needed, stays in `persistence/` as a record/RowMapper, not an `@Entity`.

---

## 3. Responsibilities & naming

| Layer | Responsibility | Naming | Example (gym) |
|---|---|---|---|
| `domain` | Pure business rules, no framework | `<X>`, `<X>Status` | `Member`, `MemberStatus` |
| `application/command` | Write input, **self-validating** in `from(...)` | `<Verb><X>Command` | `CreateMemberCommand` |
| `application/query` | Read/paging params | `Search<X>Query` (paged), `List<X>Query` (non-paged) | `SearchMembersQuery` |
| `application/view` | Outbound read models | `<X>Detail`, `<X>ListItem`, `<X>Summary`, `<X>Stats` | `MemberDetail` |
| `application/port/in` | Use-case contracts | `<Verb><X>UseCase` (verbs §3.1) | `SearchMembersUseCase` |
| `application/port/out` | SPI contracts (DB, other modules) | `Read<X>Port`, `Write<X>Port` | `ReadMemberPort` / `WriteMemberPort` |
| `application/service` | Use-case impls, orchestrate ports | `<X>CommandService`, `<X>QueryService` | `MemberQueryService` |
| `adapter/in/rest/admin` | Admin controller (`/api/v1/admin/**`) | `Admin<X>Controller` | `AdminMemberController` |
| `adapter/in/rest/client` | Client controller (`/api/v1/client/**`) | `Client<X>Controller` | `ClientBookingController` |
| `adapter/in/rest/{admin,client}/request` | Request DTO | `<Verb><X>Request` | `CreateMemberRequest` |
| `adapter/in/rest/{admin,client}/response` | Response DTO + `fromDomain` | `<X>...Response` | `AdminMemberDetailResponse` |
| `adapter/out/persistence` | **Native SQL** (NamedParameterJdbcTemplate) | `<X>ReadAdapter`, `<X>WriteAdapter`, `<X>SqlPaths` | `MemberReadAdapter` |
| `api` | Public cross-module port | `<X>Directory`, `<X>Ref` | `MemberDirectory`, `MemberRef` |

**General conventions:**
- Services & adapters are **package-private** (Spring still scans them; prevents other modules importing internals). Only `port`, `view`, `command`, `query`, `api`, request/response are `public`.
- Read models always live in `application/view` — never in `domain` (domain keeps aggregate + enum + value object + pure factories).
- Read/Write are **symmetric**: `Read<X>Port`↔`<X>ReadAdapter`↔`<X>QueryService`; `Write<X>Port`↔`<X>WriteAdapter`↔`<X>CommandService`. One resource = **one combined Read port** + **one Write port**. Ports are named per **resource**, not per module. Cheap read guards (`existsById`/by-id) belong to that resource's `Read<X>Port`.
- Map responses with `static fromDomain(view)` on the Response record — no separate `<X>ApiMapper`.
- Commands self-validate in `from(...)`, throwing `DomainException.validation(...)` (→ 400). `from(...)` takes **primitives/raw values** only — it does NOT import adapter Request DTOs (dependency rule); the controller spreads `body.field()` into `from(...)`. Nested data uses a nested `<X>Input` record inside the command. Generic helpers use `shared/validation/Validations`; enum parsing uses `shared/validation/Enums`. Resource-specific rules go into `<Resource>CommandValidation` (package-private) in `application/command`.

### 3.1. Standard read verbs

| Verb | Meaning | Returns | Example |
|---|---|---|---|
| `Get<X>` | one record by id | `<X>Detail` | `GetMemberUseCase` |
| `Get<X>Stats` | aggregate stats | `<X>Stats` | `GetMemberStatsUseCase` |
| `List<X>` | **non-paged** collection (dropdown/all) | `List<...>` | `ListBranchesUseCase` |
| `Search<X>` | **paged** table + filter | `PageResponse<...>` | `SearchMembersUseCase` |

- Do NOT use `Page<X>`/`Load<X>` for reads.
- `List<X>` non-paged may return the **domain aggregate** directly when no join fields are needed; use a view for flat/joined read models.
- One resource with two paged flows of different return types: detail table = `Search<X>`; summary picker = `Search<X>Summaries`.

---

## 4. One request flow (outside-in)

```
HTTP → Admin<X>Controller (adapter/in/rest/admin)
     → <Verb><X>UseCase (port/in)            ← interface
     → <X>Query/CommandService (service)     ← impl, orchestration
     → Read/Write<X>Port (port/out)          ← interface
     → <X>ReadAdapter/<X>WriteAdapter (adapter/out/persistence) → PostgreSQL (Native SQL)
return ← <X>Detail (view) → <X>Response (adapter/in/rest/admin/response) → JSON
```

The controller depends only on `port/in` + request/response DTOs. The service depends only on `port/out` + view/command/query. No layer "jumps" down to an adapter.

---

## 5. Cross-module (module A needs module B's data)

- Module B exposes **`api/<B>Directory`** (interface) + **`api/<B>Ref`** (record: `id: long` + `code` + minimal fields).
- Module A imports **only** `com.gym.<B>.api.*`, never `<B>.domain`/`<B>.adapter`/`<B>.application`.
- This realises the "logical reference" of [ADR-0011](../decisions/adr-0011-schema-per-module-no-cross-fk.md): no cross-schema FK; integrity checked in the app via `Directory.existsById(...)`.
- Example: `booking` checks member existence via `member.api.MemberDirectory`; `kyc` resolves staff via `staff.api.StaffDirectory`.

---

## 6. New-module checklist

1. Create `com/gym/<module>/` with the §2 tree (keep only needed layers).
2. Create the Flyway migration `Vxxx__<module>.sql` — its **own schema** (already present in `db/migration`, see `data-model/`).
3. Keep services & adapters **package-private**.
4. Native SQL in `resources/sql/<module>/*.sql`; adapters inject `SqlLoader` + `NamedParameterJdbcTemplate`.
5. Reuse the `shared/` utilities (§8) — do not re-implement.
6. `./mvnw -q test-compile` to confirm wiring passes. Once `ArchitectureRulesTest` is added, it must pass too.

---

## 7. Adding a new API to an existing module — inside-out

**Decide READ vs WRITE first:**

| Type | HTTP | Process |
|---|---|---|
| **READ** | `GET` | `Query` + `Read<X>Port` + `QueryService(@Transactional(readOnly=true))`. No Request/Command, no reload. |
| **WRITE** | `POST/PATCH/PUT/DELETE` | `Request` + `Command(validate)` + `Write<X>Port` + `CommandService(@Transactional)`. Validate in Command, **reload after write**. |

### 7.1. READ (GET) — order
1. (FE contract) — `GET` function in `admin-web/src/features/<module>/api/*`.
2. `application/query` — `Search<X>Query`/`List<X>Query` (`from(...)` normalised via `QueryParams`+`PageParams`, `implements Paged` if paged). Get-by-id: skip — pass a `long`.
3. `application/view` — `<X>Detail`/`<X>Summary`/`<X>ListItem`/`<X>Stats`.
4. `application/port/in` — `Get/Search/List/GetStats<X>UseCase` (`@FunctionalInterface`, `handle(...)`).
5. `application/port/out` *(if needed)* — add a method to `Read<X>Port` (1 resource = 1 combined Read port).
6. `adapter/out/persistence` *(if needed)* — `<X>ReadAdapter`: native SQL from `.sql` via `SqlLoader`, map with `RowMapper`, paginate with `PageResponse.ofPageIndex`.
7. `application/service` — `<X>QueryService implements ...UseCase`, `@Transactional(readOnly=true)`; get-by-id `.orElseThrow(DomainException.notFound(...))`.
8. `adapter/in/rest/<audience>/response` — `<X>...Response` + `static fromDomain(view)`.
9. `adapter/in/rest/<audience>` — `@GetMapping`, map query → `Query.from(...)` (or `long`) → `usecase.handle(...)` → `.map(Response::fromDomain)` → `ApiResponse.ok(...)`.
10. Verify: `./mvnw -q test-compile` + smoke.

### 7.2. WRITE (POST/PATCH/PUT/DELETE) — order
1. (FE contract).
2. `adapter/in/rest/<audience>/request` *(if body)* — `<Verb><X>Request` (record, raw types; **no** validation here).
3. `application/command` — `<Verb><X>Command` (+ `<Resource>CommandValidation` if domain rules). `from(...)` takes **primitives**, throws `DomainException.validation(...)`. Generic via `Validations`, enums via `Enums`.
4. `application/port/in` — `<Verb><X>UseCase` (`handle(command)`).
5. `application/port/out` *(if needed)* — add a write method to `Write<X>Port`. Write input = **command + system/derived values** (id, generated code, timestamp, resolved name) or the domain aggregate — **never** a read view. Derived nested data goes into a `New<X>` write model nested in the port. Cheap read guards come from `Read<X>Port`.
6. `adapter/out/persistence` *(if needed)* — `<X>WriteAdapter`: INSERT/UPDATE via Native SQL; **atomic SQL** for counters/quota/stock (`... WHERE qty >= :n`) per `database-guideline.md`.
7. `application/service` — `<X>CommandService implements <Verb><X>UseCase`, `@Transactional`: load/guard → validate domain → save → **reload + return view**.
8. `adapter/in/rest/<audience>` — `@PostMapping/@PatchMapping/...`, spread `body.field()` into `Command.from(...)` → `usecase.handle(...)` → `Response.fromDomain(...)` → `ApiResponse.ok(...)`.
9. Verify: `./mvnw -q test-compile` + smoke.

### Distilled rules
- **READ ≠ WRITE**: GET never has `Command`/`Request`/reload; QueryService is `readOnly=true`. WRITE validates in Command, reloads after write.
- **Validate in the command, not the controller/request.**
- **Domain checks in the service**, not the adapter.
- **Write input is not a read view**; use command + derived, or the aggregate; nested data in `New<X>`.
- **Reuse the out-port before creating a new one**; writing to a different table → add a new `Write<X>Port` method.
- **URL by resource**, not by sub-entity.

---

## 8. Shared utilities (`com.gym.shared`)

> Already present: `ApiResponse`, `ListResponse`, `DomainException`, `RestExceptionHandler`. Extended per the table below (adapted to Native SQL, no JPA).

| Utility | Location | When to use |
|---|---|---|
| `SqlLoader` + `ClasspathSqlQueryLoader` | `shared/sql/` | Native SQL in `resources/sql/<module>/*.sql`. Adapter injects `SqlLoader`, calls `sql.load(<Module>SqlPaths.X)`. One `<Module>SqlPaths` per module. |
| `Rows` | `shared/persistence/` | Map columns from a `ResultSet` in a `RowMapper`: `Rows.longValue(rs,"col")`, `string`, `dateTime`, `localDate`, `bigDecimal`, `bool`. (Replaces the original `Tuples` — we use JDBC `ResultSet`, not a JPA `Tuple`.) |
| `PageResponse.ofPageIndex(items,total,pageIndex,size)` | `shared/api/` | Build a paged result from 0-based pageIndex. |
| `PageParams.normalize(page,size,defaultSize,maxSize)` | `shared/api/` | Normalise page/size in `*Query.from(...)`. |
| `Paged` (interface) | `shared/api/` | Paged `*Query` `implements Paged` → free `pageIndex()`. |
| `QueryParams` | `shared/api/` | `filterOrNull(v)` (null/blank/"all"→null) for filters; `searchOrEmpty(v)` for free-text. |
| `Validations` | `shared/validation/` | Generic helpers for `*Command.from(...)`: `requireText`, `requirePositive`, `optionalUuid`, `requirePhone`, `requireDate`... Invalid → `DomainException.validation` (400). |
| `Enums` | `shared/validation/` | `parseStrict(type,name,value)` (optional) / `requireStrict(...)` (mandatory); invalid → 400 with the list of valid values. |
| `ApiResponse` / `DomainException` / `ErrorCode` | `shared/api`, `shared/error` | Wrap responses & throw business errors. Add factories `DomainException.validation(...)`, `.notFound(...)`, `.conflict(...)`. |

---

## 9. Rules to lock by a test

The current source does not yet include `src/test/java/com/gym/architecture/ArchitectureRulesTest`. Before adding real business modules, add this test to scan source (plain JDK, **no ArchUnit** because of Java 26 — ArchUnit's ASM may not read new class files) and fail the build on violations:
- **R1** `application` ↛ `adapter` (core does not import adapters).
- **R2** `domain` ↛ framework/outer (Spring/JDBC/web).
- **R3** Cross-module only via `api` (no importing another module's `domain`/`application`/`adapter`).
- **R4** Write side (`Write*Port`/`*WriteAdapter`) does not accept read views (`*Detail`/`*ListItem`/`*Summary`).
- **R5 (added)** `adapter/out/persistence` does not import `jakarta.persistence`/`org.springframework.data.jpa` (keeps ADR-0004: Native SQL, no JPA).

---

## 10. Differences from the original (adapted for gym-platform)

| Original | gym-platform | Reason |
|---|---|---|
| JPA (`*JpaEntity`/`*JpaRepository`/`*JpaSpecifications`) for writes | **Native SQL** (`NamedParameterJdbcTemplate` + `RowMapper`) for read/write | ADR-0004 |
| Cross-module ref by **UUID**/code | **`id: BIGINT` + `code`** | data-model PK = BIGINT identity + business code |
| `Tuples` (JPA `Tuple`) | `Rows` (JDBC `ResultSet`) | no JPA |
| `DomainException` / `RestExceptionHandler` | **kept original name** | already matches our shared layer |
| (R1–R4) | added **R5**: forbid JPA in persistence | enforce ADR-0004 |

Kept as-is: directory structure, read/write split, standard verbs, cross-module via `api/`, package-private services/adapters, self-validating commands, reload-after-write, and adding source-scan ArchitectureRulesTest before business modules expand.
