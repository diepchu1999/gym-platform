# Module: Equipment + Maintenance

> Bản tiếng Việt (canonical). English: [`../../en/modules/equipment-maintenance.md`](../../en/modules/equipment-maintenance.md).

## Mục đích
Quản lý thiết bị gym, lịch bảo trì, báo hỏng, phiếu bảo trì và lịch sử bảo trì.

## Tác nhân
- Member
- Receptionist
- Maintenance Staff
- Branch Manager
- Super Admin

## Quy tắc nghiệp vụ
- Thiết bị theo dõi theo chi nhánh và vị trí.
- Thiết bị nên có trạng thái và lịch sử bảo trì.
- Nhân viên/member có thể báo thiết bị hỏng.
- Thiết bị hỏng tạo phiếu bảo trì.
- Nhân viên bảo trì cập nhật phiếu và trạng thái thiết bị.

## Trạng thái Equipment
- ACTIVE
- NEED_MAINTENANCE
- UNDER_MAINTENANCE
- BROKEN
- RETIRED

## Trạng thái Maintenance Ticket
- NEW
- ASSIGNED
- IN_PROGRESS
- RESOLVED
- CLOSED

## Luồng chính
1. Nhân viên/admin tạo tài sản thiết bị.
2. Gán thiết bị cho chi nhánh/phòng/khu vực.
3. Nhân viên/member báo sự cố.
4. Tạo phiếu bảo trì.
5. Nhân viên bảo trì nhận/bắt đầu việc.
6. Trạng thái thiết bị thành UNDER_MAINTENANCE.
7. Nhân viên xử lý xong phiếu và ghi chi phí/ghi chú.
8. Thiết bị trở lại ACTIVE hoặc thành RETIRED.

## Trường dữ liệu gợi ý

Equipment Asset:
- id, asset_code, name, category, branch_id, room_id, area, status, purchase_date, supplier, next_maintenance_date

Maintenance Ticket:
- id, equipment_id, branch_id, reported_by, assigned_to, issue_description, status, priority, cost, resolved_at

Maintenance History:
- id, equipment_id, ticket_id, action, note, cost, performed_by, performed_at

## Gợi ý API
- `POST /equipment`
- `GET /equipment`
- `PATCH /equipment/{id}`
- `POST /equipment/{id}/report-issue`
- `GET /maintenance-tickets`
- `POST /maintenance-tickets/{id}/assign`
- `POST /maintenance-tickets/{id}/start`
- `POST /maintenance-tickets/{id}/resolve`

## Ý tưởng sản phẩm
Gắn QR code cho mỗi thiết bị. Member/nhân viên quét QR để báo sự cố kèm ảnh/video.

## Tests
- Tạo thiết bị.
- Báo sự cố tạo phiếu.
- Bắt đầu bảo trì cập nhật trạng thái thiết bị.
- Xử lý xong phiếu cập nhật trạng thái thiết bị và lịch sử.
