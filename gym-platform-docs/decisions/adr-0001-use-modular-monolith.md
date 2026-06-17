# ADR-0001: Use Modular Monolith First

## Status

Accepted

## Context

The GYM platform has many domains: member, KYC, membership, contract, payment, check-in, booking, PT, group class, private room, massage, inventory, pantry, equipment, CRM, and reporting.

The project may have many users in the future, but the early phase requires fast development, strong consistency, and clear domain discovery.

## Decision

Use a Spring Boot Modular Monolith for the first phase.

## Consequences

Positive:
- Faster development.
- Easier local setup.
- Easier transaction management for core flows.
- Lower operational complexity.
- Easier for AI coding assistant to understand the full system.

Negative:
- Requires discipline to avoid module coupling.
- One deployable unit initially.
- Need clear documentation and boundaries.

## Future Extraction Candidates

Potential future services:
- Notification.
- Reporting.
- File storage.
- Payment integration.
- Partner integration.

Core domains should remain together until there is a proven scaling or team-boundary reason to split.
