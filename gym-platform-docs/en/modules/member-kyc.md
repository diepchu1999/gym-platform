# Module: Member + KYC

## Purpose

Manage member profile, lead conversion, CCCD KYC, student verification, and trial eligibility.

## Actors

- Member
- Receptionist
- CSKH
- Branch Manager
- Super Admin

## Main Features

- Register member online.
- Register member at counter.
- Manage member profile.
- Upload CCCD for KYC.
- Approve/reject/request resubmit KYC.
- Upload student card for student verification.
- Approve/reject student verification.
- Track trial usage by CCCD.
- Track member status and history.

## Business Rules

- Trial requires approved CCCD KYC.
- One CCCD can use trial only once.
- Trial is 7 days free.
- Trial allows 1 check-in per day.
- Trial includes 1 group class trial session.
- Student discount requires approved student verification.
- CCCD should not be exposed in full to unauthorized roles.

## Suggested Data Fields

Member:
- id
- code
- full_name
- phone
- email
- gender
- date_of_birth
- home_branch_id
- status
- created_at
- updated_at

KYC Request:
- id
- member_id
- identity_type
- identity_number_masked
- identity_number_hash
- front_image_url
- back_image_url
- status
- submitted_at
- reviewed_by
- reviewed_at
- rejection_reason

Student Verification:
- id
- member_id
- school_name
- student_card_image_url
- status
- expired_at
- reviewed_by
- reviewed_at

Trial Usage:
- id
- member_id
- identity_number_hash
- trial_started_at
- trial_ended_at
- status

## API Suggestions

- `POST /members`
- `GET /members/{id}`
- `PATCH /members/{id}`
- `POST /members/{id}/kyc-requests`
- `POST /kyc-requests/{id}/approve`
- `POST /kyc-requests/{id}/reject`
- `POST /kyc-requests/{id}/request-resubmit`
- `POST /members/{id}/student-verifications`
- `POST /student-verifications/{id}/approve`
- `POST /student-verifications/{id}/reject`

## Edge Cases

- Same phone used by existing member.
- CCCD already used for trial.
- KYC rejected and member uploads again.
- Member tries to activate trial before KYC approved.
- Staff tries to view full CCCD without permission.
- Student verification expired.

## Tests

- Register member successfully.
- Reject duplicate phone/email according to rule.
- Submit KYC.
- Approve KYC.
- Reject KYC.
- Prevent second trial with same CCCD.
- Mask CCCD in normal responses.
