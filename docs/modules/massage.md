# Module: Massage Booking

## Purpose

Manage VIP massage booking and paid massage extra booking.

## Actors

- VIP Member
- Member
- Massage Staff
- Receptionist
- CSKH
- Branch Manager
- Super Admin

## Business Rules

- VIP gets 3 free massage bookings per week.
- Week is Monday to Sunday unless changed by business.
- After free quota is used, massage booking requires payment.
- Massage duration follows staff operation process and can be internally configured.
- Massage staff and room cannot be double-booked.
- No-show loses quota/payment.
- Valid cancellation returns quota/payment.

## Main Flow

1. Member selects massage service, branch, and slot.
2. System checks member VIP status and weekly free usage.
3. System checks massage staff/room availability.
4. If free quota remains, booking becomes CONFIRMED.
5. If quota used up, booking becomes PENDING_PAYMENT.
6. Member arrives and checks in.
7. Massage staff completes service.
8. Booking completes.

## Suggested Data Fields

Massage Service:
- id
- code
- name
- internal_duration_minutes
- price
- active

Massage Room:
- id
- branch_id
- name
- status

Massage Staff Availability:
- id
- staff_id
- branch_id
- start_time
- end_time

Massage Booking Detail:
- booking_id
- massage_service_id
- massage_room_id
- massage_staff_id
- free_quota_used
- paid_amount

Massage Weekly Usage:
- id
- member_id
- week_start_date
- free_used_count

## API Suggestions

- `GET /massage-services`
- `GET /massage/available-slots`
- `POST /massage-bookings`
- `POST /massage-bookings/{id}/cancel`
- `POST /massage-bookings/{id}/complete`

## Tests

- VIP books within free quota.
- Fourth weekly booking requires payment.
- Prevent staff double-booking.
- Prevent room double-booking.
- Cancel before 10 hours returns quota.
