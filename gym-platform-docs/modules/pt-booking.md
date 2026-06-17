# Module: PT Booking

## Purpose

Manage 1-on-1 personal trainer booking.

## Actors

- Member
- PT
- Receptionist
- CSKH
- Branch Manager
- Super Admin

## Business Rules

- PT is 1-on-1.
- One session is 90 minutes by default.
- PT service operates from 06:00 to 22:00.
- Gym operates 24/24, but PT cannot be booked outside 06:00-22:00.
- Customer pays per PT session online or at counter.
- PT cannot have overlapping bookings.
- Member cannot have overlapping bookings.
- PT rating is anonymous to PT but visible to manager.

## Main Flow

1. Member selects PT, branch, date, and 90-minute slot.
2. System validates PT availability and service hours.
3. System creates booking PENDING_PAYMENT and holds slot.
4. Member pays online or at counter.
5. Payment success confirms booking.
6. Member arrives and checks in.
7. PT starts and completes session.
8. Member rates PT.

## Suggested Data Fields

Trainer:
- id
- staff_id
- branch_id
- level
- specialties
- price_per_session
- status

Trainer Availability:
- id
- trainer_id
- day_of_week
- start_time
- end_time
- branch_id

PT Booking Detail:
- booking_id
- trainer_id
- duration_minutes
- price
- completed_by_trainer_at

PT Rating:
- id
- booking_id
- member_id
- trainer_id
- rating
- comment
- visible_to_trainer
- created_at

## API Suggestions

- `GET /trainers`
- `GET /trainers/{id}/available-slots`
- `POST /pt-bookings`
- `POST /pt-bookings/{id}/complete`
- `POST /pt-bookings/{id}/ratings`

## Edge Cases

- Book outside 06:00-22:00.
- PT unavailable or day off.
- Payment not completed before hold expiry.
- Customer late; CSKH holds 30 minutes.
- Customer no-show.
- PT cancels due to emergency.

## Tests

- Book PT successfully.
- Reject slot outside operating hours.
- Prevent trainer double-booking.
- Prevent member overlapping booking.
- Rating hidden from trainer identity view.
