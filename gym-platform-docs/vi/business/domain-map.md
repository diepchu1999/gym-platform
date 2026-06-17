# Bản đồ miền nghiệp vụ (Business Domain Map)

> Bản tiếng Việt (canonical). English: [`../../en/business/domain-map.md`](../../en/business/domain-map.md).

## Các miền lõi (Core Domains)

1. Identity & RBAC (Định danh & Phân quyền)
2. Branch Management (Quản lý chi nhánh)
3. Staff Management (Quản lý nhân sự)
4. Member Management (Quản lý thành viên)
5. KYC & Verification (KYC & Xác minh)
6. Membership Package (Gói thành viên)
7. Contract Management (Quản lý hợp đồng)
8. Payment (Thanh toán)
9. Finance Installment Integration (Tích hợp trả góp)
10. QR Check-in
11. Booking Engine
12. Group Class (Lớp nhóm)
13. PT Booking
14. Private Room Booking (Đặt phòng riêng)
15. Massage Booking
16. Product / Inventory / POS (Sản phẩm / Kho / POS)
17. Pantry
18. Equipment / Maintenance (Thiết bị / Bảo trì)
19. CRM / Customer Care (CRM / Chăm sóc khách hàng)
20. Rating / Feedback (Đánh giá / Phản hồi)
21. Promotion / Coupon (Khuyến mãi / Mã giảm giá)
22. Notification (Thông báo)
23. Report / Analytics (Báo cáo / Phân tích)
24. Audit Log (Nhật ký kiểm toán)

## Trách nhiệm từng miền

### Identity & RBAC
Quản lý đăng nhập, vai trò, quyền, trạng thái tài khoản và phạm vi truy cập theo chi nhánh.

### Branch Management
Quản lý chi nhánh, phòng, tình trạng dịch vụ, quy tắc mở cửa, sức chứa và cấu hình cấp chi nhánh.

### Staff Management
Quản lý nhân viên như PT, lễ tân, CSKH, lao công, nhân viên giữ xe, nhân viên bảo trì, nhân viên massage, quản lý, kế toán và nhân viên marketing.

### Member Management
Quản lý hồ sơ khách hàng, trạng thái, quan hệ chi nhánh, lịch sử thành viên, lịch sử booking, lịch sử thanh toán và hồ sơ chăm sóc khách hàng.

### KYC & Verification
Quản lý KYC CCCD, xác minh sinh viên và điều kiện trial.

### Membership Package
Quản lý các gói trial, tháng, quý, năm, VIP, sinh viên, class pass, buổi PT, gói massage bổ sung và các gói khác.

### Contract Management
Quản lý tạo hợp đồng, ký/xác nhận, kích hoạt, hết hạn, chấm dứt, tạm ngưng và tài liệu đính kèm.

### Payment
Quản lý thanh toán online, thanh toán tại quầy, hoàn tiền, idempotency cho callback thanh toán, hóa đơn và lịch sử thanh toán.

### Finance Installment Integration
Quản lý hồ sơ trả góp qua công ty tài chính, trạng thái hồ sơ, duyệt/từ chối của nhà cung cấp và trạng thái giải ngân.

### QR Check-in
Quản lý sinh QR token, kiểm tra hợp lệ, chống quét trùng, kiểm tra quy tắc truy cập và lịch sử check-in.

### Booking Engine
Nền tảng booking dùng chung cho PT, group class, private room và massage.

### Group Class
Quản lý loại lớp, buổi học, huấn luyện viên, phòng, sức chứa, sử dụng class pass và điểm danh.

### PT Booking
Quản lý hồ sơ PT, lịch rảnh PT, booking 1 kèm 1 90 phút, thanh toán, hoàn thành và đánh giá.

### Private Room Booking
Quản lý tài nguyên phòng riêng, quota VIP theo tháng, booking theo giờ, trạng thái dọn dẹp/bảo trì và booking trả phí thêm.

### Massage Booking
Quản lý quota miễn phí VIP, nhân viên massage, phòng massage, booking, booking trả phí thêm và hoàn thành.

### Product / Inventory / POS
Quản lý sản phẩm, nhãn hàng đối tác, tồn kho theo chi nhánh, đơn nhập, chuyển kho, đơn bán, trả hàng và điều chỉnh.

### Pantry
Quản lý đồ ăn/uống pantry, giờ hoạt động, tồn kho, hạn dùng, batch và luồng đặt hàng.

### Equipment / Maintenance
Quản lý tài sản thiết bị, trạng thái, lịch bảo trì, báo hỏng, phiếu bảo trì và lịch sử chi phí.

### CRM / Customer Care
Quản lý lead, follow-up trial, follow-up gia hạn, gọi no-show, ticket, khiếu nại và ghi chú chăm sóc.

### Rating / Feedback
Quản lý phản hồi cho PT, lớp, chi nhánh, dịch vụ, thiết bị và hỗ trợ khách hàng.

### Promotion / Coupon
Quản lý coupon, chiến dịch, giảm giá sinh viên, giới thiệu và khuyến mãi gói.

### Notification
Quản lý email, SMS, push, Zalo, nhắc booking, nhắc thanh toán, nhắc hết hạn và thông báo chiến dịch.

### Report / Analytics
Quản lý báo cáo vận hành, doanh thu, chi nhánh, thành viên, booking, PT, tồn kho và chuyển đổi.

### Audit Log
Ghi nhận các hành động nghiệp vụ nhạy cảm như hợp đồng, thanh toán, kích hoạt gói, hoàn tiền, thay đổi quyền, duyệt KYC và điều chỉnh tồn kho.
