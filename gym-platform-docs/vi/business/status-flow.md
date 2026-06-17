# Danh mục luồng trạng thái (Status Flow Catalog)

> Bản tiếng Việt (canonical). English: [`../../en/business/status-flow.md`](../../en/business/status-flow.md).
> Lưu ý: tên trạng thái (enum) giữ nguyên tiếng Anh vì khớp với code.

## Trạng thái Member

```text
LEAD -> REGISTERED -> KYC_PENDING -> ACTIVE
ACTIVE -> INACTIVE
ACTIVE -> SUSPENDED
ACTIVE -> BLACKLISTED
```

Ý nghĩa:
- LEAD: khách tiềm năng, chưa đăng ký thành member đầy đủ.
- REGISTERED: đã tạo tài khoản/hồ sơ.
- KYC_PENDING: đang chờ xác minh CCCD/sinh viên.
- ACTIVE: member hợp lệ.
- INACTIVE: không có gói đang hoạt động hoặc không hoạt động lâu.
- SUSPENDED: tạm khóa bởi quy tắc hệ thống/admin.
- BLACKLISTED: bị khóa do vấn đề nghiêm trọng.

## Trạng thái KYC / Verification

```text
NOT_SUBMITTED -> PENDING -> APPROVED
PENDING -> REJECTED
PENDING -> REQUEST_RESUBMIT
APPROVED -> EXPIRED
```

## Trạng thái Trial

```text
KYC_PENDING -> ACTIVE -> EXPIRED
ACTIVE -> CONVERTED
KYC_PENDING -> CANCELLED
```

## Trạng thái Membership

```text
PENDING_PAYMENT -> ACTIVE -> EXPIRED
ACTIVE -> SUSPENDED
ACTIVE -> CANCELLED
```

## Trạng thái Contract

```text
DRAFT -> PENDING_SIGNATURE -> ACTIVE -> EXPIRED
DRAFT -> CANCELLED
PENDING_SIGNATURE -> CANCELLED
ACTIVE -> TERMINATED
ACTIVE -> SUSPENDED
SUSPENDED -> ACTIVE
```

## Trạng thái Payment

```text
UNPAID -> PENDING_PAYMENT -> PAID
PENDING_PAYMENT -> FAILED
PENDING_PAYMENT -> EXPIRED
PAID -> REFUNDED
PAID -> PARTIALLY_REFUNDED
```

## Trạng thái Installment Application

```text
DRAFT -> SUBMITTED -> PENDING_PROVIDER_APPROVAL -> APPROVED -> DISBURSED
PENDING_PROVIDER_APPROVAL -> REJECTED
DRAFT -> CANCELLED
SUBMITTED -> CANCELLED
```

## Trạng thái Booking

```text
DRAFT -> PENDING_PAYMENT -> CONFIRMED -> CHECKED_IN -> IN_PROGRESS -> COMPLETED
CONFIRMED -> WAITING_CUSTOMER_CONFIRMATION -> CHECKED_IN
WAITING_CUSTOMER_CONFIRMATION -> NO_SHOW
PENDING_PAYMENT -> EXPIRED
CONFIRMED -> CANCELLED
CONFIRMED -> NO_SHOW
PAID_BOOKING -> REFUNDED
```

Ghi chú:
- Booking miễn phí/dùng quota có thể bỏ qua PENDING_PAYMENT và vào thẳng CONFIRMED.
- Booking trả phí nên bắt đầu ở PENDING_PAYMENT và hết hạn nếu không thanh toán trong thời gian giữ chỗ.

## Trạng thái Class Session

```text
SCHEDULED -> OPEN_FOR_BOOKING -> FULL
OPEN_FOR_BOOKING -> ONGOING -> COMPLETED
SCHEDULED -> CANCELLED
OPEN_FOR_BOOKING -> CANCELLED
```

## Trạng thái Private Room

```text
AVAILABLE -> BOOKED -> IN_USE -> CLEANING -> AVAILABLE
AVAILABLE -> MAINTENANCE -> AVAILABLE
AVAILABLE -> CLOSED
```

## Trạng thái Equipment

```text
ACTIVE -> NEED_MAINTENANCE -> UNDER_MAINTENANCE -> ACTIVE
ACTIVE -> BROKEN -> UNDER_MAINTENANCE -> ACTIVE
ACTIVE -> RETIRED
BROKEN -> RETIRED
```

## Trạng thái Ticket

```text
NEW -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED
IN_PROGRESS -> WAITING_CUSTOMER
WAITING_CUSTOMER -> IN_PROGRESS
```
