# Modular Monolith Guideline

## Decision
The project starts as a Modular Monolith.

## Why
- Core business modules are strongly connected.
- Payment, contract, membership, booking, and check-in need transactional consistency.
- A clean monolith is faster to build, easier to deploy, and easier to reason about in early phases.
- Microservices can be extracted later only when there is real operational need.

## Module Boundary Rule
Each module owns:
- Its domain rules.
- Its application services.
- Its repository ports.
- Its Native SQL repository implementations.
- Its database tables or table group.

A module must not directly use another module's repository.

## Allowed Communication
- Application service interface.
- Domain event.
- Query service for read-only cross-module views.
- Shared value objects from `shared`.

## Not Allowed
- Controller -> repository.
- Module A repository querying Module B tables for command logic.
- Shared mega-service that contains all business logic.
- Business rule hidden inside frontend only.

## Extraction Later
Possible future service extraction:
- Notification.
- Reporting.
- File/object storage.
- Payment provider integration.
- Partner integration.

Keep core modules together longer:
- Member.
- KYC.
- Membership.
- Contract.
- Payment core.
- Booking.
- Check-in.
