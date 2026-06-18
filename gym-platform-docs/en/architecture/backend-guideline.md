# Backend Guideline

## Technical Baseline
- Java.
- Spring Boot.
- Modular Monolith.
- SOLID principles.
- Clean Architecture / Hexagonal Architecture style.
- PostgreSQL.
- Native SQL.
- Prefer `NamedParameterJdbcTemplate` for repository adapters at the beginning.
- Do not use JPA repositories for business queries unless explicitly approved by a later ADR.

## Package Layout Per Module
The mandatory standard is [`module-architecture.md`](module-architecture.md) (Hexagonal, adapted for Native SQL — ADR-0012). Summary:

```text
com/gym/<module>/
 ├── api/                 # public cross-module port (<X>Directory, <X>Ref)
 ├── domain/             # aggregate, enum, value object (NO framework)
 ├── application/        # command, query, view, port/in, port/out, service
 └── adapter/            # in/rest/{admin,client}, in/cli, out/persistence (Native SQL), out/storage
```

> **`adapter/out` replaces `infrastructure`** (no `infrastructure` package). Persistence uses `NamedParameterJdbcTemplate` + `RowMapper` + `.sql` files, **NO JPA**. Smaller modules drop unused layers; dependencies always point inward.

## Dependency Direction
Allowed:

```text
api -> application -> domain
application -> port
infrastructure -> port/domain mapping
```

Not allowed:
- Controller directly calls repository.
- Module A directly uses Module B repository.
- Domain object depends on Spring, HTTP, SQL, or framework annotations.
- Business rules placed inside SQL mapper only.

## Native SQL Repository Rule
Repository implementation should:
- Use parameter binding.
- Keep SQL readable.
- Prefer named parameters over string concatenation.
- Map DB rows to domain objects or persistence models explicitly.
- Keep business validation in application/domain layer.

Example using `NamedParameterJdbcTemplate` style:

```java
String sql = """
    UPDATE class_session
    SET booked_count = booked_count + 1
    WHERE id = :sessionId
      AND booked_count < capacity
""";

int updated = jdbc.update(sql, Map.of("sessionId", sessionId));
```

## Transaction Rule
Application service owns transaction boundary.

Transactions are mandatory for:
- Booking confirmation.
- Slot holding and release.
- Payment status update.
- Contract activation.
- Membership activation.
- QR check-in creation.
- Quota deduction.
- Class pass deduction.
- Inventory stock deduction.

## Race Condition Rule
Do not implement read-then-write for sensitive counters.

Prefer atomic SQL:

```sql
UPDATE inventory_stock
SET quantity = quantity - :qty
WHERE product_id = :productId
  AND branch_id = :branchId
  AND quantity >= :qty;
```

Check affected rows:
- `1` means success.
- `0` means not enough stock or invalid state.

## Error Handling
Use clear business exceptions.

Examples:
- `MEMBER_NOT_FOUND`
- `PACKAGE_EXPIRED`
- `TRIAL_DAILY_CHECKIN_LIMIT_REACHED`
- `BOOKING_SLOT_NOT_AVAILABLE`
- `PAYMENT_ALREADY_PROCESSED`
- `INSUFFICIENT_CLASS_PASS_BALANCE`
- `PRIVATE_ROOM_QUOTA_EXCEEDED`
- `OUT_OF_STOCK`

## Testing
Every sensitive use case must have tests for:
- Success path.
- Invalid input.
- Permission denied.
- Business rule violation.
- Duplicate request/idempotency.
- Race-condition sensitive path where possible.
