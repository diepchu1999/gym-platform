# ADR-0009: Dùng Redis cho Cache và Lock ngắn hạn

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0009-use-redis-cache-and-locks.md`](../../en/decisions/adr-0009-use-redis-cache-and-locks.md).

## Status
Proposed (Đề xuất)

## Bối cảnh
Check-in QR cần token một lần, sống ngắn (TTL 30–60s), chặn quét trùng trong vài phút, và hệ thống cần rate limit + cache dữ liệu đọc-nhiều nóng. Làm tất cả thuần bằng PostgreSQL gây nhiều ghi và độ trễ cho trạng thái vốn ephemeral.

## Quyết định
Dùng **Redis** cho phần **ephemeral, nhạy hiệu năng**: **QR token TTL**, **nonce một lần**, **lock chống quét trùng**, và **rate limit**, kèm cache tùy chọn. **Redis KHÔNG thay thế constraint bền vững của DB.** Bảo vệ race condition có thẩm quyền — 1 trial/CCCD, idempotency thanh toán, uniqueness đặt lớp, kho/quota không âm, tiêu thụ nonce QR cuối cùng — vẫn thực thi ở **PostgreSQL** (constraint, atomic SQL, transaction). Redis là cổng nhanh đầu tiên; PostgreSQL là nguồn sự thật.

## Hệ quả
Tích cực: độ trễ thấp cho trạng thái ephemeral, TTL tự nhiên, lock ngắn phân tán đơn giản, rate limit. Đánh đổi: thêm hạ tầng; Redis không phải system-of-record (dữ liệu có thể bị evict); không được dựa hoàn toàn vào Redis cho uniqueness mang tính đúng-đắn.

## Quy tắc
- Use case Redis: QR token TTL + nonce một lần, lock cửa sổ chống quét trùng, rate limit, read cache.
- Mọi bất biến mang tính đúng-đắn PHẢI có thêm constraint/atomic update ở PostgreSQL.
- Lock ngắn hạn và có giới hạn thời gian (TTL) để tránh deadlock khi crash.
- Không lưu dữ liệu nhạy cảm (CCCD, token) trong Redis quá TTL cần thiết.
