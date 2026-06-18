# Module: Private Room

> Bản tiếng Việt (canonical). English: [`../../en/modules/private-room.md`](../../en/modules/private-room.md).

## Mục đích
Quản lý phòng gym riêng và booking theo giờ.

## Tác nhân
- VIP Member
- Member
- Receptionist
- CSKH
- Branch Manager
- Super Admin

## Quy tắc nghiệp vụ
- Private room phải đặt trước khi dùng.
- VIP có quota giờ private room theo tháng.
- Mỗi booking tối đa 2 giờ.
- Nếu quota VIP không đủ, member có thể trả phí thêm nếu nghiệp vụ cho phép.
- Phòng không đặt được nếu đang bảo trì, đóng, dọn dẹp, hoặc đã được đặt.

## Luồng chính
1. Member chọn chi nhánh, phòng riêng, ngày và giờ.
2. Hệ thống kiểm tra thời lượng tối đa 2 giờ.
3. Hệ thống kiểm tra phòng còn trống.
4. Hệ thống kiểm tra quota VIP.
5. Nếu còn quota, booking thành CONFIRMED và trừ/giữ quota.
6. Nếu cần trả phí thêm, booking thành PENDING_PAYMENT.
7. Member đến và check-in.
8. Phòng chuyển IN_USE.
9. Booking hoàn thành và phòng chuyển CLEANING/AVAILABLE.

## Trường dữ liệu gợi ý

Private Room:
- id, branch_id, name, capacity, status, hourly_price

Private Room Quota:
- id, member_id, month, total_hours, used_hours, remaining_hours

Private Room Booking Detail:
- booking_id, room_id, duration_hours, quota_used_hours, paid_extra_amount

## Gợi ý API
- `GET /private-rooms`
- `GET /private-rooms/{id}/available-slots`
- `POST /private-room-bookings`
- `POST /private-room-bookings/{id}/cancel`
- `POST /private-room-bookings/{id}/check-in`
- `POST /private-room-bookings/{id}/complete`

## Edge cases
- Thời lượng booking lớn hơn 2 giờ.
- Quota VIP không đủ.
- Phòng chuyển bảo trì sau khi đặt.
- Khách trễ; CSKH giữ 30 phút.
- No-show.

## Tests
- VIP đặt bằng quota.
- Từ chối đặt quá 2 giờ.
- Chặn phòng double-book.
- Trừ quota atomic.
- Hủy trước 10 giờ khôi phục quota.
