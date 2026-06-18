# Module: Booking Engine

> Bản tiếng Việt (canonical). English: [`../../en/modules/booking-engine.md`](../../en/modules/booking-engine.md).

## Mục đích
Cung cấp logic booking dùng chung cho PT, group class, private room và massage.

## Loại booking
- PT
- GROUP_CLASS
- PRIVATE_ROOM
- MASSAGE

## Quy tắc nghiệp vụ chung
- Tài nguyên không được double-book.
- Member không được đặt các dịch vụ trùng giờ.
- Booking trả phí bắt đầu ở PENDING_PAYMENT.
- Booking miễn phí/quota có thể vào thẳng CONFIRMED.
- Khách hủy ít nhất 10 giờ trước giờ bắt đầu để được hoàn tiền/buổi/quota.
- Hủy trong vòng 10 giờ không hoàn, trừ khi do phía gym.
- Nếu khách không đến, CSKH gọi và giữ chỗ tối đa 30 phút.
- Sau 30 phút không đến, booking chuyển NO_SHOW.

## Trạng thái chung
- DRAFT
- PENDING_PAYMENT
- CONFIRMED
- WAITING_CUSTOMER_CONFIRMATION
- CHECKED_IN
- IN_PROGRESS
- COMPLETED
- CANCELLED
- NO_SHOW
- EXPIRED
- REFUNDED

## Trường dữ liệu gợi ý

Booking:
- id, booking_code, booking_type, member_id, branch_id, resource_type, resource_id, start_time, end_time, status, payment_status, used_quota_type, used_quota_amount, cancellation_reason, no_show_at, created_at

Booking Hold:
- id, booking_id, expires_at, status

Booking Event:
- id, booking_id, event_type, actor_id, note, created_at

## Gợi ý API
- `POST /bookings`
- `GET /bookings/{id}`
- `POST /bookings/{id}/cancel`
- `POST /bookings/{id}/check-in`
- `POST /bookings/{id}/start`
- `POST /bookings/{id}/complete`
- `POST /bookings/{id}/mark-no-show`

## Race Conditions
- Hai member đặt slot lớp cuối cùng.
- Hai member đặt cùng slot PT.
- Hai member đặt cùng slot private room.
- Quota bị trừ hai lần.
- Payment callback xác nhận booking hai lần.

## Tests
- Tạo booking thành công.
- Chặn đặt trùng tài nguyên.
- Hủy trước 10 giờ có hoàn.
- Hủy trong 10 giờ không hoàn.
- No-show sau 30 phút.
- Hết hạn booking hold chưa thanh toán.
