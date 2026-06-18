# Module: QR Check-in

> Bản tiếng Việt (canonical). English: [`../../en/modules/checkin.md`](../../en/modules/checkin.md).

## Mục đích
Cho phép member vào các chi nhánh gym bằng mã QR tạm thời.

## Tác nhân
- Member
- Receptionist
- Branch Manager
- Super Admin

## Quy tắc nghiệp vụ
- Gói trả phí chính check-in không giới hạn.
- Trial chỉ check-in 1 lần/ngày.
- QR token hết hạn sau 30–60 giây.
- QR token nên dùng một lần.
- Chặn quét trùng trong 3–5 phút.
- Mọi gói chính dùng được trên tất cả chi nhánh.
- Trial không giới hạn khung giờ.

## Luồng chính
1. Member mở app.
2. App yêu cầu QR token tạm thời.
3. Member quét QR tại chi nhánh.
4. Backend kiểm tra token, member, gói và chi nhánh.
5. Backend tạo bản ghi check-in.
6. Cổng/lễ tân cho vào.

## Trường dữ liệu gợi ý

Checkin Token:
- id, member_id, nonce, expires_at, used_at, status

Checkin Log:
- id, member_id, branch_id, package_id, checkin_time, result, denied_reason, device_id

## Gợi ý API
- `POST /checkins/qr-tokens`
- `POST /checkins/scan`
- `GET /members/{id}/checkins`
- `GET /branches/{id}/checkins`

## Lý do từ chối (Denied Reasons)
- QR_EXPIRED
- QR_ALREADY_USED
- DUPLICATE_SCAN
- MEMBER_BLOCKED
- PACKAGE_EXPIRED
- TRIAL_DAILY_LIMIT_REACHED
- KYC_REQUIRED
- BRANCH_UNAVAILABLE

## Race Conditions
- Cùng QR bị quét nhiều lần.
- Trial member quét nhiều chi nhánh trong cùng ngày.
- Nhiều thiết bị cổng xử lý cùng token.

Dùng transaction và atomic update/unique trên việc sử dụng token.

## Tests
- Member trả phí check-in thành công.
- Trial member check-in lần đầu thành công.
- Trial check-in lần 2 cùng ngày bị từ chối.
- QR hết hạn bị từ chối.
- QR đã dùng bị từ chối.
- Quét trùng bị từ chối.

## Ghi chú kỹ thuật (implementation)

> Chỉ là chi tiết triển khai — KHÔNG đổi business rule ở trên. Xem ADR-0009 (Redis) + `architecture/solution-architecture.md`.

- **Redis (ephemeral / cổng nhanh)**: QR token TTL (30–60s), nonce một lần, lock chống quét trùng (cửa sổ 3–5 phút), và rate limit.
- **PostgreSQL (durable / nguồn sự thật)**: tiêu thụ nonce QR cuối cùng (unique constraint), giới hạn check-in trial theo ngày, và checkin log. Bảo vệ race condition có thẩm quyền nằm ở đây, không ở Redis.
- Mẫu: Redis là kiểm tra nhanh đầu tiên; unique/atomic update của DB là chốt có thẩm quyền. Cả hai phải nhất quán trước khi cho vào.
