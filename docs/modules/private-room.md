# Module: Private Room

## Purpose

Manage private gym rooms and hourly booking.

## Actors

- VIP Member
- Member
- Receptionist
- CSKH
- Branch Manager
- Super Admin

## Business Rules

- Private room must be booked before use.
- VIP has private room monthly hour quota.
- Each booking can be at most 2 hours.
- If VIP quota is insufficient, member can pay extra if business allows.
- Room cannot be booked if under maintenance, closed, cleaning, or already booked.

## Main Flow

1. Member selects branch, private room, date, and time.
2. System validates max 2-hour duration.
3. System checks room availability.
4. System checks VIP quota.
5. If quota available, booking becomes CONFIRMED and quota is deducted/held.
6. If paid extra is required, booking becomes PENDING_PAYMENT.
7. Member arrives and checks in.
8. Room becomes IN_USE.
9. Booking completes and room moves to CLEANING/AVAILABLE.

## Suggested Data Fields

Private Room:
- id
- branch_id
- name
- capacity
- status
- hourly_price

Private Room Quota:
- id
- member_id
- month
- total_hours
- used_hours
- remaining_hours

Private Room Booking Detail:
- booking_id
- room_id
- duration_hours
- quota_used_hours
- paid_extra_amount

## API Suggestions

- `GET /private-rooms`
- `GET /private-rooms/{id}/available-slots`
- `POST /private-room-bookings`
- `POST /private-room-bookings/{id}/cancel`
- `POST /private-room-bookings/{id}/check-in`
- `POST /private-room-bookings/{id}/complete`

## Edge Cases

- Booking duration greater than 2 hours.
- VIP quota insufficient.
- Room becomes maintenance after booking.
- Customer late; CSKH holds 30 minutes.
- No-show.

## Tests

- VIP books with quota.
- Reject booking over 2 hours.
- Prevent room double-booking.
- Deduct quota atomically.
- Cancel before 10 hours restores quota.
