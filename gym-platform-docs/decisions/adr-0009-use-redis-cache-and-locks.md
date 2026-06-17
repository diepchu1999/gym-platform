# ADR-0009: Use Redis for Cache and Short-Lived Locks / Dùng Redis cho Cache và Lock ngắn hạn

## Status
Proposed / Đề xuất

## Context / Bối cảnh
**EN —** QR check-in needs a short-lived, one-time token (TTL 30–60s), duplicate-scan suppression within a few minutes, and the platform needs rate limiting and caching of hot read-mostly data. Doing all of this purely in PostgreSQL adds write churn and latency for inherently ephemeral state.

**VI —** Check-in QR cần token một lần, sống ngắn (TTL 30–60s), chặn quét trùng trong vài phút, và hệ thống cần rate limit + cache dữ liệu đọc-nhiều nóng. Làm tất cả thuần bằng PostgreSQL gây nhiều ghi và độ trễ cho trạng thái vốn ephemeral.

## Decision / Quyết định
**EN —** Use **Redis** for **ephemeral, performance-critical** concerns: **QR token TTL**, **one-time nonce**, **duplicate-scan lock**, and **rate limiting**, plus optional caching. **Redis does NOT replace durable database constraints.** Authoritative race protection — 1 trial per CCCD, payment idempotency, class booking uniqueness, stock/quota non-negativity, QR nonce final consumption — remains enforced in **PostgreSQL** (constraints, atomic SQL, transactions). Redis is a fast first gate; PostgreSQL is the source of truth.

**VI —** Dùng **Redis** cho phần **ephemeral, nhạy hiệu năng**: **QR token TTL**, **nonce một lần**, **lock chống quét trùng**, và **rate limit**, kèm cache tùy chọn. **Redis KHÔNG thay thế constraint bền vững của DB.** Bảo vệ race condition có thẩm quyền — 1 trial/CCCD, idempotency thanh toán, uniqueness đặt lớp, kho/quota không âm, tiêu thụ nonce QR cuối cùng — vẫn thực thi ở **PostgreSQL** (constraint, atomic SQL, transaction). Redis là cổng nhanh đầu tiên; PostgreSQL là nguồn sự thật.

## Consequences / Hệ quả
**EN —** Positive: low latency for ephemeral state, natural TTL, simple distributed short locks, rate limiting. Trade-offs: extra infra; Redis is not the system of record (data may be evicted); must avoid relying on Redis alone for correctness-critical uniqueness.

**VI —** Tích cực: độ trễ thấp cho trạng thái ephemeral, TTL tự nhiên, lock ngắn phân tán đơn giản, rate limit. Đánh đổi: thêm hạ tầng; Redis không phải system-of-record (dữ liệu có thể bị evict); không được dựa hoàn toàn vào Redis cho uniqueness mang tính đúng-đắn.

## Rules / Quy tắc
- Redis use cases: QR token TTL + one-time nonce, duplicate-scan window lock, rate limiting, read cache.
- Every correctness-critical invariant MUST also be backed by a PostgreSQL constraint/atomic update.
- Locks are short-lived and time-bounded (TTL) to avoid deadlocks on crash.
- Do not store sensitive data (CCCD, tokens) in Redis beyond required TTL.
