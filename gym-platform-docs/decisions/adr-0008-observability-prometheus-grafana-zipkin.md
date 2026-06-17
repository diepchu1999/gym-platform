# ADR-0008: Observability — Prometheus, Grafana, Zipkin / Quan sát hệ thống

## Status
Proposed / Đề xuất

## Context / Bối cảnh
**EN —** A multi-branch, transaction-heavy platform with async eventing needs visibility into latency, throughput, error rates, DB pool, Kafka lag, and end-to-end request flows (HTTP → DB → Kafka → consumers) to operate and debug safely.

**VI —** Nền tảng đa chi nhánh, nhiều giao dịch, có sự kiện bất đồng bộ cần khả năng nhìn thấy độ trễ, thông lượng, tỷ lệ lỗi, DB pool, độ trễ Kafka, và luồng request đầu-cuối (HTTP → DB → Kafka → consumer) để vận hành và debug an toàn.

## Decision / Quyết định
**EN —** Use **Micrometer** (Spring Boot Actuator) to expose metrics at `/actuator/prometheus`, scraped by **Prometheus** and visualized/alerted in **Grafana**. Use **Micrometer Tracing** (Brave/OpenTelemetry bridge) to export spans to **Zipkin**, propagating trace context across HTTP and Kafka. Logs are structured and enriched with `traceId`/`spanId`.

**VI —** Dùng **Micrometer** (Spring Boot Actuator) expose metrics tại `/actuator/prometheus`, được **Prometheus** scrape và **Grafana** hiển thị/cảnh báo. Dùng **Micrometer Tracing** (cầu nối Brave/OpenTelemetry) xuất span sang **Zipkin**, lan truyền trace context qua HTTP và Kafka. Log có cấu trúc, gắn `traceId`/`spanId`.

## Consequences / Hệ quả
**EN —** Positive: standard Spring observability, end-to-end traces, dashboards & alerts. Trade-offs: more infra (Prometheus/Grafana/Zipkin), sampling/retention tuning, minor runtime overhead.

**VI —** Tích cực: observability chuẩn Spring, trace đầu-cuối, dashboard & cảnh báo. Đánh đổi: thêm hạ tầng (Prometheus/Grafana/Zipkin), phải tinh chỉnh sampling/retention, chút overhead runtime.

## Rules / Quy tắc
- Expose only `health`, `info`, `prometheus` actuator endpoints publicly; secure the rest.
- Trace context must cross Kafka producer/consumer headers (ties to ADR-0007).
- Define Grafana dashboards for: JVM, HTTP latency/error, DB pool, Kafka consumer lag, business KPIs.
- Tune trace sampling for production; do not log sensitive data (CCCD, tokens) in spans/logs.
