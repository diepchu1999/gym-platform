# ADR-0001: Dùng Modular Monolith trước

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0001-use-modular-monolith.md`](../../en/decisions/adr-0001-use-modular-monolith.md).

## Status
Accepted (Đã chấp nhận)

## Bối cảnh
Nền tảng GYM có rất nhiều miền: member, KYC, membership, contract, payment, check-in, booking, PT, group class, private room, massage, inventory, pantry, equipment, CRM và báo cáo.

Dự án về sau có thể đông người dùng, nhưng giai đoạn đầu cần phát triển nhanh, nhất quán mạnh và khám phá miền rõ ràng.

## Quyết định
Dùng Spring Boot Modular Monolith cho giai đoạn đầu.

## Hệ quả
Tích cực:
- Phát triển nhanh hơn.
- Cài đặt local dễ hơn.
- Quản lý transaction cho luồng lõi dễ hơn.
- Độ phức tạp vận hành thấp hơn.
- Trợ lý AI dễ hiểu toàn hệ thống hơn.

Tiêu cực:
- Cần kỷ luật để tránh coupling giữa các module.
- Ban đầu chỉ có một đơn vị triển khai.
- Cần tài liệu và ranh giới rõ ràng.

## Ứng viên tách service trong tương lai
Các service tiềm năng: Notification, Reporting, File storage, Payment integration, Partner integration.

Các miền lõi nên giữ chung cho tới khi có lý do thực sự về scale hoặc ranh giới đội ngũ để tách.
