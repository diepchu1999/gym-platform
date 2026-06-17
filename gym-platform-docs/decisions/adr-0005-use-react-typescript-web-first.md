# ADR-0005: Use React + TypeScript Web First

## Status
Accepted

## Context
The initial product needs web applications for admin, staff operation, and possibly member self-service. Mobile app is not required in the first technical phase.

## Decision
Use React + TypeScript for frontend web application.

## Consequences
Positive:
- Strong type safety with TypeScript.
- Large ecosystem.
- Suitable for admin dashboards and operational screens.
- Easier to build quickly with component-based architecture.

Trade-offs:
- Requires frontend architecture discipline as modules grow.
- Mobile app will require a separate decision later.

## Rules
- Use TypeScript for main source files.
- Keep API models typed.
- Group features by domain.
- Do not introduce mobile app scope unless explicitly requested.
