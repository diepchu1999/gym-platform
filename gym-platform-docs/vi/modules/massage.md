# Module: Massage Booking

> Bản tiếng Việt (canonical). English: [`../../en/modules/massage.md`](../../en/modules/massage.md).

## Mục đích
Quản lý booking massage VIP và booking massage trả phí thêm.

## Tác nhân
- VIP Member
- Member
- Massage Staff
- Receptionist
- CSKH
- Branch Manager
- Super Admin

## Quy tắc nghiệp vụ
- VIP được 3 lượt booking massage miễn phí mỗi tuần.
- Tuần tính từ Thứ 2 đến Chủ nhật trừ khi nghiệp vụ đổi.
- Sau khi hết quota miễn phí, booking massage phải trả phí.
- Thời lượng massage theo quy trình vận hành của nhân viên và cấu hình nội bộ.
- Nhân viên massage và phòng không được double-book.
- No-show mất quota/tiền.
- Hủy hợp lệ hoàn lại quota/tiền.

## Luồng chính
1. Member chọn dịch vụ massage, chi nhánh và slot.
2. Hệ thống kiểm tra trạng thái VIP và số lượt miễn phí đã dùng trong tuần.
3. Hệ thống kiểm tra nhân viên/phòng massage còn trống.
4. Nếu còn quota miễn phí, booking thành CONFIRMED.
5. Nếu hết quota, booking thành PENDING_PAYMENT.
6. Member đến và check-in.
7. Nhân viên massage hoàn thành dịch vụ.
8. Booking hoàn thành.

## Trường dữ liệu gợi ý

Massage Service:
- id, code, name, internal_duration_minutes, price, active

Massage Room:
- id, branch_id, name, status

Massage Staff Availability:
- id, staff_id, branch_id, start_time, end_time

Massage Booking Detail:
- booking_id, massage_service_id, massage_room_id, massage_staff_id, free_quota_used, paid_amount

Massage Weekly Usage:
- id, member_id, week_start_date, free_used_count

## Gợi ý API
- `GET /massage-services`
- `GET /massage/available-slots`
- `POST /massage-bookings`
- `POST /massage-bookings/{id}/cancel`
- `POST /massage-bookings/{id}/complete`

## Tests
- VIP đặt trong quota miễn phí.
- Lượt thứ 4 trong tuần phải trả phí.
- Chặn nhân viên double-book.
- Chặn phòng double-book.
- Hủy trước 10 giờ hoàn lại quota.
