# ADR-0012: Hexagonal Module Architecture Standard (Native SQL adaptation)

> English version. Vietnamese (canonical): [`../../vi/decisions/adr-0012-hexagonal-module-architecture.md`](../../vi/decisions/adr-0012-hexagonal-module-architecture.md).

## Status
Accepted

## Context
The owner has a battle-tested Hexagonal/Clean template (read/write split, in/out ports, cross-module via `api/`, an ArchitectureRulesTest). The original template uses JPA for writes and UUID keys — which conflicts with the gym-platform baseline (ADR-0004 Native SQL; data-model PK = BIGINT + business code). We need one enforceable module standard.

## Decision
Adopt the template as the **mandatory standard** for every `com.gym.<module>`, documented in `architecture/module-architecture.md`, with these adaptations:
- **Persistence = Native SQL** (`NamedParameterJdbcTemplate` + `RowMapper` + `.sql` files via `SqlLoader`); **NO JPA** (keeps ADR-0004). Drop `*JpaEntity`/`*JpaRepository`/`*JpaSpecifications`.
- **`adapter/out` replaces `infrastructure`**. Tree: `api/ · domain/ · application/{command,query,view,port/in,port/out,service} · adapter/{in/rest/{admin,client},in/cli,out/persistence,out/storage}`.
- **Cross-module via `api/<B>Directory` + `<B>Ref`** (`id: BIGINT` + `code`, not UUID) — realises the logical reference of ADR-0011.
- Services & adapters **package-private**; self-validating commands; reload-after-write; standard read verbs `Get/Search/List/GetStats`.
- **ArchitectureRulesTest** (plain JDK source-scan for Java 26) locks R1–R5 (R5: forbid JPA in persistence).

## Consequences
Positive: a consistent, reviewable standard; clear bounded contexts; build-enforced. Trade-offs: more files/classes per feature; needs a `shared/` toolkit (SqlLoader, Rows, PageResponse, Validations, Enums...) before the first module.

## Rules
- Every new module follows `module-architecture.md`; violating PRs are blocked by the ArchitectureRulesTest.
- Supplements ADR-0004 (does not replace it): reaffirms Native SQL, clarifying it applies to both read and write within the Hexagonal layout.
- Differences from the original are listed in `module-architecture.md` §10.
