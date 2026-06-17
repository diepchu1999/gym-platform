# API Guideline

## Style

Use RESTful APIs with JSON request/response.

Use plural nouns for resources:

```text
/members
/packages
/contracts
/payments
/checkins
/bookings
/class-sessions
/trainers
/private-rooms
/products
/equipment
```

## Response Shape

Recommended success response:

```json
{
  "data": {},
  "meta": {},
  "message": "OK"
}
```

Recommended error response:

```json
{
  "error": {
    "code": "BOOKING_SLOT_UNAVAILABLE",
    "message": "Selected slot is no longer available",
    "details": {}
  }
}
```

## Pagination

Use cursor pagination for large lists where possible.

For admin small lists, offset pagination can be acceptable initially.

## Idempotency

Use idempotency key for:
- Payment callback.
- Booking creation from app.
- QR check-in scan.
- Order creation.

## Common HTTP Codes

- 200: success.
- 201: created.
- 204: no content.
- 400: validation/business input error.
- 401: unauthenticated.
- 403: forbidden.
- 404: not found.
- 409: conflict/race condition/business conflict.
- 422: valid JSON but business rule failed.
- 500: unexpected server error.

## Example Endpoints

Member:
- `POST /members`
- `GET /members/{id}`
- `PATCH /members/{id}`

KYC:
- `POST /members/{id}/kyc-requests`
- `POST /kyc-requests/{id}/approve`
- `POST /kyc-requests/{id}/reject`

Booking:
- `POST /bookings`
- `POST /bookings/{id}/cancel`
- `POST /bookings/{id}/check-in`
- `POST /bookings/{id}/complete`

Check-in:
- `POST /checkins/qr-tokens`
- `POST /checkins/scan`

Payment:
- `POST /orders`
- `POST /payments`
- `POST /payments/callback/{provider}`
