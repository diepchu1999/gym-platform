# Module: Group Class

## Purpose

Manage group class add-on packages, class sessions, booking, attendance, and capacity.

## Actors

- Member
- Instructor
- Receptionist
- CSKH
- Branch Manager
- Super Admin

## Business Rules

- Group class is an add-on sold by number of sessions.
- Trial includes 1 free group class session.
- Member needs active class pass or trial benefit to book.
- Class has type, instructor, room, schedule, and capacity.
- Do not allow booking when class is full.
- Do not allow room/instructor conflict.
- No-show loses session.
- Valid cancellation returns session.

## Main Flow

1. Member buys class pass or uses trial class benefit.
2. Member selects class session.
3. System checks pass/benefit and capacity.
4. System creates booking and deducts/holds 1 class session.
5. Member attends and checks in.
6. Instructor/receptionist confirms attendance.
7. Booking completes.

## Suggested Data Fields

Class Type:
- id
- code
- name
- description

Class Session:
- id
- class_type_id
- branch_id
- room_id
- instructor_id
- start_time
- end_time
- capacity
- booked_count
- status

Class Pass:
- id
- member_id
- class_type_scope
- total_sessions
- remaining_sessions
- valid_from
- valid_to
- status

Class Booking Detail:
- booking_id
- class_session_id
- class_pass_id
- attendance_status

## API Suggestions

- `GET /class-types`
- `GET /class-sessions`
- `POST /class-bookings`
- `POST /class-bookings/{id}/cancel`
- `POST /class-bookings/{id}/attendance`

## Race Conditions

- Last slot booked by two members.
- Same class pass session deducted twice.
- Instructor double-booked.
- Room double-booked.

## Tests

- Book class with class pass.
- Book class with trial benefit.
- Reject booking without pass.
- Reject full class.
- Cancel before 10 hours returns session.
- No-show consumes session.
