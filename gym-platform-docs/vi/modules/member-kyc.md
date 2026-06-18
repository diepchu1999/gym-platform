# Module: Member + KYC

> Bản tiếng Việt (canonical). English: [`../../en/modules/member-kyc.md`](../../en/modules/member-kyc.md).

## Mục đích
Quản lý hồ sơ member, chuyển đổi lead, KYC CCCD, xác minh sinh viên và điều kiện trial.

## Tác nhân (Actors)
- Member
- Receptionist
- CSKH
- Branch Manager
- Super Admin

## Tính năng chính
- Đăng ký member online.
- Đăng ký member tại quầy.
- Quản lý hồ sơ member.
- Upload CCCD cho KYC.
- Duyệt/từ chối/yêu cầu nộp lại KYC.
- Upload thẻ sinh viên cho xác minh sinh viên.
- Duyệt/từ chối xác minh sinh viên.
- Theo dõi sử dụng trial theo CCCD.
- Theo dõi trạng thái và lịch sử member.

## Quy tắc nghiệp vụ
- Trial yêu cầu KYC CCCD được duyệt.
- Một CCCD chỉ dùng trial một lần.
- Trial miễn phí 7 ngày.
- Trial cho 1 check-in/ngày.
- Trial gồm 1 buổi group class.
- Giảm giá sinh viên yêu cầu xác minh sinh viên được duyệt.
- Không lộ CCCD đầy đủ cho vai trò không có quyền.

## Trường dữ liệu gợi ý

Member:
- id, code, full_name, phone, email, gender, date_of_birth, home_branch_id, status, created_at, updated_at

KYC Request:
- id, member_id, identity_type, identity_number_masked, identity_number_hash, front_image_url, back_image_url, status, submitted_at, reviewed_by, reviewed_at, rejection_reason

Student Verification:
- id, member_id, school_name, student_card_image_url, status, expired_at, reviewed_by, reviewed_at

Trial Usage:
- id, member_id, identity_number_hash, trial_started_at, trial_ended_at, status

## Gợi ý API
- `POST /members`
- `GET /members/{id}`
- `PATCH /members/{id}`
- `POST /members/{id}/kyc-requests`
- `POST /kyc-requests/{id}/approve`
- `POST /kyc-requests/{id}/reject`
- `POST /kyc-requests/{id}/request-resubmit`
- `POST /members/{id}/student-verifications`
- `POST /student-verifications/{id}/approve`
- `POST /student-verifications/{id}/reject`

## Edge cases
- Cùng phone đã dùng bởi member hiện hữu.
- CCCD đã dùng trial.
- KYC bị từ chối và member upload lại.
- Member cố kích hoạt trial trước khi KYC được duyệt.
- Nhân viên cố xem CCCD đầy đủ khi không có quyền.
- Xác minh sinh viên hết hạn.

## Tests
- Đăng ký member thành công.
- Từ chối phone/email trùng theo quy tắc.
- Nộp KYC.
- Duyệt KYC.
- Từ chối KYC.
- Chặn trial lần 2 với cùng CCCD.
- Che CCCD trong response thông thường.
