# Development Guideline

## Documentation Is Source of Truth
Business and technical rules must live in Markdown docs.

When a technical decision changes:
1. Update `CLAUDE.md` if it affects global behavior.
2. Update the relevant architecture/module docs.
3. Create or update an ADR if the decision is architectural.
4. Update dev notes when finishing a development session.

Do not leave important decisions only in chat.

## Before Coding
Claude Code or any developer should:
1. Read `CLAUDE.md`.
2. Read the relevant business/module docs.
3. Inspect current code.
4. Propose a short implementation plan.
5. Wait for approval if the task is broad or risky.

## Backend Rules
- Spring Boot only for backend unless explicitly changed.
- Modular Monolith first.
- SOLID principles.
- Clean/Hexagonal style.
- Native SQL persistence.
- PostgreSQL database.
- Do not introduce JPA repositories unless approved.
- Do not place business logic in controllers.
- Do not expose DB rows directly as API response.
- Do not directly query another module's tables from the wrong module without an approved query-service pattern.

## Frontend Rules
- React + TypeScript.
- Web application first.
- No mobile app initially.
- Type API models.
- Keep feature code grouped by domain.

## Git Discipline
- Commit docs with code when behavior changes.
- Use meaningful commit messages.
- Do not commit secrets.
- Keep `.env.example` updated.

## Migrations
- Use Flyway unless a later ADR changes it.
- New DB change means new migration.
- Never edit applied migrations.

## Testing Discipline
For each feature, include relevant tests:
- Unit tests for business rules.
- Integration tests for repository/use case where practical.
- API tests for important endpoints.
- Race-sensitive tests for booking/payment/quota/stock when practical.

## Done Definition
A task is done when:
- Business rule is implemented.
- Validation is implemented.
- Race conditions are considered.
- Tests are added or explicitly deferred with reason.
- Docs are updated if behavior or technical decisions changed.
