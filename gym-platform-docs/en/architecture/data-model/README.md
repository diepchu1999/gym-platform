# Data Model — Overview & Conventions

> English version. Vietnamese (canonical): [`../../../vi/architecture/data-model/README.md`](../../../vi/architecture/data-model/README.md).

Database design for gym-platform, following `business/*` and `architecture/database-guideline.md`.

> Process: **design here first → owner approves → then write the Flyway migration**.
> Applied migrations must NOT be edited (create a new migration). See `development-guideline.md`.
>
> 🧩 **Schema-per-module (ADR-0011):** one PostgreSQL schema per module, **no cross-module FK** (cross references are logical IDs). Schema map + logical reference inventory: [`module-schemas.md`](module-schemas.md). In the phase files, any "FK" to a table in another module is a logical reference (no DB FK).

## Common conventions (all tables)

| Topic | Convention |
|---|---|
| Naming | `snake_case`, module prefix: `identity_*`, `rbac_*`, `branch_*`, `staff_*`, `member_*`, `kyc_*`, `contract`, `payment_*`, `booking_*`, `checkin_*`, `inventory_*`, `audit_*`... |
| Primary key | `BIGINT GENERATED ALWAYS AS IDENTITY`. Do **not** expose the PK in the API — use the `*_code` (business code) externally. |
| Business code | `code` / `*_code` `VARCHAR`, `UNIQUE`, human-readable (e.g. `BR-Q1`, `STF-000123`). Used for URLs/external refs. |
| Timestamp | `timestamptz` (store UTC). `created_at`/`updated_at` `NOT NULL DEFAULT now()`. `updated_at` auto via trigger. |
| Money | `numeric(14,2)` + `currency VARCHAR(3) DEFAULT 'VND'`. |
| Status / enum | `VARCHAR` + `CHECK (...)` matching the values in `business/status-flow.md`. No PostgreSQL `ENUM` (hard to migrate). |
| Boolean flags | `BOOLEAN NOT NULL DEFAULT ...`. |
| Soft delete | No global soft delete. Use a `status` column. Sensitive actions go to `audit_log` (P8). |
| Schema | **One schema per module** (microservices-ready) — e.g. `member.member_profile`, `booking.booking`. See [`module-schemas.md`](module-schemas.md) + ADR-0011. `flyway_schema_history` lives in `public`. |
| FK | **Only FK within the same schema/module.** Cross-module FK is FORBIDDEN → use a logical ID column (BIGINT, indexed), integrity in the app layer (ADR-0011, [`module-schemas.md`](module-schemas.md)). Intra FK: `ON DELETE RESTRICT` default; `CASCADE` for pure dependent tables. |
| Module boundary | Each module owns its tables. Other modules must NOT query them directly — go through application/query services (see `modular-monolith.md`). |

## Race-condition conventions (mandatory)

| Mechanism | Used for |
|---|---|
| `UNIQUE` constraint | trial CCCD (1 per CCCD), QR nonce, provider transaction id, class booking per member-session, business code |
| `CHECK (col >= 0)` | stock quantity, quota balance |
| Atomic update (`... WHERE col >= :n`) | deduct stock, deduct quota, deduct class pass, increment `booked_count < capacity` |
| `EXCLUDE USING gist` (needs `btree_gist`) | prevent overlapping time slots for the same resource (PT, private room, massage, class room) |
| `version BIGINT` column | optimistic lock for sensitive updates |
| Idempotency key | payment callback |

## P0 — Baseline migration (`V001`)

`V001__init_baseline.sql` contains:
1. Create one schema per module (identity, branch, …, messaging).
2. Extensions: `pgcrypto` (CCCD hashing, `gen_random_uuid()`), `btree_gist` (EXCLUDE for booking overlap).
3. Shared `updated_at` trigger helpers (`public.set_updated_at()` + `public.apply_updated_at_triggers()` — each migration calls the latter once).

## Phase roadmap (dependency order)

| Phase | Content | Design file |
|---|---|---|
| P0 | Baseline: schemas + extensions + trigger | (this README) |
| P1 | Identity, RBAC, Branch, Staff | [`p1-identity-org.md`](p1-identity-org.md) |
| P2 | Member, KYC, Student verify, Trial usage | [`p2-member-kyc.md`](p2-member-kyc.md) |
| P3 | Package, Membership, Contract, Order, Payment, Installment | [`p3-package-contract-payment.md`](p3-package-contract-payment.md) |
| P4 | Check-in (token, log) | [`p4-checkin.md`](p4-checkin.md) |
| P5 | Booking core (booking, resource slot, hold, event) | [`p5-booking-core.md`](p5-booking-core.md) |
| P6 | Group class / PT / Private room / Massage (+ quota) | [`p6-booking-verticals.md`](p6-booking-verticals.md) |
| P7 | Inventory / Pantry / Equipment-Maintenance | [`p7-inventory-pantry-equipment.md`](p7-inventory-pantry-equipment.md) |
| P8 | CRM / Rating / Promotion / Notification / Report / Audit | [`p8-crm-rating-promotion-notification-audit.md`](p8-crm-rating-promotion-notification-audit.md) |
| P9 | Messaging: Transactional Outbox + consumer idempotency | [`p9-messaging-outbox.md`](p9-messaging-outbox.md) |

## Phase → migration file mapping

- P0: `V001` baseline (schemas + extensions + trigger)
- P1: `V002` identity_rbac · `V003` branch · `V004` staff · `V005` seed_rbac
- P2: `V006` member · `V007` kyc
- P3: `V008` package_plan · `V009` contract_membership · `V010` order_payment · `V011` installment
- P4: `V012` checkin
- P5: `V013` booking_core (+ EXCLUDE)
- P6: `V014` group_class · `V015` pt · `V016` private_room · `V017` massage
- P7: `V018` product_inventory · `V019` purchase_transfer_adjust · `V020` equipment_maintenance
- P8: `V021` crm · `V022` rating_promotion · `V023` notification · `V024` audit
- P9: `V025` outbox

## Where adopted infrastructure touches data

See [`../solution-architecture.md`](../solution-architecture.md) + ADR-0006…0010.

- **Keycloak (ADR-0006)**: `identity_user_account` maps to `keycloak_user_id` (no password). Branch-scoped RBAC (`rbac_*`, `staff_branch_assignment`) stays in the DB.
- **Redis (ADR-0009)**: NOT a table. Handles ephemeral state (QR token TTL, nonce, dup-scan lock, rate limit). **Every correctness-critical invariant must still have a PostgreSQL constraint/atomic update.**
- **Object Storage (ADR-0010)**: image/document columns (`kyc_request.front_image_url`, contract PDF, invoice, media) store only the **object key/URL**, never bytes.
- **Outbox (ADR-0007)**: `outbox_event` table written in the same business transaction; Kafka plugs in later.
