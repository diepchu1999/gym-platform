# Module: QR Check-in

## Purpose

Allow members to enter gym branches using temporary QR code.

## Actors

- Member
- Receptionist
- Branch Manager
- Super Admin

## Business Rules

- Main paid packages have unlimited check-in.
- Trial can check in only once per day.
- QR token expires after 30-60 seconds.
- QR token should be one-time-use.
- Prevent duplicate scan within 3-5 minutes.
- All main packages are valid across all branches.
- Trial has no time-window restriction.

## Main Flow

1. Member opens app.
2. App requests temporary QR token.
3. Member scans QR at branch.
4. Backend validates token, member, package, and branch.
5. Backend creates check-in record.
6. Gate/reception allows access.

## Suggested Data Fields

Checkin Token:
- id
- member_id
- nonce
- expires_at
- used_at
- status

Checkin Log:
- id
- member_id
- branch_id
- package_id
- checkin_time
- result
- denied_reason
- device_id

## API Suggestions

- `POST /checkins/qr-tokens`
- `POST /checkins/scan`
- `GET /members/{id}/checkins`
- `GET /branches/{id}/checkins`

## Denied Reasons

- QR_EXPIRED
- QR_ALREADY_USED
- DUPLICATE_SCAN
- MEMBER_BLOCKED
- PACKAGE_EXPIRED
- TRIAL_DAILY_LIMIT_REACHED
- KYC_REQUIRED
- BRANCH_UNAVAILABLE

## Race Conditions

- Same QR scanned multiple times.
- Trial member scans multiple branches on same day.
- Multiple gate devices process same token.

Use transaction and unique/atomic update on token usage.

## Tests

- Paid member check-in success.
- Trial member first check-in success.
- Trial second check-in same day denied.
- Expired QR denied.
- Used QR denied.
- Duplicate scan denied.

## Technical Notes (implementation)

> Implementation detail only — does NOT change the business rules above. See ADR-0009 (Redis) + `architecture/solution-architecture.md`.

- **Redis (ephemeral / fast gate)**: QR token TTL (30–60s), one-time nonce, duplicate-scan short lock (3–5 min window), and rate limiting.
- **PostgreSQL (durable / source of truth)**: final QR nonce consumption (unique constraint), trial daily-check-in limit, and check-in log. Authoritative race protection lives here, not in Redis.
- Pattern: Redis is the fast first check; the DB unique/atomic update is the authoritative guard. Both must agree before access is granted.
