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
- `gym-platform-docs/vi/business/GYM_Business_Requirement_Document_v1.md`
- `gym-platform-docs/vi/architecture/architecture-overview.md`
- `gym-platform-docs/vi/architecture/backend-guideline.md`
- `gym-platform-docs/vi/architecture/database-guideline.md`
- `gym-platform-docs/vi/architecture/frontend-guideline.md`
- `gym-platform-docs/vi/modules/`
- `gym-platform-docs/vi/decisions/`

## Documentation Rule
Every new technical decision must update Markdown docs. Important architectural decisions should have an ADR.
