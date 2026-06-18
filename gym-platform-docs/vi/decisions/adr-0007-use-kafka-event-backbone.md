# ADR-0007: Sự kiện bất đồng bộ — Outbox trước, Kafka sau

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0007-use-kafka-event-backbone.md`](../../en/decisions/adr-0007-use-kafka-event-backbone.md).

## Status
Proposed (Đề xuất) — Outbox áp dụng ngay; Kafka hoãn lại.

## Bối cảnh
Tác vụ phụ (thông báo, audit, báo cáo, CSKH follow-up) không được gây nguy hiểm cho luồng giao dịch lõi (thanh toán, booking, check-in, quota, kho) — vốn cần nhất quán mạnh ở PostgreSQL. Ta muốn ghi nhận domain event đáng tin cậy ngay từ đầu, nhưng dựng broker Kafka ngay bây giờ tốn chi phí vận hành khi consumer async chưa thực sự cần.

## Quyết định
Áp dụng **Transactional Outbox ngay**: module nghiệp vụ ghi event vào bảng `outbox_event` trong cùng transaction DB với thay đổi nghiệp vụ. **Kafka hoãn lại ("later")**: khi cần phát async, thêm **Outbox Relay** (polling trước, **Debezium CDC** sau) publish event đã commit lên **Kafka** cho consumer idempotent. Quyết định lõi cần nhất quán vẫn transactional trong PostgreSQL, không bao giờ đưa vào Kafka.

## Hệ quả
Tích cực: event bền vững ngay từ đầu; chưa phải vận hành broker; outbox là điểm nối ổn định nên thêm Kafka sau không phá vỡ; giữ nhất quán. Đánh đổi: phải xây relay khi có Kafka; consumer cần idempotency/dedupe; projection trở thành nhất quán-cuối khi Kafka chạy.

## Quy tắc
- Ghi `outbox_event` trong **cùng transaction** với thay đổi aggregate (không có event nếu chưa commit).
- Mỗi event có id duy nhất; consumer tương lai khử trùng (bảng idempotency).
- KHÔNG đưa quyết định payment/booking/quota/stock vào eventing — giữ transactional trong PostgreSQL (xem `CLAUDE.md` Race Condition Protection).
- Topic (khi có Kafka) key theo aggregate id để giữ thứ tự; lan truyền trace context qua header (ADR-0008).
- Thay thế cách diễn đạt cũ "Kafka là backbone tức thì".
