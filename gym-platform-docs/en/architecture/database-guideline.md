# Database Guideline

## Database Choice
PostgreSQL is the main database.

## Persistence Style
Use Native SQL for business persistence.

Do not use JPA repositories for business queries unless explicitly approved by a later ADR.

Preferred initial implementation:
- Spring `NamedParameterJdbcTemplate`.
- Explicit SQL files or SQL strings where suitable.
- Explicit row mappers.

## Migration
Use Flyway unless changed by a later ADR.

Rules:
- Every schema change must be done through migration.
- Do not manually edit database schema outside migration.
- Do not modify old migration files after they have been applied.
- Create new migration files for changes.

Recommended naming:

```text
V001__init_schema.sql
V002__create_member_tables.sql
V003__create_kyc_tables.sql
```

## Schema Design
**Schema-per-module (ADR-0011):** each module owns its own PostgreSQL schema (`identity`, `branch`, `member`, `booking`, …). Foreign keys are allowed ONLY within the same schema; cross-module references are logical ID columns (no DB FK), enforced in the application layer. This keeps modules extraction-ready for future microservices. See `data-model/module-schemas.md`. Native SQL must be schema-qualified.

Acceptable table prefix style:

```text
member_profile
kyc_verification
membership_package
booking_booking
booking_resource_slot
payment_transaction
inventory_stock
```

## Required Constraints
Use database constraints to enforce critical rules.

Examples:
- Unique CCCD for approved KYC.
- Trial usage once per CCCD.
- Unique payment transaction id.
- Unique class booking per member/session.
- Unique QR nonce usage.
- Non-negative stock quantity.
- Non-negative quota balance.

## Race Condition Patterns
Use atomic updates for counters and balances.

### Class capacity

```sql
UPDATE class_session
SET booked_count = booked_count + 1
WHERE id = :sessionId
  AND booked_count < capacity;
```

### Stock deduction

```sql
UPDATE inventory_stock
SET quantity = quantity - :quantity
WHERE branch_id = :branchId
  AND product_id = :productId
  AND quantity >= :quantity;
```

### Trial daily check-in
Use unique key:

```text
(member_id, checkin_date, checkin_type)
```

or conditional logic with transaction.

### Payment callback
Use idempotency key and unique provider transaction id.

```text
UNIQUE(provider, provider_transaction_id)
```

## Indexing Rule
Add indexes for frequent filters:
- `member_id`
- `branch_id`
- `status`
- `created_at`
- `start_time`, `end_time`
- `booking_type`
- `payment_status`
- `contract_code`
- `phone_number`
- `identity_number_hash` if storing hashed CCCD search key.

## Sensitive Data
CCCD and personal documents are sensitive.

Guidelines:
- Do not store raw document images in DB.
- Store file path/object key only — binaries live in S3-compatible Object Storage (ADR-0010).
- Prefer short-lived pre-signed URLs for sensitive objects (CCCD, student card).
- Consider encryption/tokenization for identity number.
- Limit access by RBAC.
- Audit all reads/updates for sensitive data when practical.
