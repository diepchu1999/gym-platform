# Module: Package + Contract + Payment

> Bản tiếng Việt (canonical). English: [`../../en/modules/package-contract-payment.md`](../../en/modules/package-contract-payment.md).

## Mục đích
Quản lý các gói, membership của member, tạo/kích hoạt hợp đồng, thanh toán, hoàn tiền, và luồng trả góp qua nhà cung cấp.

## Tác nhân
- Member
- Receptionist
- Accountant
- Branch Manager
- Super Admin

## Loại gói
- Trial 7 ngày.
- Gói tháng.
- Gói quý.
- Gói năm.
- Gói VIP.
- Gói sinh viên.
- Group class pass.
- Buổi/gói PT.
- Gói massage trả phí.
- Booking trả phí thêm cho private room.

## Quy tắc nghiệp vụ
- Gói tập chính dùng được trên mọi chi nhánh.
- Gói tháng/quý/năm check-in không giới hạn.
- Hợp đồng không cần quản lý duyệt.
- Hợp đồng active sau khi khách xác nhận/ký và thanh toán hợp lệ.
- Trả góp chỉ áp dụng gói quý/năm.
- Trả góp do nhà cung cấp tài chính xử lý.
- Payment callback phải idempotent.

## Trạng thái Contract
DRAFT, PENDING_SIGNATURE, ACTIVE, EXPIRED, TERMINATED, CANCELLED, SUSPENDED

## Trạng thái Payment
UNPAID, PENDING_PAYMENT, PAID, FAILED, EXPIRED, REFUNDED, PARTIALLY_REFUNDED

## Trạng thái Installment
DRAFT, SUBMITTED, PENDING_PROVIDER_APPROVAL, APPROVED, REJECTED, CANCELLED, DISBURSED

## Trường dữ liệu gợi ý

Package Plan:
- id, code, name, package_type, duration_days, price, is_vip, is_student_only, is_active

Contract:
- id, contract_code, member_id, package_plan_id, sale_branch_id, status, signed_at, effective_from, effective_to, total_amount

Order:
- id, order_code, member_id, branch_id, order_type, status, total_amount

Payment:
- id, order_id, payment_method, payment_status, provider, provider_transaction_id, amount, paid_at

Installment Application:
- id, order_id, provider, status, provider_application_code, submitted_at, approved_at, rejected_reason

## Gợi ý API
- `GET /package-plans`
- `POST /orders/package-purchase`
- `POST /contracts/{id}/sign`
- `POST /payments`
- `POST /payments/callback/{provider}`
- `POST /installment-applications`
- `POST /installment-applications/{id}/mark-approved`
- `POST /installment-applications/{id}/mark-rejected`

## Edge cases
- Payment callback đến hai lần.
- Hợp đồng đã ký nhưng thanh toán fail.
- Nhà cung cấp tài chính từ chối trả góp.
- Member mua gói năm bằng trả góp.
- Member cố trả góp cho gói tháng.
- Hoàn tiền sau khi hủy booking.

## Tests
- Mua gói thanh toán đủ.
- Kích hoạt hợp đồng sau thanh toán.
- Từ chối trả góp cho gói tháng.
- Xử lý payment callback trùng an toàn.
- Tạo bản ghi hoàn tiền.
