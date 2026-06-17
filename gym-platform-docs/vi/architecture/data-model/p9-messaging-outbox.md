# P9 — Messaging: Transactional Outbox (now) + Consumer Idempotency

Nguồn: ADR-0007 (Outbox now / Kafka later), `solution-architecture.md` §7.

## Phạm vi
`outbox_event` (ghi ngay từ đầu), `processed_event` (idempotency cho consumer khi Kafka có). Đây là "đường ray" để thêm Kafka sau mà không phá vỡ.

## `outbox_event`
| Cột | Kiểu | Ràng buộc | Ghi chú |
|---|---|---|---|
| id | BIGINT | PK identity | thứ tự ghi |
| event_id | UUID | NOT NULL DEFAULT gen_random_uuid(), UNIQUE | định danh toàn cục (pgcrypto, V001) |
| aggregate_type | VARCHAR(40) | NOT NULL | 'payment','booking','contract',... |
| aggregate_id | BIGINT | NOT NULL | id aggregate (key để giữ thứ tự theo Kafka sau) |
| event_type | VARCHAR(60) | NOT NULL | 'PaymentPaid','BookingConfirmed',... |
| payload | JSONB | NOT NULL | dữ liệu sự kiện |
| occurred_at | timestamptz | NOT NULL DEFAULT now() | |
| status | VARCHAR(15) | NOT NULL DEFAULT 'PENDING', CHECK IN ('PENDING','PUBLISHED','FAILED') | |
| published_at | timestamptz | NULL | |
| retry_count | INT | NOT NULL DEFAULT 0 | |
| created_at | timestamptz | NOT NULL DEFAULT now() | |

- **Quy tắc cốt lõi**: ghi `outbox_event` **trong cùng transaction** với thay đổi nghiệp vụ (không có event nếu chưa commit).
- Index: `(status, occurred_at)` cho relay polling; `(aggregate_type, aggregate_id)`.
- **Now**: có thể có dispatcher in-process đọc PENDING để xử lý handler nội bộ (vd tạo `notification_message`).
- **Later**: Outbox Relay (polling → Debezium CDC) publish lên Kafka, set `PUBLISHED`.

## `processed_event` (idempotency consumer — khi có Kafka)
id · consumer_name VARCHAR(60) NOT NULL · event_id UUID NOT NULL · processed_at timestamptz NOT NULL DEFAULT now().
- `UNIQUE(consumer_name, event_id)` → consumer xử lý đúng-một-lần (chèn trước khi xử lý; trùng ⇒ bỏ qua).

## Vì sao tách "now/later"
- `outbox_event` đảm bảo **không mất sự kiện** ngay cả khi chưa dựng Kafka.
- Khi cần async backbone, chỉ thêm relay + consumer, **không sửa** logic nghiệp vụ lõi.
- Quyết định lõi (payment/booking/quota/stock) vẫn transactional ở PostgreSQL — Kafka chỉ chở sự kiện kết quả.

## Migration dự kiến
`V025__outbox.sql` (outbox_event, processed_event). (Có thể đẩy `outbox_event` lên sớm hơn nếu module nào cần phát event trước.)
