# Hướng dẫn Bảo mật

> Bản tiếng Việt (canonical). English: [`../../en/architecture/security-guideline.md`](../../en/architecture/security-guideline.md).

## Xác thực
Dùng xác thực dựa trên JWT/session theo quyết định kiến trúc cuối (xem ADR-0006: Keycloak).

Tách user account khỏi member profile và staff profile.

## Phân quyền
Dùng RBAC theo phạm vi chi nhánh.

Ví dụ:
- Super Admin truy cập mọi chi nhánh.
- Branch Manager chỉ truy cập chi nhánh được gán.
- Receptionist tạo member, bán gói, check-in khách, và bán POS tại chi nhánh được gán.
- PT xem lịch và khách của mình.
- PT không thấy ai đã đánh giá mình.
- Manager thấy tác giả đánh giá để xử lý nội bộ.

## Dữ liệu nhạy cảm
Dữ liệu nhạy cảm:
- Số CCCD.
- Ảnh CCCD.
- Ảnh thẻ sinh viên.
- Dữ liệu thanh toán.
- Dữ liệu hợp đồng.
- Tác giả đánh giá.

Quy tắc:
- Không expose dữ liệu nhạy cảm cho vai trò không có quyền.
- Lưu file ở object storage với truy cập private.
- Dùng signed URL cho truy cập tạm thời nếu cần.
- Che CCCD trong hầu hết response UI.
- Audit mọi hành động duyệt/từ chối KYC.

## Bảo mật QR
- QR token nên hết hạn trong 30–60 giây.
- QR token nên gồm nonce hoặc định danh dùng-một-lần.
- Đánh dấu nonce đã dùng sau khi quét thành công.
- Chặn quét trùng trong 3–5 phút.
- Không nhúng dữ liệu nhạy cảm của member trực tiếp vào QR.

## Bảo mật Thanh toán
- Xác minh chữ ký callback khi nhà cung cấp hỗ trợ.
- Dùng idempotency theo provider transaction id/order code.
- Không bao giờ xử lý cùng một payment callback hai lần.
- Audit hoàn tiền và xác nhận thanh toán thủ công.

## Upload File
- Validate loại và kích thước file.
- Lưu metadata file gốc.
- Quét file nếu hạ tầng hỗ trợ.
- Không bao giờ expose path lưu trữ thô ra public.
