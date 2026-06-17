# ADR-0002: Use Spring Boot for Backend

## Status
Accepted

## Context
The project needs a backend suitable for a large multi-branch gym management platform with contract, payment, booking, check-in, inventory, staff, and reporting modules.

The owner is familiar with Java/Spring Boot and wants a maintainable backend foundation.

## Decision
Use Java + Spring Boot for backend development.

## Consequences
Positive:
- Strong ecosystem for web APIs, security, transaction management, testing, scheduling, and database access.
- Suitable for Modular Monolith.
- Good fit for enterprise-style business domains.

Trade-offs:
- Requires clear architecture discipline to avoid large messy service classes.
- Requires strict module boundaries.

## Rules
- Follow SOLID principles.
- Use Clean/Hexagonal style where practical.
- Do not put business logic in controllers.
- Application services own use case orchestration and transaction boundaries.
