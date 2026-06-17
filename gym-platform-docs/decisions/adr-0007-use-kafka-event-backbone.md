# ADR-0007: Async Eventing — Transactional Outbox now, Kafka later / Sự kiện bất đồng bộ — Outbox trước, Kafka sau

## Status
Proposed / Đề xuất — Outbox adopted now; Kafka deferred. / Outbox áp dụng ngay; Kafka hoãn lại.

## Context / Bối cảnh
**EN —** Side-effects (notifications, audit, reporting, CRM follow-up) must not endanger core transactional flows (payment, booking, check-in, quota, stock), which need strong consistency in PostgreSQL. We want to capture domain events reliably from day one, but introducing a Kafka broker now adds operational cost before async consumers are actually needed.

**VI —** Tác vụ phụ (thông báo, audit, báo cáo, CSKH follow-up) không được gây nguy hiểm cho luồng giao dịch lõi (thanh toán, booking, check-in, quota, kho) — vốn cần nhất quán mạnh ở PostgreSQL. Ta muốn ghi nhận domain event đáng tin cậy ngay từ đầu, nhưng dựng broker Kafka ngay bây giờ tốn chi phí vận hành khi consumer async chưa thực sự cần.

## Decision / Quyết định
**EN —** Adopt the **Transactional Outbox now**: business modules append events to an `outbox_event` table within the same DB transaction as the business change. **Kafka is deferred ("later")**: when async delivery is needed, add an **Outbox Relay** (polling first, **Debezium CDC** later) that publishes committed events to **Kafka** for idempotent consumers. Core consistency-critical decisions remain transactional in PostgreSQL and are never moved into Kafka.

**VI —** Áp dụng **Transactional Outbox ngay**: module nghiệp vụ ghi event vào bảng `outbox_event` trong cùng transaction DB với thay đổi nghiệp vụ. **Kafka hoãn lại ("later")**: khi cần phát async, thêm **Outbox Relay** (polling trước, **Debezium CDC** sau) publish event đã commit lên **Kafka** cho consumer idempotent. Quyết định lõi cần nhất quán vẫn transactional trong PostgreSQL, không bao giờ đưa vào Kafka.

## Consequences / Hệ quả
**EN —** Positive: events are durable from day one; no broker to operate yet; the outbox is a stable seam, so adding Kafka later is non-breaking; consistency preserved. Trade-offs: a relay must be built when Kafka arrives; consumers need idempotency/dedupe; projections become eventually consistent once Kafka is live.

**VI —** Tích cực: event bền vững ngay từ đầu; chưa phải vận hành broker; outbox là điểm nối ổn định nên thêm Kafka sau không phá vỡ; giữ nhất quán. Đánh đổi: phải xây relay khi có Kafka; consumer cần idempotency/dedupe; projection trở thành nhất quán-cuối khi Kafka chạy.

## Rules / Quy tắc
- `outbox_event` row is written in the **same transaction** as the aggregate change (no event without commit).
- Each event has a unique id; future consumers dedupe (idempotency table).
- Do NOT move payment/booking/quota/stock decisions into eventing — keep them transactional in PostgreSQL (`CLAUDE.md` Race Condition Protection).
- Topics (when Kafka lands) keyed by aggregate id for ordering; propagate trace context via headers (ADR-0008).
- Supersedes the earlier "Kafka as immediate backbone" framing.
