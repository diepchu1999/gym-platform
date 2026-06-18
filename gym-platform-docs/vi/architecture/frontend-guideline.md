# Hướng dẫn Frontend

> Bản tiếng Việt (canonical). English: [`../../en/architecture/frontend-guideline.md`](../../en/architecture/frontend-guideline.md).

## Nền tảng kỹ thuật
- React.
- TypeScript.
- Web trước.
- Chưa có app mobile ở giai đoạn đầu.
- Dùng pattern React hiện đại với functional component và hooks.

## Mục tiêu ứng dụng
Mục tiêu web ban đầu:
- Admin web.
- Màn hình vận hành nhân viên/chi nhánh.
- Web hướng member có thể thêm sau khi xong luồng admin lõi.

Admin và member web có thể:
- Một app React với route group theo vai trò ở giai đoạn đầu.
- Tách thành app riêng sau nếu quy mô sản phẩm yêu cầu.

## Cấu trúc khuyến nghị

```text
src/
 ├── app/                 # app bootstrap, router, providers
 ├── shared/              # shared UI, utils, hooks, API client
 ├── features/
 │   ├── member/
 │   ├── kyc/
 │   ├── package/
 │   ├── contract/
 │   ├── payment/
 │   ├── checkin/
 │   ├── booking/
 │   ├── group-class/
 │   ├── pt/
 │   ├── private-room/
 │   ├── inventory/
 │   ├── pantry/
 │   └── equipment/
 └── pages/               # màn hình cấp route nếu dùng page folder
```

## Quy tắc TypeScript
- Không dùng JavaScript thuần cho file nguồn chính.
- Model request/response API phải có kiểu.
- Tránh `any` trừ khi có lý do rõ ràng.
- Dùng type/enum theo miền cho các trạng thái.

## Tích hợp API
- Tách hàm API client khỏi UI component.
- UI component không nên tự dựng URL thô ở khắp nơi.
- Tập trung xử lý auth token.
- Tập trung xử lý lỗi cho business error.

## Nền tảng UI/UX
Màn hình admin ưu tiên tốc độ vận hành:
- Tìm member theo phone/CCCD/member code.
- Hỗ trợ check-in nhanh.
- Xem lịch booking dạng calendar.
- Thấy rõ trạng thái thanh toán.
- Filter theo chi nhánh ở mọi nơi liên quan.
- Badge trạng thái rõ ràng.

## Quy tắc tài liệu
Khi chọn thư viện frontend như UI kit, router, query/caching, state management, hay form library, cập nhật file này và tạo ADR nếu quyết định quan trọng.
