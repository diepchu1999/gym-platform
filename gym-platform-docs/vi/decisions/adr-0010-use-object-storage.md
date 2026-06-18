# ADR-0010: Dùng Object Storage tương thích S3 cho tài liệu

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0010-use-object-storage.md`](../../en/decisions/adr-0010-use-object-storage.md).

## Status
Proposed (Đề xuất)

## Bối cảnh
Hệ thống xử lý tài liệu nhị phân: ảnh CCCD / thẻ sinh viên, contract PDF, hóa đơn/biên nhận, ảnh thiết bị/sản phẩm. Lưu nhị phân trong PostgreSQL làm phình DB, chậm backup, và phức tạp việc kiểm soát truy cập và phân phối CDN. Ảnh CCCD là dữ liệu cá nhân nhạy cảm.

## Quyết định
Dùng **Object Storage tương thích S3** (MinIO ở local; dịch vụ S3-compatible quản lý ở production) cho mọi tài liệu nhị phân. Database chỉ lưu **object key/URL** (vd `kyc_request.front_image_url`), không lưu bytes. Truy cập object nhạy cảm do app trung gian qua RBAC (vd `MEMBER_VIEW_FULL_CCCD`), nên dùng pre-signed URL có thời hạn; đọc/ghi tài liệu nhạy cảm được audit.

## Hệ quả
Tích cực: DB nhỏ/nhanh hơn, lưu trữ rẻ và co giãn, thân thiện CDN, kiểm soát truy cập gọn. Đánh đổi: thêm hạ tầng; phải đồng bộ vòng đời object với dòng DB (dọn orphan); cần xử lý pre-signed URL và policy bucket.

## Quy tắc
- DB chỉ lưu object key/URL; không lưu bytes thô (khớp `database-guideline.md` mục Sensitive Data).
- Bucket (local): `kyc`, `contracts`, `invoices`, `media`.
- Object nhạy cảm (CCCD/thẻ SV): bucket private, truy cập qua RBAC + pre-signed URL ngắn hạn, có audit.
- Cân nhắc mã hóa phía server cho bucket nhạy cảm.
