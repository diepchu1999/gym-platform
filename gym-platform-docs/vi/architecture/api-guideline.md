# Hướng dẫn API

> Bản tiếng Việt (canonical). English: [`../../en/architecture/api-guideline.md`](../../en/architecture/api-guideline.md).

## Phong cách
Dùng API RESTful với request/response JSON.

Dùng danh từ số nhiều cho resource:

```text
/members
/packages
/contracts
/payments
/checkins
/bookings
/class-sessions
/trainers
/private-rooms
/products
/equipment
```

## Cấu trúc Response
Response thành công chuẩn hiện tại:

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

Response lỗi chuẩn hiện tại:

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "BOOKING_SLOT_UNAVAILABLE",
    "message": "Selected slot is no longer available"
  }
}
```

Nếu cần thêm `meta`, `message`, hoặc `details`, phải cập nhật `ApiResponse`/`ApiError` và tài liệu trong cùng thay đổi.

## Phân trang
Dùng cursor pagination cho danh sách lớn khi có thể.

Với danh sách admin nhỏ, offset pagination ban đầu có thể chấp nhận được.

## Idempotency
Dùng idempotency key cho:
- Payment callback.
- Tạo booking từ app.
- Quét QR check-in.
- Tạo order.

## Mã HTTP thường dùng
- 200: thành công.
- 201: đã tạo.
- 204: không có nội dung.
- 400: lỗi validation/input nghiệp vụ.
- 401: chưa xác thực.
- 403: bị cấm.
- 404: không tìm thấy.
- 409: conflict/race condition/xung đột nghiệp vụ.
- 422: JSON hợp lệ nhưng business rule fail.
- 500: lỗi server bất ngờ.

## Ví dụ Endpoint
Member:
- `POST /members`
- `GET /members/{id}`
- `PATCH /members/{id}`

KYC:
- `POST /members/{id}/kyc-requests`
- `POST /kyc-requests/{id}/approve`
- `POST /kyc-requests/{id}/reject`

Booking:
- `POST /bookings`
- `POST /bookings/{id}/cancel`
- `POST /bookings/{id}/check-in`
- `POST /bookings/{id}/complete`

Check-in:
- `POST /checkins/qr-tokens`
- `POST /checkins/scan`

Payment:
- `POST /orders`
- `POST /payments`
- `POST /payments/callback/{provider}`
