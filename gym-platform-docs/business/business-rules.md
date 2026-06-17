# Business Rules

## Membership & Branch Access

BR-001: All main gym packages are valid across all branches.

BR-002: Monthly, quarterly, and yearly packages have unlimited daily check-in.

BR-003: The system must still prevent duplicate QR scans within a short time window, even when the package has unlimited check-in.

BR-004: The system should track home branch, sale branch, and actual check-in branch separately for reporting.

## Trial Package

BR-005: Trial package is free for 7 days.

BR-006: Trial requires approved CCCD KYC before activation.

BR-007: One CCCD can use trial only once.

BR-008: Trial allows 1 gym check-in per day.

BR-009: Trial has no time-window restriction because the gym operates 24/24.

BR-010: Trial includes exactly 1 group class trial session.

BR-011: Trial does not include private room, massage VIP benefit, or free PT.

## VIP

BR-012: VIP members can use private rooms only through booking.

BR-013: VIP private room usage is controlled by monthly hour quota.

BR-014: One private room booking can be at most 2 hours.

BR-015: VIP members get 3 free massage bookings per week.

BR-016: After the 3 free weekly massage bookings are used, further massage bookings require payment.

## Booking Common Rules

BR-017: Booking resources cannot overlap for the same resource.

BR-018: Member should not be able to book overlapping sessions for themselves.

BR-019: Customer can cancel at least 10 hours before start time to receive refund/session/quota back.

BR-020: Cancellation within 10 hours before start time is non-refundable unless cancellation is caused by gym-side issue.

BR-021: If customer does not arrive at booking time, CSKH calls to confirm and can hold the slot for up to 30 minutes.

BR-022: After 30 minutes without arrival, booking becomes NO_SHOW.

BR-023: No-show does not refund payment/session/quota.

BR-024: If gym cancels due to PT unavailable, room maintenance, class cancellation, or system issue, customer should receive refund/session/quota back.

## Group Class

BR-025: Group class is an add-on service and is sold by number of sessions.

BR-026: Member needs active class pass or trial class benefit to book group class.

BR-027: Group class has schedule, instructor, room, and capacity.

BR-028: Do not allow booking when class is full.

BR-029: Do not allow instructor or room schedule conflict.

## PT Booking

BR-030: PT is 1-on-1.

BR-031: One PT session is 90 minutes by default.

BR-032: PT booking is only available from 06:00 to 22:00.

BR-033: Gym operates 24/24 but PT service does not.

BR-034: Customer can pay online or at counter based on 1-session PT price.

BR-035: PT rating must be anonymous to PT but visible to manager for internal handling.

## Private Room

BR-036: Private room is a limited resource and must be booked by hour.

BR-037: Each private room has branch, room status, availability, and booking schedule.

BR-038: Private room cannot be booked while under maintenance, closed, cleaning, or already booked.

## Massage

BR-039: Massage service is available to VIP members with 3 free sessions per week.

BR-040: Massage duration follows operational process and can be configured internally per service type.

BR-041: Massage booking must avoid staff and room schedule conflicts.

## Contract & Payment

BR-042: Contract does not need manager approval.

BR-043: Contract can become active after customer signature/confirmation and valid payment.

BR-044: Installment is only available for quarterly and yearly packages.

BR-045: Installment is handled by finance providers such as FE Credit or Home Credit.

BR-046: Once finance provider approves/disburses, gym treats the payment as valid.

## Product, Inventory, Pantry

BR-047: Partner products are purchased and stocked by the gym.

BR-048: Inventory must be tracked per branch.

BR-049: Selling product or pantry item must deduct stock atomically.

BR-050: Pantry sells to all members from 06:00 to 22:00.

BR-051: Pantry food/drink items should support expiry date and batch/lot tracking.

## Equipment

BR-052: Equipment is tracked by branch, area/room, status, quantity or asset code, and maintenance history.

BR-053: Equipment can be reported as broken by staff or members.

BR-054: Maintenance ticket should track assignee, status, cost, and resolution.
