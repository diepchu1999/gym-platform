# ADR-0005: Dùng React + TypeScript, Web trước

> Bản tiếng Việt (canonical). English: [`../../en/decisions/adr-0005-use-react-typescript-web-first.md`](../../en/decisions/adr-0005-use-react-typescript-web-first.md).

## Status
Accepted (Đã chấp nhận)

## Bối cảnh
Sản phẩm ban đầu cần ứng dụng web cho admin, vận hành nhân viên, và có thể là member tự phục vụ. Ứng dụng mobile chưa cần ở giai đoạn kỹ thuật đầu tiên.

## Quyết định
Dùng React + TypeScript cho ứng dụng web frontend.

## Hệ quả
Tích cực:
- An toàn kiểu (type safety) mạnh với TypeScript.
- Hệ sinh thái lớn.
- Phù hợp dashboard admin và màn hình vận hành.
- Dễ build nhanh với kiến trúc theo component.

Đánh đổi:
- Cần kỷ luật kiến trúc frontend khi module lớn dần.
- App mobile sẽ cần một quyết định riêng về sau.

## Quy tắc
- Dùng TypeScript cho file nguồn chính.
- Giữ API model có kiểu.
- Nhóm tính năng theo miền.
- Không đưa phạm vi app mobile vào trừ khi được yêu cầu rõ ràng.
