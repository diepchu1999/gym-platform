# ADR-0008: Quan sát hệ thống — Prometheus, Grafana, Zipkin

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0008-observability-prometheus-grafana-zipkin.md`](../../en/decisions/adr-0008-observability-prometheus-grafana-zipkin.md).

## Status
Proposed (Đề xuất)

## Bối cảnh
Nền tảng đa chi nhánh, nhiều giao dịch, có sự kiện bất đồng bộ cần khả năng nhìn thấy độ trễ, thông lượng, tỷ lệ lỗi, DB pool, độ trễ Kafka, và luồng request đầu-cuối (HTTP → DB → Kafka → consumer) để vận hành và debug an toàn.

## Quyết định
Dùng **Micrometer** (Spring Boot Actuator) expose metrics tại `/actuator/prometheus`, được **Prometheus** scrape và **Grafana** hiển thị/cảnh báo. Dùng **Micrometer Tracing** (cầu nối Brave/OpenTelemetry) xuất span sang **Zipkin**, lan truyền trace context qua HTTP và Kafka. Log có cấu trúc, gắn `traceId`/`spanId`.

## Hệ quả
Tích cực: observability chuẩn Spring, trace đầu-cuối, dashboard & cảnh báo. Đánh đổi: thêm hạ tầng (Prometheus/Grafana/Zipkin), phải tinh chỉnh sampling/retention, chút overhead runtime.

## Quy tắc
- Chỉ expose endpoint actuator `health`, `info`, `prometheus` ra ngoài; phần còn lại phải bảo mật.
- Trace context phải đi qua header producer/consumer Kafka (liên quan ADR-0007).
- Định nghĩa Grafana dashboard cho: JVM, độ trễ/lỗi HTTP, DB pool, độ trễ consumer Kafka, KPI nghiệp vụ.
- Tinh chỉnh sampling trace cho production; không log dữ liệu nhạy cảm (CCCD, token) trong span/log.
