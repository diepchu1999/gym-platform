# ADR-0010: Use S3-Compatible Object Storage for Documents / Dùng Object Storage tương thích S3 cho tài liệu

## Status
Proposed / Đề xuất

## Context / Bối cảnh
**EN —** The platform handles binary documents: CCCD / student card images, contract PDFs, invoices/receipts, and equipment/product images. Storing binaries in PostgreSQL bloats the database, slows backups, and complicates access control and CDN delivery. CCCD images are sensitive personal data.

**VI —** Hệ thống xử lý tài liệu nhị phân: ảnh CCCD / thẻ sinh viên, contract PDF, hóa đơn/biên nhận, ảnh thiết bị/sản phẩm. Lưu nhị phân trong PostgreSQL làm phình DB, chậm backup, và phức tạp việc kiểm soát truy cập và phân phối CDN. Ảnh CCCD là dữ liệu cá nhân nhạy cảm.

## Decision / Quyết định
**EN —** Use **S3-compatible Object Storage** (MinIO locally; a managed S3-compatible service in production) for all binary documents. The database stores **only the object key/URL** (e.g. `kyc_request.front_image_url`), never the bytes. Access to sensitive objects is brokered by the app via RBAC (e.g. `MEMBER_VIEW_FULL_CCCD`), ideally using time-limited pre-signed URLs; reads/writes of sensitive documents are audited.

**VI —** Dùng **Object Storage tương thích S3** (MinIO ở local; dịch vụ S3-compatible quản lý ở production) cho mọi tài liệu nhị phân. Database chỉ lưu **object key/URL** (vd `kyc_request.front_image_url`), không lưu bytes. Truy cập object nhạy cảm do app trung gian qua RBAC (vd `MEMBER_VIEW_FULL_CCCD`), nên dùng pre-signed URL có thời hạn; đọc/ghi tài liệu nhạy cảm được audit.

## Consequences / Hệ quả
**EN —** Positive: smaller/faster DB, cheap scalable storage, CDN-friendly, cleaner access control. Trade-offs: extra infra; must keep object lifecycle in sync with DB rows (orphan cleanup); pre-signed URL handling and bucket policy needed.

**VI —** Tích cực: DB nhỏ/nhanh hơn, lưu trữ rẻ và co giãn, thân thiện CDN, kiểm soát truy cập gọn. Đánh đổi: thêm hạ tầng; phải đồng bộ vòng đời object với dòng DB (dọn orphan); cần xử lý pre-signed URL và policy bucket.

## Rules / Quy tắc
- DB stores object key/URL only; never raw bytes (aligns with `database-guideline.md` Sensitive Data).
- Buckets (local): `kyc`, `contracts`, `invoices`, `media`.
- Sensitive objects (CCCD/student card): private buckets, access via RBAC + short-lived pre-signed URLs, audited.
- Consider server-side encryption for sensitive buckets.
