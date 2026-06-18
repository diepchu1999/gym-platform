# ADR-0008: Observability — Prometheus, Grafana, Zipkin

> English version. Vietnamese (canonical): [`../../vi/decisions/adr-0008-observability-prometheus-grafana-zipkin.md`](../../vi/decisions/adr-0008-observability-prometheus-grafana-zipkin.md).

## Status
Proposed

## Context
A multi-branch, transaction-heavy platform with async eventing needs visibility into latency, throughput, error rates, DB pool, Kafka lag, and end-to-end request flows (HTTP → DB → Kafka → consumers) to operate and debug safely.

## Decision
Use **Micrometer** (Spring Boot Actuator) to expose metrics at `/actuator/prometheus`, scraped by **Prometheus** and visualized/alerted in **Grafana**. Use **Micrometer Tracing** (Brave/OpenTelemetry bridge) to export spans to **Zipkin**, propagating trace context across HTTP and Kafka. Logs are structured and enriched with `traceId`/`spanId`.

## Consequences
Positive: standard Spring observability, end-to-end traces, dashboards & alerts. Trade-offs: more infra (Prometheus/Grafana/Zipkin), sampling/retention tuning, minor runtime overhead.

## Rules
- Expose only `health`, `info`, `prometheus` actuator endpoints publicly; secure the rest.
- Trace context must cross Kafka producer/consumer headers (ties to ADR-0007).
- Define Grafana dashboards for: JVM, HTTP latency/error, DB pool, Kafka consumer lag, business KPIs.
- Tune trace sampling for production; do not log sensitive data (CCCD, tokens) in spans/logs.
