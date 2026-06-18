# ADR-0006: Dùng Keycloak cho Xác thực

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0006-use-keycloak-for-authentication.md`](../../en/decisions/adr-0006-use-keycloak-for-authentication.md).

## Status
Proposed (Đề xuất)

## Bối cảnh
Hệ thống cần đăng nhập, cấp token, chính sách mật khẩu, quản lý phiên, và sau này MFA/OTP/đăng nhập mạng xã hội cho cả nhân viên lẫn hội viên trên nhiều chi nhánh. Tự xây và bảo trì auth an toàn (hash mật khẩu, xoay token, khôi phục tài khoản) tốn kém và rủi ro. Phân quyền trong hệ thống là **theo chi nhánh** (vd Branch Manager chỉ ở chi nhánh được gán) — realm role không biểu diễn gọn được.

## Quyết định
Dùng **Keycloak** làm Identity Provider OIDC/OAuth2 **chỉ cho xác thực**. Monolith Spring Boot đóng vai **OAuth2 resource server**, kiểm tra JWT qua JWKS của Keycloak. **Phân quyền theo chi nhánh nằm ở app** (`rbac_*` + `staff_branch_assignment`). App DB không lưu mật khẩu; `identity_user_account` ánh xạ principal nội bộ với `sub` của Keycloak (`keycloak_user_id`).

## Hệ quả
Tích cực: chuyển gánh nặng bảo mật credential, MFA, phiên; chuẩn OIDC; SSO giữa admin/member web. Đánh đổi: thêm hạ tầng vận hành (Keycloak + DB của nó); định danh thành ngoại bộ (cần ánh xạ + đồng bộ provisioning); dev local cần cấu hình realm.

## Quy tắc
- Realm `gym-platform`; client `gym-admin-web`, `gym-member-web` (public + PKCE); API = resource server.
- Kiểm tra JWT qua `issuer-uri` + JWKS; không tin role do client gửi để phân quyền chi nhánh.
- Giữ kiểm tra quyền hạt mịn theo chi nhánh ở tầng application.
- Thay thế giả định identity dựa trên mật khẩu trong `data-model/p1-identity-org.md`.
