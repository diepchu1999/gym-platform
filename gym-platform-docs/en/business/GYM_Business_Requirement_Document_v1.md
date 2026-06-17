# Business Requirement Document - GYM Multi-Branch Platform
Version: v1.0
Purpose: Training Claude Code and serving development.

> English version. Vietnamese (canonical): [`../../vi/business/GYM_Business_Requirement_Document_v1.md`](../../vi/business/GYM_Business_Requirement_Document_v1.md).

## Product Vision
A multi-branch GYM chain system in Ho Chi Minh City, supporting member app / admin / staff portal, system-wide membership, QR check-in, service booking, contracts, payment, inventory, pantry, equipment, CRM, reporting, and audit.

## Global Business Rules
- **BR-001**: All main gym packages are usable across every branch.
- **BR-002**: Monthly/quarterly/yearly packages have no daily check-in limit.
- **BR-003**: Trial is free for 7 days, requires CCCD KYC; one CCCD can use trial only once.
- **BR-004**: Trial allows only 1 check-in per day, with no time-window restriction.
- **BR-005**: Trial includes 1 free group class session.
- **BR-006**: Group class is an add-on, sold by number of sessions and requires scheduled booking.
- **BR-007**: PT is 1-on-1, default 90 minutes per session, operating 06:00-22:00.
- **BR-008**: Private room must be booked by hour, max 2 hours per booking.
- **BR-009**: VIP has a private-room hour quota per month; once exhausted, may pay extra.
- **BR-010**: VIP gets 3 free massages per week; once used up, pay extra.
- **BR-011**: Contracts do not need manager approval.
- **BR-012**: Installment applies only to quarterly/yearly packages, via finance companies.
- **BR-013**: Partner products are imported by the gym, with inventory tracked per branch.
- **BR-014**: Pantry sells to all members within 06:00-22:00.
- **BR-015**: Cancelling a booking at least 10 hours before start time refunds money/session.
- **BR-016**: Customer not arriving on time: CSKH calls, holds the slot up to 30 minutes, then NO_SHOW.

## Domains
- Identity & RBAC
- Branch
- Staff
- Member
- KYC / Verification
- Membership Package
- Contract
- Payment
- Finance Installment Integration
- QR Check-in
- Booking Engine
- Group Class
- PT Booking
- Private Room
- Massage
- Product / Inventory / POS
- Pantry
- Equipment / Maintenance
- CRM / Customer Care
- Rating / Feedback
- Promotion / Coupon
- Notification
- Report / Analytics
- Audit Log

## Status Catalog
- **Member**: LEAD, REGISTERED, KYC_PENDING, ACTIVE, INACTIVE, SUSPENDED, BLACKLISTED
- **KYC/Verification**: NOT_SUBMITTED, PENDING, APPROVED, REJECTED, REQUEST_RESUBMIT, EXPIRED
- **Membership**: PENDING_PAYMENT, ACTIVE, EXPIRED, SUSPENDED, CANCELLED
- **Contract**: DRAFT, PENDING_SIGNATURE, ACTIVE, EXPIRED, TERMINATED, CANCELLED, SUSPENDED
- **Payment**: UNPAID, PENDING_PAYMENT, PAID, FAILED, EXPIRED, REFUNDED, PARTIALLY_REFUNDED
- **Booking**: DRAFT, PENDING_PAYMENT, CONFIRMED, WAITING_CUSTOMER_CONFIRMATION, CHECKED_IN, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW, EXPIRED, REFUNDED

## Claude Code Instructions
- Build Modular Monolith.
- Use enums for statuses.
- Use transaction/idempotency for booking, payment, QR scan, quota, stock.
- Validate server-side; never trust frontend for price/quota/permission.
- Add audit log for sensitive operations.
