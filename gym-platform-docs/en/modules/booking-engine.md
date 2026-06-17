# Module: Booking Engine

## Purpose

Provide shared booking logic for PT, group class, private room, and massage.

## Booking Types

- PT
- GROUP_CLASS
- PRIVATE_ROOM
- MASSAGE

## Common Business Rules

- Resource cannot be double-booked.
- Member cannot book overlapping services.
- Paid booking starts as PENDING_PAYMENT.
- Free/quota booking can become CONFIRMED immediately.
- Customer can cancel at least 10 hours before start to receive refund/session/quota back.
- Cancellation within 10 hours is non-refundable unless gym-side issue.
- If customer does not arrive, CSKH calls and holds slot up to 30 minutes.
- After 30 minutes without arrival, booking becomes NO_SHOW.

## Common Status

- DRAFT
- PENDING_PAYMENT
- CONFIRMED
- WAITING_CUSTOMER_CONFIRMATION
- CHECKED_IN
- IN_PROGRESS
- COMPLETED
- CANCELLED
- NO_SHOW
- EXPIRED
- REFUNDED

## Suggested Data Fields

Booking:
- id
- booking_code
- booking_type
- member_id
- branch_id
- resource_type
- resource_id
- start_time
- end_time
- status
- payment_status
- used_quota_type
- used_quota_amount
- cancellation_reason
- no_show_at
- created_at

Booking Hold:
- id
- booking_id
- expires_at
- status

Booking Event:
- id
- booking_id
- event_type
- actor_id
- note
- created_at

## API Suggestions

- `POST /bookings`
- `GET /bookings/{id}`
- `POST /bookings/{id}/cancel`
- `POST /bookings/{id}/check-in`
- `POST /bookings/{id}/start`
- `POST /bookings/{id}/complete`
- `POST /bookings/{id}/mark-no-show`

## Race Conditions

- Two members book last class slot.
- Two members book same PT slot.
- Two members book same private room slot.
- Quota deducted twice.
- Payment callback confirms booking twice.

## Tests

- Create booking successfully.
- Prevent overlapping resource booking.
- Cancel before 10 hours with refund.
- Cancel within 10 hours without refund.
- No-show after 30 minutes.
- Expire unpaid booking hold.
