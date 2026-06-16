# Module: Product Inventory + Pantry

## Purpose

Manage partner products, supplement products, stock per branch, POS sales, and pantry food/drink sales.

## Actors

- Member
- Receptionist
- Inventory Staff
- Branch Manager
- Accountant
- Super Admin

## Product Types

- Gym support product: gloves, belt, towel, bottle, shirt.
- Supplement: protein, supplement, protein bar.
- Pantry: protein shake, smoothie, meal, drink.

## Business Rules

- Gym keeps partner product inventory.
- Inventory is tracked per branch.
- Product sale deducts stock atomically.
- Pantry sells to all members from 06:00 to 22:00.
- Pantry should track expiry date and batch/lot where relevant.
- Do not allow sale if stock is insufficient.

## Main Product Sale Flow

1. Staff/member selects product.
2. System checks branch stock.
3. System creates order.
4. Payment is completed.
5. Stock is deducted.
6. Order completed.

## Pantry Flow

1. Member orders pantry item.
2. System checks current time between 06:00 and 22:00.
3. System checks stock/batch.
4. System creates order and payment.
5. Staff prepares item.
6. Order completed.

## Suggested Data Fields

Product:
- id
- sku
- name
- category
- product_type
- brand_or_partner_id
- price
- active

Stock:
- id
- product_id
- branch_id
- quantity
- low_stock_threshold

Stock Movement:
- id
- product_id
- branch_id
- movement_type
- quantity
- reference_type
- reference_id
- created_at

Batch:
- id
- product_id
- branch_id
- batch_no
- expiry_date
- quantity

Order:
- id
- order_code
- member_id
- branch_id
- order_type
- total_amount
- status

## API Suggestions

- `GET /products`
- `POST /product-orders`
- `POST /stock/imports`
- `POST /stock/transfers`
- `POST /stock/adjustments`
- `GET /branches/{id}/stock`
- `POST /pantry-orders`

## Race Conditions

- Two sales for last item.
- Pantry batch quantity deducted twice.
- Payment success processed twice.

Use atomic stock deduction and idempotent payment/order processing.

## Tests

- Sell product successfully.
- Reject sale when stock insufficient.
- Pantry sale outside 06:00-22:00 denied.
- Deduct stock atomically.
- Low stock alert condition.
