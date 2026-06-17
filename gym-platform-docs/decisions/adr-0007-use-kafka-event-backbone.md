# ADR-0007: Use Kafka as Async Event Backbone / Dùng Kafka làm Xương sống Sự kiện Bất đồng bộ

## Status
Proposed / Đề xuất

## Context / Bối cảnh
**EN —** Many side-effects (notifications, audit streaming, CRM follow-up, reporting projections) should not block or endanger core transactional flows (payment, booking, check-in, quota, stock). These flows require strong consistency and race-condition protection in PostgreSQL. We also want loose coupling between modules and a path to extract services later (notification, reporting).

**VI —** Nhiều tác vụ phụ (thông báo, luồng audit, CSKH follow-up, projection báo cáo) không nên chặn hoặc gây nguy hiểm cho luồng giao dịch lõi (thanh toán, booking, check-in, quota, kho). Các luồng này cần nhất quán mạnh và chống race condition trong PostgreSQL. Ta cũng muốn giảm phụ thuộc giữa các module và có đường tách service sau này (notification, reporting).

## Decision / Quyết định
**EN —** Use **Apache Kafka** as the asynchronous event backbone, fed via the **Transactional Outbox** pattern: business modules append events to `outbox_event` within the same DB transaction as the business change; a relay publishes committed events to Kafka. Core consistency-critical logic stays **in-process with PostgreSQL transactions / atomic SQL / constraints** — Kafka is never in the race-condition critical path; it carries resulting facts only.

**VI —** Dùng **Apache Kafka** làm xương sống sự kiện bất đồng bộ, cấp dữ liệu qua mẫu **Transactional Outbox**: module nghiệp vụ ghi event vào `outbox_event` trong cùng transaction DB với thay đổi nghiệp vụ; một relay publish các event đã commit lên Kafka. Logic lõi cần nhất quán vẫn **in-process với transaction PostgreSQL / atomic SQL / constraint** — Kafka không bao giờ nằm trong đường găng race condition; chỉ chở sự kiện kết quả.

## Consequences / Hệ quả
**EN —** Positive: decoupled side-effects, resilience, replay, future service extraction. Trade-offs: operational complexity (broker), outbox relay to build, consumers must be idempotent, eventual consistency for projections.

**VI —** Tích cực: tách tác vụ phụ, bền bỉ, replay được, sẵn cho tách service. Đánh đổi: phức tạp vận hành (broker), phải xây outbox relay, consumer phải idempotent, projection chỉ nhất quán cuối cùng.

## Rules / Quy tắc
- Topics by aggregate, keyed by aggregate id for ordering (`payment.events`, `booking.events`, ...).
- Every event has a unique id; consumers dedupe (idempotency table).
- Do NOT move payment/booking/quota/stock decisions into Kafka — keep them transactional in PostgreSQL (see `CLAUDE.md` Race Condition Protection).
- Start with a polling outbox publisher; consider Debezium CDC later.
- Propagate trace context (W3C / B3) through Kafka headers for end-to-end tracing (ADR-0008).
