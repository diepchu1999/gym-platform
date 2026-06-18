# Tổng quan kiến trúc

> Bản tiếng Việt (canonical). English: [`../../en/architecture/architecture-overview.md`](../../en/architecture/architecture-overview.md).

## Định hướng
Dự án dùng Spring Boot Modular Monolith cho giai đoạn đầu.

Miền nghiệp vụ rộng nhưng liên kết chặt. Member, membership, contract, payment, booking, check-in, quota và inventory cần nhất quán transaction. Bắt đầu bằng microservices sẽ thêm độ phức tạp distributed transaction và deployment quá sớm không cần thiết.

## Hệ thống tổng thể

```text
React + TypeScript Web Application
        |
        v
Spring Boot Modular Monolith API
        |
        v
PostgreSQL
```

Hạ tầng hỗ trợ tương lai có thể gồm background jobs và object-storage/CDN có quản lý.

> **Hạ tầng hỗ trợ đã duyệt** (xem [`solution-architecture.md`](solution-architecture.md) + ADR-0006…0010). Vẫn là **Modular Monolith** (một đơn vị triển khai):
> - **Keycloak** — xác thực / OIDC identity provider (app lo phân quyền theo chi nhánh).
> - **PostgreSQL** — nguồn sự thật (transaction, atomic SQL, constraint) + `outbox_event`.
> - **Redis** — cache + lock ngắn hạn: QR token TTL, nonce một lần, lock chống quét trùng, rate limit; uniqueness bền vững vẫn ở PostgreSQL (ADR-0009).
> - **Object Storage (S3)** — tài liệu/ảnh (CCCD, contract PDF, hóa đơn, media); DB chỉ lưu object key (ADR-0010).
> - **Transactional Outbox** dùng ngay; **Kafka** async backbone **sau** (ADR-0007).
> - **Prometheus + Grafana** (metrics) và **Zipkin** (tracing); **Loki/ELK** log sau (ADR-0008).

## Phong cách kiến trúc Backend
Dùng nguyên tắc SOLID và phong cách Clean/Hexagonal thực dụng.

Luồng ưu tiên:

```text
Controller
-> Application Use Case / Application Service
-> Domain Model / Domain Service
-> Port / Repository Interface
-> Infrastructure Adapter / Native SQL Repository
-> PostgreSQL
```

## Cấu trúc module Backend

```text
com.gym
 ├── identity
 ├── branch
 ├── staff
 ├── member
 ├── kyc
 ├── membership
 ├── contract
 ├── payment
 ├── finance
 ├── checkin
 ├── booking
 ├── groupclass
 ├── pt
 ├── privateroom
 ├── massage
 ├── inventory
 ├── pantry
 ├── equipment
 ├── crm
 ├── rating
 ├── promotion
 ├── notification
 ├── report
 ├── audit
 └── shared
```

## Trách nhiệm các tầng

### Controller
- Xử lý mapping HTTP.
- Validate hình dạng request/input cơ bản.
- Chuyển request DTO sang command/query object.
- Không chứa business logic.
- Không gọi repository trực tiếp.

### Application Service / Use Case
- Sở hữu việc điều phối use case.
- Sở hữu ranh giới transaction.
- Gọi domain service và repository port.
- Phối hợp các module khác qua interface/event.

### Domain
- Sở hữu business rule.
- Chứa entity, value object, domain policy và domain service.
- Không biết HTTP, SQL, hay chi tiết framework.

### Repository Port
- Interface mà application/domain cần.
- Mô tả nhu cầu persistence mà không lộ chi tiết SQL.

### Native SQL Repository Adapter
- Hiện thực repository port bằng Native SQL.
- Ưu tiên `NamedParameterJdbcTemplate` ban đầu trừ khi ADR sau đổi.
- Dùng parameter binding.
- Xử lý row mapping.
- Không chứa quyết định nghiệp vụ ngoài việc persistence.

## Database
PostgreSQL là nguồn sự thật.

Dùng Native SQL cho persistence nghiệp vụ. Không dùng JPA repository trừ khi có quyết định rõ ràng sau này đổi điều này.

Dùng Flyway cho migration schema trừ khi quyết định sau đổi.

## Frontend
Frontend dùng React + TypeScript, web trước.

Sản phẩm ban đầu:
- Admin web.
- Màn hình staff trong admin hoặc route group riêng.
- Member web sau nếu cần.

App mobile không nằm trong nền tảng kỹ thuật đầu tiên.

## Ứng viên tách service sau này
Chỉ tách service sau khi có áp lực scale hoặc quy mô đội ngũ thực sự.

Dễ tách hơn sau:
- Notification service.
- Reporting service.
- File service.
- Payment integration service.
- Partner/product integration service.

Các module lõi nên giữ trong monolith lâu hơn:
- Member.
- Membership.
- Contract.
- Booking.
- Check-in.
- Payment core.
