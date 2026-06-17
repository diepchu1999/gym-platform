# Business Requirement Document - GYM Multi-Branch Platform
Version: v1.0
Purpose: Training Claude Code and serving development.

## Product Vision
Hệ thống chuỗi GYM nhiều chi nhánh tại TP.HCM, hỗ trợ member app/admin/staff portal, membership toàn hệ thống, QR check-in, booking dịch vụ, hợp đồng, payment, inventory, pantry, equipment, CRM, report, audit.

## Global Business Rules
- **BR-001**: Tất cả gói tập chính dùng được toàn bộ chi nhánh.
- **BR-002**: Gói tháng/quý/năm không giới hạn số lần check-in/ngày.
- **BR-003**: Trial miễn phí 7 ngày, yêu cầu KYC CCCD, 1 CCCD chỉ trial 1 lần.
- **BR-004**: Trial chỉ check-in 1 lần/ngày, không giới hạn khung giờ.
- **BR-005**: Trial được 1 buổi group class miễn phí.
- **BR-006**: Group class là gói phụ, bán theo số buổi và cần booking theo lịch.
- **BR-007**: PT 1 kèm 1, mặc định 90 phút/buổi, hoạt động 06:00-22:00.
- **BR-008**: Private room bắt buộc booking theo giờ, mỗi booking tối đa 2 giờ.
- **BR-009**: VIP có private room quota theo giờ/tháng, hết quota có thể trả phí.
- **BR-010**: VIP massage miễn phí 3 lần/tuần, hết lượt trả phí.
- **BR-011**: Hợp đồng không cần duyệt quản lý.
- **BR-012**: Trả góp chỉ áp dụng gói quý/năm qua công ty tài chính.
- **BR-013**: Product partner do gym nhập về, quản lý tồn kho theo chi nhánh.
- **BR-014**: Pantry bán cho tất cả member trong khung 06:00-22:00.
- **BR-015**: Hủy booking trước giờ bắt đầu ít nhất 10 tiếng thì hoàn tiền/hoàn lượt.
- **BR-016**: Khách không đến đúng giờ: CSKH gọi, giữ chỗ tối đa 30 phút, sau đó NO_SHOW.

## Domains
- Identity & RBAC
- Branch
- Staff
- Member
- KYC / Verification
- Membership Package
- Contract
- Payment
- Finance Installment Integration
- QR Check-in
- Booking Engine
- Group Class
- PT Booking
- Private Room
- Massage
- Product / Inventory / POS
- Pantry
- Equipment / Maintenance
- CRM / Customer Care
- Rating / Feedback
- Promotion / Coupon
- Notification
- Report / Analytics
- Audit Log

## Status Catalog
- **Member**: LEAD, REGISTERED, KYC_PENDING, ACTIVE, INACTIVE, SUSPENDED, BLACKLISTED
- **KYC/Verification**: NOT_SUBMITTED, PENDING, APPROVED, REJECTED, REQUEST_RESUBMIT, EXPIRED
- **Membership**: PENDING_PAYMENT, ACTIVE, EXPIRED, SUSPENDED, CANCELLED
- **Contract**: DRAFT, PENDING_SIGNATURE, ACTIVE, EXPIRED, TERMINATED, CANCELLED, SUSPENDED
- **Payment**: UNPAID, PENDING_PAYMENT, PAID, FAILED, EXPIRED, REFUNDED, PARTIALLY_REFUNDED
- **Booking**: DRAFT, PENDING_PAYMENT, CONFIRMED, WAITING_CUSTOMER_CONFIRMATION, CHECKED_IN, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW, EXPIRED, REFUNDED

## Claude Code Instructions
- Build Modular Monolith.
- Use enums for statuses.
- Use transaction/idempotency for booking, payment, QR scan, quota, stock.
- Validate server-side; never trust frontend for price/quota/permission.
- Add audit log for sensitive operations.
