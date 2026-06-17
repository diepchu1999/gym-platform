# Security Guideline

## Authentication

Use JWT/session-based authentication based on final architecture decision.

Separate user account from member profile and staff profile.

## Authorization

Use RBAC with branch scope.

Examples:
- Super Admin can access all branches.
- Branch Manager can access only assigned branches.
- Receptionist can create member, sell package, check in customer, and sell POS items at assigned branch.
- PT can view own schedule and own customers.
- PT cannot see who rated them.
- Manager can see rating author for internal handling.

## Sensitive Data

Sensitive data:
- CCCD number.
- CCCD image.
- Student card image.
- Payment data.
- Contract data.
- Rating author.

Rules:
- Do not expose sensitive data to unauthorized roles.
- Store files in object storage with private access.
- Use signed URLs for temporary access if needed.
- Mask CCCD in most UI responses.
- Audit all KYC approval/rejection actions.

## QR Security

- QR token should expire within 30-60 seconds.
- QR token should include nonce or one-time-use identifier.
- Mark nonce as used after successful scan.
- Prevent duplicate scan within 3-5 minutes.
- Do not encode sensitive member data directly in QR.

## Payment Security

- Verify callback signature when provider supports it.
- Use idempotency on provider transaction id/order code.
- Never process the same payment callback twice.
- Audit refunds and manual payment confirmation.

## File Upload

- Validate file type and size.
- Store original file metadata.
- Scan files if infrastructure supports it.
- Never expose raw storage path publicly.
