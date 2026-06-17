# Module Schemas & Cross-Module References

Microservices-ready data layout: **mỗi module 1 PostgreSQL schema**, **KHÔNG có FK chéo module**. Ref: ADR-0011.

> Trong các file phase (`p1`..`p9`), bất kỳ "FK" trỏ tới bảng của module khác đều là **logical reference** (cột BIGINT, KHÔNG có DB FK). FK chỉ tồn tại trong cùng schema/module.

## Schema map

| Schema | Bảng |
|---|---|
| `identity` | identity_user_account, rbac_role, rbac_permission, rbac_role_permission |
| `branch` | branch_branch, branch_room |
| `staff` | staff_staff, staff_branch_assignment |
| `member` | member_profile |
| `kyc` | kyc_request, student_verification, trial_usage |
| `membership` | package_plan, membership |
| `contract` | contract |
| `payment` | customer_order, customer_order_item, payment, refund |
| `finance` | installment_application |
| `checkin` | checkin_token, checkin_log |
| `booking` | booking, booking_resource_slot, booking_hold, booking_event |
| `groupclass` | class_type, class_session, class_pass, class_booking |
| `pt` | trainer_profile, trainer_availability, pt_booking, pt_rating |
| `privateroom` | private_room, private_room_quota, private_room_booking |
| `massage` | massage_service, massage_room, massage_staff_availability, massage_weekly_usage, massage_booking |
| `inventory` | product_partner, product, inventory_stock, stock_movement, product_batch, purchase_order, purchase_order_item, stock_transfer, stock_adjustment |
| `equipment` | equipment_asset, maintenance_ticket, maintenance_history |
| `crm` | crm_lead, crm_care_task, crm_care_note, crm_ticket |
| `rating` | rating |
| `promotion` | coupon, coupon_redemption, campaign, referral |
| `notification` | notification_message |
| `audit` | audit_log |
| `messaging` | outbox_event, processed_event |

Pantry dùng chung schema `inventory`. `report` không có bảng (dùng view/projection).

## Quy tắc FK

- **Giữ** FK khi cả hai bảng cùng schema (vd `payment.payment → payment.customer_order`, `groupclass.class_booking → groupclass.class_session`).
- **Bỏ** mọi FK chéo schema → chỉ còn cột ID (BIGINT, có index). Toàn vẹn do **application layer** đảm bảo (+ domain event/outbox khi cần).
- Trạng thái đã verify trên DB: **0 cross-schema FK**, 35 intra-schema FK.

## Cross-module logical references (app phải tự enforce)

Các "hub" được nhiều module tham chiếu bằng ID (khi tách service sẽ thành API/event):

- **→ `member.member_profile`**: kyc(*), membership, contract, payment.customer_order, checkin(*), booking.booking, groupclass(class_pass/class_booking), pt.pt_rating, privateroom.private_room_quota, massage.massage_weekly_usage, crm(*), rating.rating, promotion(coupon_redemption/referral), notification.notification_message.
- **→ `branch.branch_branch`** / `branch.branch_room`: staff.staff_branch_assignment, member, contract, payment.customer_order, checkin.checkin_log, booking.booking, groupclass.class_session, pt.trainer*, privateroom.private_room, massage.massage_room/availability, inventory(stock/movement/po/transfer/adjust), equipment, crm.
- **→ `staff.staff_staff`**: kyc(reviewed_by), groupclass.class_session(instructor), pt.trainer_profile, massage(availability/booking), inventory(movement/adjust created_by), equipment(assigned_to/performed_by), crm(assigned_to/author).
- **→ `identity`**: staff.staff_staff(user_account_id), staff_branch_assignment(role_id), member.member_profile(user_account_id).
- **→ `booking.booking`**: groupclass.class_booking, pt(pt_booking/pt_rating), privateroom.private_room_booking, massage.massage_booking, crm.crm_care_task(related_booking), rating.rating.
- **→ `payment.customer_order`**: finance.installment_application, groupclass.class_pass(source_order), promotion.coupon_redemption.
- **→ `contract.contract`**: membership.membership(contract_id), payment.customer_order(contract_id).
- **→ `membership.package_plan`**: contract.contract(package_plan_id).
- **→ `promotion.coupon`**: payment.customer_order(coupon_id).

## Hệ quả vận hành

- Code dùng **Native SQL schema-qualified** (`SELECT ... FROM member.member_profile`).
- `flyway_schema_history` nằm ở schema `public` (`spring.flyway.default-schema: public`).
- Khi tách 1 module thành microservice: bê nguyên schema sang DB riêng; các logical reference biến thành lời gọi API hoặc dữ liệu đến qua event — không phải gỡ FK.
