# Module: Product Inventory + Pantry

> Bản tiếng Việt (canonical). English: [`../../en/modules/inventory-pantry.md`](../../en/modules/inventory-pantry.md).

## Mục đích
Quản lý sản phẩm đối tác, sản phẩm bổ sung, tồn kho theo chi nhánh, bán POS, và bán đồ ăn/uống pantry.

## Tác nhân
- Member
- Receptionist
- Inventory Staff
- Branch Manager
- Accountant
- Super Admin

## Loại sản phẩm
- Sản phẩm hỗ trợ gym: găng tay, đai, khăn, bình nước, áo.
- Bổ sung: protein, supplement, thanh protein.
- Pantry: protein shake, smoothie, suất ăn, đồ uống.

## Quy tắc nghiệp vụ
- Gym giữ tồn kho sản phẩm đối tác.
- Tồn kho theo dõi theo chi nhánh.
- Bán sản phẩm trừ tồn kho atomic.
- Pantry bán cho mọi member từ 06:00–22:00.
- Pantry nên theo dõi hạn dùng và batch/lô khi cần.
- Không cho bán nếu tồn không đủ.

## Luồng bán sản phẩm chính
1. Nhân viên/member chọn sản phẩm.
2. Hệ thống kiểm tra tồn chi nhánh.
3. Hệ thống tạo order.
4. Hoàn tất thanh toán.
5. Trừ tồn kho.
6. Order hoàn thành.

## Luồng Pantry
1. Member đặt món pantry.
2. Hệ thống kiểm tra giờ hiện tại trong 06:00–22:00.
3. Hệ thống kiểm tra tồn/batch.
4. Hệ thống tạo order và thanh toán.
5. Nhân viên chuẩn bị món.
6. Order hoàn thành.

## Trường dữ liệu gợi ý

Product:
- id, sku, name, category, product_type, brand_or_partner_id, price, active

Stock:
- id, product_id, branch_id, quantity, low_stock_threshold

Stock Movement:
- id, product_id, branch_id, movement_type, quantity, reference_type, reference_id, created_at

Batch:
- id, product_id, branch_id, batch_no, expiry_date, quantity

Order:
- id, order_code, member_id, branch_id, order_type, total_amount, status

## Gợi ý API
- `GET /products`
- `POST /product-orders`
- `POST /stock/imports`
- `POST /stock/transfers`
- `POST /stock/adjustments`
- `GET /branches/{id}/stock`
- `POST /pantry-orders`

## Race Conditions
- Hai lần bán cho món cuối cùng.
- Số lượng batch pantry bị trừ hai lần.
- Thanh toán thành công xử lý hai lần.

Dùng trừ tồn kho atomic và xử lý order/thanh toán idempotent.

## Tests
- Bán sản phẩm thành công.
- Từ chối bán khi tồn không đủ.
- Bán pantry ngoài 06:00–22:00 bị từ chối.
- Trừ tồn kho atomic.
- Điều kiện cảnh báo tồn thấp.
