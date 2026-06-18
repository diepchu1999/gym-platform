# Module: Staff + RBAC

> Bản tiếng Việt (canonical). English: [`../../en/modules/staff-rbac.md`](../../en/modules/staff-rbac.md).

## Mục đích
Quản lý hồ sơ nhân viên, vai trò, gán chi nhánh và quyền truy cập.

## Vai trò (Roles)
- Super Admin
- Operation Manager
- Branch Manager
- Receptionist
- Sales
- Customer Care
- Personal Trainer
- Class Instructor
- Massage Staff
- Cleaner
- Parking Staff
- Maintenance Staff
- Accountant
- Marketing Staff
- Partner Manager

## Quy tắc nghiệp vụ
- Super Admin truy cập mọi chi nhánh.
- Branch Manager chỉ truy cập chi nhánh được gán.
- Receptionist có thể tạo member, bán gói, hỗ trợ check-in, và bán POS tại chi nhánh được gán.
- CSKH quản lý ticket, follow-up và gọi no-show.
- PT xem lịch và khách được gán của mình.
- PT không thấy tác giả đánh giá.
- Manager thấy tác giả đánh giá để xử lý nội bộ.
- Nhân viên bảo trì quản lý phiếu bảo trì được gán.

## Trường dữ liệu gợi ý

Staff:
- id, user_account_id, full_name, phone, email, employee_code, status

Role:
- id, code, name

Permission:
- id, code, description

Staff Branch Assignment:
- staff_id, branch_id, role_id, active

## Gợi ý API
- `POST /staff`
- `GET /staff`
- `PATCH /staff/{id}`
- `POST /staff/{id}/roles`
- `POST /staff/{id}/branch-assignments`
- `GET /permissions`

## Tests
- Branch manager không truy cập được chi nhánh khác.
- PT không xem được tác giả đánh giá.
- Manager xem được tác giả đánh giá.
- Receptionist tạo được member tại chi nhánh được gán.
