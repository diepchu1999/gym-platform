# Module: Package + Contract + Payment

## Purpose

Manage package plans, member membership, contract creation/activation, payment, refund, and installment provider flow.

## Actors

- Member
- Receptionist
- Accountant
- Branch Manager
- Super Admin

## Package Types

- Trial 7 days.
- Monthly package.
- Quarterly package.
- Yearly package.
- VIP package.
- Student package.
- Group class pass.
- PT single session/package.
- Massage paid add-on.
- Private room paid extra booking.

## Business Rules

- Main gym packages are valid across all branches.
- Monthly/quarterly/yearly packages have unlimited check-in.
- Contract does not need manager approval.
- Contract becomes active after customer confirmation/signature and valid payment.
- Installment applies only to quarterly/yearly packages.
- Installment is handled by finance provider.
- Payment callbacks must be idempotent.

## Contract Status

- DRAFT
- PENDING_SIGNATURE
- ACTIVE
- EXPIRED
- TERMINATED
- CANCELLED
- SUSPENDED

## Payment Status

- UNPAID
- PENDING_PAYMENT
- PAID
- FAILED
- EXPIRED
- REFUNDED
- PARTIALLY_REFUNDED

## Installment Status

- DRAFT
- SUBMITTED
- PENDING_PROVIDER_APPROVAL
- APPROVED
- REJECTED
- CANCELLED
- DISBURSED

## Suggested Data Fields

Package Plan:
- id
- code
- name
- package_type
- duration_days
- price
- is_vip
- is_student_only
- is_active

Contract:
- id
- contract_code
- member_id
- package_plan_id
- sale_branch_id
- status
- signed_at
- effective_from
- effective_to
- total_amount

Order:
- id
- order_code
- member_id
- branch_id
- order_type
- status
- total_amount

Payment:
- id
- order_id
- payment_method
- payment_status
- provider
- provider_transaction_id
- amount
- paid_at

Installment Application:
- id
- order_id
- provider
- status
- provider_application_code
- submitted_at
- approved_at
- rejected_reason

## API Suggestions

- `GET /package-plans`
- `POST /orders/package-purchase`
- `POST /contracts/{id}/sign`
- `POST /payments`
- `POST /payments/callback/{provider}`
- `POST /installment-applications`
- `POST /installment-applications/{id}/mark-approved`
- `POST /installment-applications/{id}/mark-rejected`

## Edge Cases

- Payment callback arrives twice.
- Contract signed but payment failed.
- Finance provider rejects installment.
- Member buys yearly package with installment.
- Member tries installment for monthly package.
- Refund after booking cancellation.

## Tests

- Buy package full payment.
- Activate contract after payment.
- Reject installment for monthly package.
- Handle duplicate payment callback safely.
- Create refund record.
