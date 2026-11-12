# Đối chiếu với báo cáo BTL 41 trang

Nguồn chuẩn: `BTL_RestMan23_BanSua/23_B23DCCN876.docx`, không lấy bản pha Thiết kế 28 trang thay thế. Các đoạn in trong Câu 4.b được giữ; phần báo cáo chỉ mô tả hoặc lược bỏ được viết bổ sung để thành project.

| Trong báo cáo | File tương ứng | Mức kế thừa |
|---|---|---|
| Food6: id, name, price, description, status | `src/main/java/model/Food6.java` | Giữ trường/kiểu; thêm getter/setter đã lược trong báo cáo. |
| FoodRevenueStatistic6 | `model/FoodRevenueStatistic6.java` | Giữ kế thừa và revenue. |
| getLineAmount | `model/OrderDetail6.java` | Giữ nguyên phép nhân BigDecimal. |
| getTotalAmount hóa đơn | `model/Invoice6.java`, `model/Order6.java` | Giữ chuyển tiếp tới tổng các dòng đơn. |
| DAO6 | `dao/DAO6.java` | Giữ final Connection, constructor và Objects.requireNonNull. |
| searchFoodByName/getFoodDetail | `dao/FoodDAO6.java` | Giữ nguyên hai phương thức, SQL và kiểu trả về. Bổ sung map(ResultSet). |
| SQL thống kê và đóng gói | `dao/FoodRevenueStatisticDAO6.java` | Giữ JOIN, SUM, PAID, paidAt, GROUP BY, ORDER BY, binding và các setter; SQL bỏ dấu ; cuối khi đưa vào text block. |
| SQL lịch sử | `dao/OrderDetailDAO6.java` | Giữ câu SQL; bổ sung binding/đóng gói và kiểm tra đầu vào. |
| Đoạn nạp chi tiết hóa đơn | `dao/InvoiceDAO6.java` | Giữ nguyên đoạn từ String detail tới return Optional.of(inv); bổ sung phần đầu hóa đơn và giao dịch được mô tả. |
| Chuẩn hóa hai ngày | `web/FoodRevenueStatisticUI6.java` | Giữ ba dòng dates/startTime/endTime. |
| Điều kiện phân quyền | `web/AuthFilter6.java` | Giữ điều kiện denied; bổ sung nội dung trang 403. |
| Bảng 4.3 URL–Servlet | `web/*UI6.java` | Giữ đúng URL và tên lớp; triển khai qua execute(request,response). |
| layout6.jsp/Html6.e | `web/Html6.java`, `WEB-INF/views/layout6.jsp` | Bổ sung phần triển khai chưa in đầy đủ; cùng cách phân lớp. |
| Hình 4.5–4.10 | `assets/style6.css`, các Servlet | Bám bố cục đỏ sẫm, nền sáng, bảng kết quả và trường thông tin; không cam kết trùng từng pixel. |
| Bảng 4.1, 17 bảng | `database/schema-mysql6.sql` | Giữ tên/kiểu/khóa/ràng buộc; bổ sung chỉ mục và mã SQL. |
| Bảng 5.2 tài khoản | `database/seed6.sql` | customer6/Customer@123; manager6/Manager@123. |
| Số liệu Câu 4 | `database/seed6.sql`, `verify6.sql` | Tái tạo 505000 tháng 10 và hóa đơn 201=210000. |

Tên gói ngắn `model`, `dao`, `web`, `config`, `util` giữ như báo cáo. Tất cả lớp do dự án định nghĩa kết thúc bằng `6`; không đổi tên các lớp Java/Servlet như Connection, List, HttpServlet.

Các lớp thực thể không dùng trong hai module vẫn được giữ để bao quát mô hình hệ thống. Không bổ sung DAO CRUD cho chúng khi chưa có nghiệp vụ/giao diện tương ứng trong phạm vi demo.

Không có ZIP nguồn cũ để đối chiếu byte-for-byte toàn bộ project. “Giữ code báo cáo” nghĩa là giữ các đoạn đã in và các quy ước được mô tả, không khẳng định mọi file mới đều giống một bản ZIP cũ chưa được truy xuất.
