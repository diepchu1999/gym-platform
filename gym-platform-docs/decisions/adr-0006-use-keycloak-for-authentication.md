# ADR-0006: Use Keycloak for Authentication / Dùng Keycloak cho Xác thực

## Status
Proposed / Đề xuất

## Context / Bối cảnh
**EN —** The platform needs login, token issuance, password policy, session management, and later MFA/OTP/social login for both staff and members across multiple branches. Building and maintaining a secure in-house auth (password hashing, token rotation, account recovery) is costly and risky. Authorization in this system is **branch-scoped** (e.g. Branch Manager only at assigned branches), which realm roles alone cannot express cleanly.

**VI —** Hệ thống cần đăng nhập, cấp token, chính sách mật khẩu, quản lý phiên, và sau này MFA/OTP/đăng nhập mạng xã hội cho cả nhân viên lẫn hội viên trên nhiều chi nhánh. Tự xây và bảo trì auth an toàn (hash mật khẩu, xoay token, khôi phục tài khoản) tốn kém và rủi ro. Phân quyền trong hệ thống là **theo chi nhánh** (vd Branch Manager chỉ ở chi nhánh được gán) — realm role không biểu diễn gọn được.

## Decision / Quyết định
**EN —** Use **Keycloak** as the OIDC/OAuth2 Identity Provider for **authentication only**. The Spring Boot monolith acts as an **OAuth2 resource server** validating JWTs via Keycloak's JWKS. **Branch-scoped authorization stays in the application** (`rbac_*` + `staff_branch_assignment`). The app DB stores no passwords; `identity_user_account` maps internal principals to the Keycloak `sub` (`keycloak_user_id`).

**VI —** Dùng **Keycloak** làm Identity Provider OIDC/OAuth2 **chỉ cho xác thực**. Monolith Spring Boot đóng vai **OAuth2 resource server**, kiểm tra JWT qua JWKS của Keycloak. **Phân quyền theo chi nhánh nằm ở app** (`rbac_*` + `staff_branch_assignment`). App DB không lưu mật khẩu; `identity_user_account` ánh xạ principal nội bộ với `sub` của Keycloak (`keycloak_user_id`).

## Consequences / Hệ quả
**EN —** Positive: offload credential security, MFA, sessions; standard OIDC; SSO across admin/member web. Trade-offs: extra infra to run (Keycloak + its DB); identity becomes external (need mapping + provisioning sync); local dev needs a realm setup.

**VI —** Tích cực: chuyển gánh nặng bảo mật credential, MFA, phiên; chuẩn OIDC; SSO giữa admin/member web. Đánh đổi: thêm hạ tầng vận hành (Keycloak + DB của nó); định danh thành ngoại bộ (cần ánh xạ + đồng bộ provisioning); dev local cần cấu hình realm.

## Rules / Quy tắc
- Realm `gym-platform`; clients `gym-admin-web`, `gym-member-web` (public + PKCE); API = resource server.
- Validate JWT via `issuer-uri` + JWKS; never trust client-supplied roles for branch authorization.
- Keep fine-grained, branch-scoped permission checks in the application layer.
- Supersedes the password-based identity assumption in `data-model/p1-identity-org.md`.
