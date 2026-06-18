# Module: Group Class

> Bản tiếng Việt (canonical). English: [`../../en/modules/group-class.md`](../../en/modules/group-class.md).

## Mục đích
Quản lý gói group class bổ sung, buổi học, booking, điểm danh và sức chứa.

## Tác nhân
- Member
- Instructor
- Receptionist
- CSKH
- Branch Manager
- Super Admin

## Quy tắc nghiệp vụ
- Group class là add-on bán theo số buổi.
- Trial gồm 1 buổi group class miễn phí.
- Member cần class pass đang hiệu lực hoặc quyền lợi trial mới được đặt.
- Lớp có loại, huấn luyện viên, phòng, lịch và sức chứa.
- Không cho đặt khi lớp đầy.
- Không cho trùng lịch phòng/huấn luyện viên.
- No-show mất buổi.
- Hủy hợp lệ hoàn lại buổi.

## Luồng chính
1. Member mua class pass hoặc dùng quyền lợi trial.
2. Member chọn buổi học.
3. Hệ thống kiểm tra pass/quyền lợi và sức chứa.
4. Hệ thống tạo booking và trừ/giữ 1 buổi class.
5. Member đến và check-in.
6. Instructor/receptionist xác nhận điểm danh.
7. Booking hoàn thành.

## Trường dữ liệu gợi ý

Class Type:
- id, code, name, description

Class Session:
- id, class_type_id, branch_id, room_id, instructor_id, start_time, end_time, capacity, booked_count, status

Class Pass:
- id, member_id, class_type_scope, total_sessions, remaining_sessions, valid_from, valid_to, status

Class Booking Detail:
- booking_id, class_session_id, class_pass_id, attendance_status

## Gợi ý API
- `GET /class-types`
- `GET /class-sessions`
- `POST /class-bookings`
- `POST /class-bookings/{id}/cancel`
- `POST /class-bookings/{id}/attendance`

## Race Conditions
- Slot cuối bị hai member đặt.
- Cùng buổi class pass bị trừ hai lần.
- Instructor bị double-book.
- Phòng bị double-book.

## Tests
- Đặt lớp bằng class pass.
- Đặt lớp bằng quyền lợi trial.
- Từ chối đặt khi không có pass.
- Từ chối lớp đầy.
- Hủy trước 10 giờ hoàn lại buổi.
- No-show tiêu thụ buổi.
