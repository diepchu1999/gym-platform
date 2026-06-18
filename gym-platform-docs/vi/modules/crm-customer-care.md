# Module: CRM + Customer Care

> Bản tiếng Việt (canonical). English: [`../../en/modules/crm-customer-care.md`](../../en/modules/crm-customer-care.md).

## Mục đích
Quản lý lead, chuyển đổi trial, follow-up, luồng gọi no-show của booking, ticket hỗ trợ và giữ chân khách.

## Tác nhân
- CSKH
- Sales
- Receptionist
- Branch Manager
- Marketing Staff
- Super Admin

## Trạng thái Lead
- NEW
- CONTACTED
- INTERESTED
- VISITED
- TRIAL_REGISTERED
- CONVERTED
- LOST

## Trạng thái Ticket
- NEW
- ASSIGNED
- IN_PROGRESS
- WAITING_CUSTOMER
- RESOLVED
- CLOSED

## Quy tắc nghiệp vụ
- Khách trial nên được follow-up trong thời gian trial.
- No-show booking tạo task cho CSKH tại giờ bắt đầu booking khi member chưa check-in.
- CSKH có thể giữ chỗ tối đa 30 phút sau khi gọi khách.
- Ghi chú chăm sóc nên lưu trong timeline của member.
- Khiếu nại và yêu cầu hoàn tiền nên theo dõi dạng ticket.

## Gợi ý follow-up Trial
- Ngày 1: Chào mừng và hướng dẫn QR.
- Ngày 3: Gợi ý buổi group class trial.
- Ngày 5: Đề xuất khuyến mãi gói.
- Ngày 7: Nhắc hết hạn và gọi chuyển đổi.

## Trường dữ liệu gợi ý

Lead:
- id, full_name, phone, source, interested_branch_id, interested_service, status, assigned_to, next_follow_up_at

Care Task:
- id, member_id, task_type, assigned_to, due_at, status, result, note

Ticket:
- id, member_id, branch_id, category, priority, status, assigned_to, description, resolution

## Gợi ý API
- `POST /leads`
- `GET /leads`
- `POST /leads/{id}/convert`
- `POST /care-tasks`
- `POST /care-tasks/{id}/complete`
- `POST /tickets`
- `POST /tickets/{id}/assign`
- `POST /tickets/{id}/resolve`

## Tests
- Tạo lead.
- Chuyển lead thành member.
- Tạo care task no-show.
- Hoàn thành care task với kết quả.
- Tạo và xử lý ticket.
