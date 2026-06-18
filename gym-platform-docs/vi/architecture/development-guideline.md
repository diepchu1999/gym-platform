# Hướng dẫn Phát triển

> Bản tiếng Việt (canonical). English: [`../../en/architecture/development-guideline.md`](../../en/architecture/development-guideline.md).

## Tài liệu là nguồn sự thật
Quy tắc nghiệp vụ và kỹ thuật phải nằm trong tài liệu Markdown.

Khi một quyết định kỹ thuật thay đổi:
1. Cập nhật `CLAUDE.md` nếu ảnh hưởng hành vi toàn cục.
2. Cập nhật doc kiến trúc/module liên quan.
3. Tạo hoặc cập nhật ADR nếu quyết định mang tính kiến trúc.
4. Cập nhật dev notes khi kết thúc một phiên phát triển.

Không để quyết định quan trọng chỉ nằm trong chat.

## Trước khi code
Claude Code hoặc bất kỳ dev nào nên:
1. Đọc `CLAUDE.md`.
2. Đọc doc nghiệp vụ/module liên quan.
3. Inspect code hiện tại.
4. Đề xuất plan ngắn gọn.
5. Chờ duyệt nếu task rộng hoặc rủi ro.

## Quy tắc Backend
- Chỉ Spring Boot cho backend trừ khi đổi rõ ràng.
- Modular Monolith trước.
- Nguyên tắc SOLID.
- Phong cách Clean/Hexagonal.
- Persistence Native SQL.
- Database PostgreSQL.
- Không đưa JPA repository vào trừ khi được duyệt.
- Không để business logic trong controller.
- Không expose row DB trực tiếp ra API response.
- Không query thẳng bảng của module khác từ module sai mà không có pattern query-service được duyệt.

## Quy tắc Frontend
- React + TypeScript.
- Web trước.
- Chưa có mobile ban đầu.
- API model có kiểu.
- Nhóm code tính năng theo miền.

## Kỷ luật Git
- Commit doc cùng code khi hành vi thay đổi.
- Dùng commit message có ý nghĩa.
- Không commit secret.
- Giữ `.env.example` cập nhật.

## Migration
- Dùng Flyway trừ khi ADR sau đổi.
- Thay đổi DB mới nghĩa là migration mới.
- Không sửa migration đã apply.

## Kỷ luật Testing
Mỗi tính năng kèm test phù hợp:
- Unit test cho business rule.
- Integration test cho repository/use case khi khả thi.
- API test cho endpoint quan trọng.
- Test nhạy race cho booking/payment/quota/stock khi khả thi.

## Định nghĩa "Hoàn thành" (Done)
Một task hoàn thành khi:
- Business rule đã implement.
- Validation đã implement.
- Đã cân nhắc race condition.
- Đã thêm test hoặc hoãn lại có lý do rõ ràng.
- Đã cập nhật doc nếu hành vi hoặc quyết định kỹ thuật thay đổi.
