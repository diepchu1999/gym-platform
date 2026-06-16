# GYM System

Multi-branch gym management platform for Ho Chi Minh City.

## Technical Baseline
Backend:
- Java + Spring Boot.
- Modular Monolith.
- SOLID principles.
- Clean/Hexagonal Architecture style.
- PostgreSQL.
- Native SQL instead of JPA repositories.
- Prefer NamedParameterJdbcTemplate initially.
- Flyway migrations unless changed later.

Frontend:
- React + TypeScript.
- Web application first.
- No mobile app in initial phase.

## Claude Code Usage
Before coding, ask Claude Code:

```text
Read CLAUDE.md first.
Then read the module docs relevant to my task.
Inspect current code before editing and propose a plan before coding.
```

## Important Docs
- `CLAUDE.md`
- `docs/business/GYM_Business_Requirement_Document_v1.md`
- `docs/architecture/architecture-overview.md`
- `docs/architecture/backend-guideline.md`
- `docs/architecture/database-guideline.md`
- `docs/architecture/frontend-guideline.md`
- `docs/modules/`
- `docs/decisions/`

## Documentation Rule
Every new technical decision must update Markdown docs. Important architectural decisions should have an ADR.
