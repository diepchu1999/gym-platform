# Quy tắc nghiệp vụ (Business Rules)

> Bản tiếng Việt (canonical). English: [`../../en/business/business-rules.md`](../../en/business/business-rules.md).

## Thành viên & Truy cập chi nhánh

BR-001: Mọi gói tập chính có hiệu lực trên tất cả chi nhánh.

BR-002: Gói tháng, quý, năm check-in không giới hạn theo ngày.

BR-003: Hệ thống vẫn phải chặn quét QR trùng trong cửa sổ thời gian ngắn, kể cả khi gói cho check-in không giới hạn.

BR-004: Hệ thống phải theo dõi riêng home branch, sale branch và chi nhánh check-in thực tế để báo cáo.

## Gói Trial

BR-005: Gói trial miễn phí 7 ngày.

BR-006: Trial yêu cầu KYC CCCD được duyệt trước khi kích hoạt.

BR-007: Một CCCD chỉ được dùng trial một lần.

BR-008: Trial cho 1 lượt check-in gym mỗi ngày.

BR-009: Trial không giới hạn khung giờ vì gym hoạt động 24/24.

BR-010: Trial bao gồm đúng 1 buổi group class dùng thử.

BR-011: Trial không bao gồm private room, quyền lợi massage VIP, hay PT miễn phí.

## VIP

BR-012: Thành viên VIP chỉ dùng private room thông qua booking.

BR-013: Việc dùng private room của VIP bị kiểm soát bởi quota giờ theo tháng.

BR-014: Mỗi lần booking private room tối đa 2 giờ.

BR-015: Thành viên VIP được 3 lượt booking massage miễn phí mỗi tuần.

BR-016: Sau khi dùng hết 3 lượt massage miễn phí trong tuần, các booking massage tiếp theo phải trả phí.

## Quy tắc Booking chung

BR-017: Tài nguyên booking không được trùng (double-book) cho cùng một tài nguyên.

BR-018: Thành viên không được tự đặt các buổi trùng giờ của chính mình.

BR-019: Khách có thể hủy ít nhất 10 giờ trước giờ bắt đầu để được hoàn tiền/buổi/quota.

BR-020: Hủy trong vòng 10 giờ trước giờ bắt đầu thì không hoàn, trừ khi nguyên nhân do phía gym.

BR-021: Nếu khách không đến đúng giờ booking, CSKH gọi xác nhận và có thể giữ chỗ tối đa 30 phút.

BR-022: Sau 30 phút mà khách không đến, booking chuyển thành NO_SHOW.

BR-023: No-show không hoàn tiền/buổi/quota.

BR-024: Nếu gym hủy do PT bận, phòng bảo trì, lớp bị hủy, hoặc sự cố hệ thống, khách được hoàn tiền/buổi/quota.

## Group Class

BR-025: Group class là dịch vụ bổ sung và bán theo số buổi.

BR-026: Thành viên cần class pass đang hiệu lực hoặc quyền lợi trial mới được đặt group class.

BR-027: Group class có lịch, huấn luyện viên, phòng và sức chứa.

BR-028: Không cho đặt khi lớp đã đầy.

BR-029: Không cho trùng lịch huấn luyện viên hoặc phòng.

## PT Booking

BR-030: PT là 1 kèm 1.

BR-031: Một buổi PT mặc định 90 phút.

BR-032: PT chỉ đặt được trong khung 06:00–22:00.

BR-033: Gym hoạt động 24/24 nhưng dịch vụ PT thì không.

BR-034: Khách thanh toán online hoặc tại quầy theo giá 1 buổi PT.

BR-035: Đánh giá PT phải ẩn danh với PT nhưng quản lý thấy được để xử lý nội bộ.

## Private Room

BR-036: Private room là tài nguyên giới hạn và phải đặt theo giờ.

BR-037: Mỗi private room có chi nhánh, trạng thái phòng, tình trạng sẵn sàng và lịch booking.

BR-038: Private room không thể đặt khi đang bảo trì, đóng, dọn dẹp, hoặc đã được đặt.

## Massage

BR-039: Dịch vụ massage dành cho thành viên VIP với 3 buổi miễn phí mỗi tuần.

BR-040: Thời lượng massage theo quy trình vận hành và có thể cấu hình nội bộ theo loại dịch vụ.

BR-041: Booking massage phải tránh trùng lịch nhân viên và phòng.

## Hợp đồng & Thanh toán

BR-042: Hợp đồng không cần quản lý duyệt.

BR-043: Hợp đồng có thể active sau khi khách ký/xác nhận và thanh toán hợp lệ.

BR-044: Trả góp chỉ áp dụng cho gói quý và năm.

BR-045: Trả góp do công ty tài chính xử lý, ví dụ FE Credit hoặc Home Credit.

BR-046: Khi công ty tài chính duyệt/giải ngân, gym coi như thanh toán hợp lệ.

## Sản phẩm, Tồn kho, Pantry

BR-047: Sản phẩm đối tác do gym mua và nhập kho.

BR-048: Tồn kho phải theo dõi theo từng chi nhánh.

BR-049: Bán sản phẩm hoặc pantry phải trừ tồn kho theo cách atomic.

BR-050: Pantry bán cho mọi thành viên trong khung 06:00–22:00.

BR-051: Đồ ăn/uống pantry nên hỗ trợ hạn dùng và theo dõi batch/lô.

## Thiết bị

BR-052: Thiết bị được theo dõi theo chi nhánh, khu vực/phòng, trạng thái, số lượng hoặc mã tài sản, và lịch sử bảo trì.

BR-053: Thiết bị có thể được báo hỏng bởi nhân viên hoặc thành viên.

BR-054: Phiếu bảo trì nên theo dõi người phụ trách, trạng thái, chi phí và cách xử lý.
