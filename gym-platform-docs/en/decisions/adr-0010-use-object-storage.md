# ADR-0010: Use S3-Compatible Object Storage for Documents

> English version. Vietnamese (canonical): [`../../vi/decisions/adr-0010-use-object-storage.md`](../../vi/decisions/adr-0010-use-object-storage.md).

## Status
Proposed

## Context
The platform handles binary documents: CCCD / student card images, contract PDFs, invoices/receipts, and equipment/product images. Storing binaries in PostgreSQL bloats the database, slows backups, and complicates access control and CDN delivery. CCCD images are sensitive personal data.

## Decision
Use **S3-compatible Object Storage** (MinIO locally; a managed S3-compatible service in production) for all binary documents. The database stores **only the object key/URL** (e.g. `kyc_request.front_image_url`), never the bytes. Access to sensitive objects is brokered by the app via RBAC (e.g. `MEMBER_VIEW_FULL_CCCD`), ideally using time-limited pre-signed URLs; reads/writes of sensitive documents are audited.

## Consequences
Positive: smaller/faster DB, cheap scalable storage, CDN-friendly, cleaner access control. Trade-offs: extra infra; must keep object lifecycle in sync with DB rows (orphan cleanup); pre-signed URL handling and bucket policy needed.

## Rules
- DB stores object key/URL only; never raw bytes (aligns with `database-guideline.md` Sensitive Data).
- Buckets (local): `kyc`, `contracts`, `invoices`, `media`.
- Sensitive objects (CCCD/student card): private buckets, access via RBAC + short-lived pre-signed URLs, audited.
- Consider server-side encryption for sensitive buckets.
