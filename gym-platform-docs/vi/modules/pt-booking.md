# Module: PT Booking

> Bản tiếng Việt (canonical). English: [`../../en/modules/pt-booking.md`](../../en/modules/pt-booking.md).

## Mục đích
Quản lý booking huấn luyện viên cá nhân 1 kèm 1.

## Tác nhân
- Member
- PT
- Receptionist
- CSKH
- Branch Manager
- Super Admin

## Quy tắc nghiệp vụ
- PT là 1 kèm 1.
- Một buổi mặc định 90 phút.
- Dịch vụ PT hoạt động 06:00–22:00.
- Gym hoạt động 24/24, nhưng PT không đặt được ngoài 06:00–22:00.
- Khách trả phí theo buổi PT, online hoặc tại quầy.
- PT không được trùng lịch booking.
- Member không được trùng lịch booking.
- Đánh giá PT ẩn danh với PT nhưng quản lý thấy được.

## Luồng chính
1. Member chọn PT, chi nhánh, ngày và slot 90 phút.
2. Hệ thống kiểm tra PT rảnh và giờ dịch vụ.
3. Hệ thống tạo booking PENDING_PAYMENT và giữ slot.
4. Member trả online hoặc tại quầy.
5. Thanh toán thành công xác nhận booking.
6. Member đến và check-in.
7. PT bắt đầu và hoàn thành buổi.
8. Member đánh giá PT.

## Trường dữ liệu gợi ý

Trainer:
- id, staff_id, branch_id, level, specialties, price_per_session, status

Trainer Availability:
- id, trainer_id, day_of_week, start_time, end_time, branch_id

PT Booking Detail:
- booking_id, trainer_id, duration_minutes, price, completed_by_trainer_at

PT Rating:
- id, booking_id, member_id, trainer_id, rating, comment, visible_to_trainer, created_at

## Gợi ý API
- `GET /trainers`
- `GET /trainers/{id}/available-slots`
- `POST /pt-bookings`
- `POST /pt-bookings/{id}/complete`
- `POST /pt-bookings/{id}/ratings`

## Edge cases
- Đặt ngoài 06:00–22:00.
- PT bận hoặc nghỉ.
- Chưa thanh toán trước khi hold hết hạn.
- Khách trễ; CSKH giữ 30 phút.
- Khách no-show.
- PT hủy do sự cố khẩn cấp.

## Tests
- Đặt PT thành công.
- Từ chối slot ngoài giờ hoạt động.
- Chặn PT double-book.
- Chặn member trùng booking.
- Đánh giá ẩn danh với PT.
