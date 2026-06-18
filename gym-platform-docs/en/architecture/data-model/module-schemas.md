# Module Schemas & Cross-Module References

> English version. Vietnamese (canonical): [`../../../vi/architecture/data-model/module-schemas.md`](../../../vi/architecture/data-model/module-schemas.md).

Microservices-ready data layout: **one PostgreSQL schema per module**, **no cross-module FK**. See ADR-0011.

> In the phase files (`p1`..`p9`), any "FK" pointing to a table in another module is a **logical reference** (a BIGINT column, NOT a DB FK). FK exists only within the same schema/module.

## Schema map

| Schema | Tables |
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

Pantry shares the `inventory` schema. `report` has no tables (uses views/projections).

## FK rules

- **Keep** the FK when both tables are in the same schema (e.g. `payment.payment → payment.customer_order`, `groupclass.class_booking → groupclass.class_session`).
- **Drop** every cross-schema FK → keep only an ID column (BIGINT, indexed). Integrity is enforced by the **application layer** (+ domain events/outbox where needed).
- Verified on the DB: **0 cross-schema FK**, 35 intra-schema FK.

## Cross-module logical references (the app must enforce)

Hubs referenced by many modules via ID (these become API/event boundaries when extracted):

- **→ `member.member_profile`**: kyc(*), membership, contract, payment.customer_order, checkin(*), booking.booking, groupclass(class_pass/class_booking), pt.pt_rating, privateroom.private_room_quota, massage.massage_weekly_usage, crm(*), rating.rating, promotion(coupon_redemption/referral), notification.notification_message.
- **→ `branch.branch_branch`** / `branch.branch_room`: staff.staff_branch_assignment, member, contract, payment.customer_order, checkin.checkin_log, booking.booking, groupclass.class_session, pt.trainer*, privateroom.private_room, massage.massage_room/availability, inventory(stock/movement/po/transfer/adjust), equipment, crm.
- **→ `staff.staff_staff`**: kyc(reviewed_by), groupclass.class_session(instructor), pt.trainer_profile, massage(availability/booking), inventory(movement/adjust created_by), equipment(assigned_to/performed_by), crm(assigned_to/author).
- **→ `identity`**: staff.staff_staff(user_account_id), staff_branch_assignment(role_id), member.member_profile(user_account_id).
- **→ `booking.booking`**: groupclass.class_booking, pt(pt_booking/pt_rating), privateroom.private_room_booking, massage.massage_booking, crm.crm_care_task(related_booking), rating.rating.
- **→ `payment.customer_order`**: finance.installment_application, groupclass.class_pass(source_order), promotion.coupon_redemption.
- **→ `contract.contract`**: membership.membership(contract_id), payment.customer_order(contract_id).
- **→ `membership.package_plan`**: contract.contract(package_plan_id).
- **→ `promotion.coupon`**: payment.customer_order(coupon_id).

## Operational implications

- Code uses **schema-qualified Native SQL** (`SELECT ... FROM member.member_profile`).
- `flyway_schema_history` lives in the `public` schema (`spring.flyway.default-schema: public`).
- When extracting a module into a microservice: move the schema into its own DB; logical references become API calls or event-delivered data — no FK to remove.
