# Hướng dẫn Modular Monolith

> Bản tiếng Việt (canonical). English: [`../../en/architecture/modular-monolith.md`](../../en/architecture/modular-monolith.md).

## Quyết định
Dự án bắt đầu là một Modular Monolith.

## Vì sao
- Các module nghiệp vụ lõi liên kết chặt với nhau.
- Payment, contract, membership, booking và check-in cần nhất quán transaction.
- Một monolith sạch xây nhanh hơn, deploy dễ hơn, và dễ suy luận hơn ở giai đoạn đầu.
- Microservices có thể tách sau, chỉ khi có nhu cầu vận hành thực sự.

## Quy tắc ranh giới module
Mỗi module sở hữu:
- Quy tắc miền của nó.
- Application service của nó.
- Repository port của nó.
- Triển khai Native SQL repository của nó.
- Bảng hoặc nhóm bảng của nó (mỗi module một schema — ADR-0011).

Một module không được dùng thẳng repository của module khác.

## Giao tiếp được phép
- Interface application service.
- Domain event.
- Query service cho view chỉ-đọc xuyên module.
- Value object dùng chung từ `shared`.

## Không được phép
- Controller -> repository.
- Repository Module A query bảng Module B cho logic command.
- Mega-service dùng chung chứa toàn bộ business logic.
- Business rule chỉ giấu trong frontend.

## Tách sau này
Service có thể tách trong tương lai:
- Notification.
- Reporting.
- File/object storage.
- Tích hợp nhà cung cấp thanh toán.
- Tích hợp đối tác.

Giữ các module lõi chung lâu hơn:
- Member.
- KYC.
- Membership.
- Contract.
- Payment core.
- Booking.
- Check-in.
