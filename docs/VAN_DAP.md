# Ôn vấn đáp — đọc đúng file, hiểu đúng luồng

## Thứ tự đọc code

1. `model/Food6.java`, `model/OrderDetail6.java`, `model/Order6.java`: dữ liệu và công thức tiền.
2. `dao/FoodDAO6.java`, `web/SearchFoodUI6.java`: một luồng hoàn chỉnh, dễ đọc nhất.
3. `dao/FoodRevenueStatisticDAO6.java`: cách nhóm và cộng doanh thu.
4. `dao/OrderDetailDAO6.java`, `dao/InvoiceDAO6.java`: lịch sử và hóa đơn.
5. `web/FoodRevenueStatisticUI6.java`, `web/InvoiceDetailUI6.java`: tham số, bộ lọc và kiểm tra ngữ cảnh.
6. `web/AuthFilter6.java`, `web/LoginUI6.java`, `config/Database6.java`: đăng nhập, quyền và kết nối.

Không cần học thuộc các getter/setter. Cần biết trường đó chứa gì và lớp nào sử dụng nó. Docker, GitHub Actions và các file test là phần hỗ trợ chạy/kiểm tra, không phải logic nghiệp vụ.

## 1. Kiến trúc của chương trình?

Trình duyệt gửi request → `AuthFilter6` kiểm tra phiên/quyền → Servlet UI lấy tham số và kiểm tra đầu vào → DAO truy vấn MySQL → đóng gói model → Servlet tạo nội dung → `layout6.jsp` trả HTML. Không có SQL trong JSP; không có logic giao diện trong DAO.

Mở `SearchFoodUI6.execute()`: tham số là `keyword`, kết quả DAO là `List<Food6>`, từng phần tử được hiển thị thành một dòng bảng.

## 2. Vì sao có 24 model nhưng chỉ 17 bảng?

Bốn lớp thống kê là dữ liệu dẫn xuất nên không có bảng riêng. Ba lớp vai trò nhân viên dùng chung bảng `Employee6`. `Employee6.id` và `Customer6.id` tham chiếu `User6.id`, không tạo lại định danh.

## 3. Vì sao thuộc tính private, còn getter/setter public?

Thuộc tính là dữ liệu bên trong đối tượng. Getter/setter cho lớp khác đọc hoặc gán dữ liệu theo giao diện rõ ràng. Ví dụ DAO tạo `Food6`, dùng `setName()`, UI đọc bằng `getName()`.

## 4. Tìm kiếm tên món hoạt động thế nào?

`Validation6.likePattern()` bỏ khoảng trắng thừa, kiểm tra 1–150 ký tự, escape `%`, `_`, `!`, rồi thêm `%` hai đầu. SQL trong `FoodDAO6` dùng `LOWER(name) LIKE LOWER(?) ESCAPE '!'`. `LOWER` giúp tìm không phân biệt hoa/thường. Không thêm chức năng tìm kiếm bỏ dấu.

## 5. PreparedStatement để làm gì?

Cấu trúc SQL cố định và giá trị nhập được truyền qua `setString`, `setLong`, `setTimestamp`. Giá trị không trở thành cú pháp SQL. `%` và `_` vẫn có ý nghĩa trong LIKE, nên phải escape riêng để thực hiện đúng yêu cầu “tên chứa từ khóa”.

## 6. List và Optional khác nhau thế nào trong bài?

Tìm kiếm có nhiều kết quả nên trả `List<Food6>`, không tìm thấy trả list rỗng. Tìm một món theo mã hoặc một hóa đơn theo đơn có thể không có, nên trả `Optional`. UI kiểm tra `isEmpty()` và trả 404, không giả tạo đối tượng rỗng.

## 7. Công thức doanh thu?

Một dòng: `quantity × unitPrice`, nằm trong `OrderDetail6.getLineAmount()`. Doanh thu món: SQL `SUM(d.quantity * d.unitPrice)`, nhóm theo món. Chỉ lấy hóa đơn `PAID`, lọc theo `paidAt`. Đây là quy ước cài đặt đã nêu trong báo cáo, không phải câu chữ nguyên văn của đề.

Ví dụ Cơm gà: `2×60.000 + 1×60.000 + 1×65.000 = 245.000`. Không dùng `Food6.price` vì đó là giá hiện tại.

## 8. Vì sao không dùng double cho tiền?

`BigDecimal` và `DECIMAL(15,2)` biểu diễn số thập phân phù hợp để cộng nhân tiền. Tránh sai số nhị phân của `double`. Chương trình dùng vòng lặp cộng, không dùng stream/lambda cho phần tính tiền.

## 9. Tại sao ngày cuối chuyển sang ngày tiếp theo?

Người dùng chọn đến hết 31/10. Code đổi thành `[01/10 00:00, 01/11 00:00)`, tức `>= startTime` và `< endTime`. Cách này bao gồm toàn bộ ngày cuối mà không phải đoán thời điểm cuối cùng của ngày.

## 10. Tại sao lịch sử có hai dòng cùng món trong một đơn?

Mỗi `OrderDetail6` là một lần gọi, có `id` và `calledAt` riêng. Đơn 101 gọi Cơm gà hai lần nên phải giữ hai dòng 1001 và 1003. Không đặt UNIQUE(orderId, foodId).

## 11. Tại sao mở Cơm gà lại thấy cả Nước cam trong hóa đơn?

Thao tác chọn một lần gọi xác định `orderId`. `InvoiceDAO6.getInvoice(orderId)` nạp toàn bộ `OrderDetail6` của đơn, không lọc tiếp theo foodId. `Order6.getTotalAmount()` cộng tất cả dòng: 120.000 + 30.000 + 60.000 = 210.000.

## 12. Các id trên URL có đáng tin không?

Không. `InvoiceDetailUI6` kiểm tra dòng được chọn thuộc đúng đơn, đúng món, hóa đơn PAID và paidAt nằm trong bộ lọc. Sửa `detailId`, `foodId` hoặc ngày trên URL có thể bị trả 404. Quyền quản lí đã được `AuthFilter6` kiểm tra trước đó.

## 13. Vì sao dùng giao dịch khi đọc hóa đơn?

Đầu hóa đơn và các dòng món được đọc trong cùng giao dịch `REPEATABLE_READ`, tránh lấy đầu hóa đơn ở một trạng thái và chi tiết ở trạng thái khác khi có cập nhật đồng thời. Đọc xong commit; lỗi thì rollback; cuối cùng khôi phục trạng thái Connection.

## 14. Ai mở và đóng Connection?

Servlet gọi `Database6.open()`, truyền vào constructor DAO. `DAO6` giữ `protected final Connection con`. `try-with-resources` đóng Connection/PreparedStatement/ResultSet sau khi dùng, kể cả khi có lỗi. Bản demo không có connection pool để giữ cấu trúc đơn giản; không nói đây là cấu hình tối ưu cho hệ thống tải lớn.

## 15. Tại sao Servlet không lưu keyword vào biến thành viên?

Một Servlet có thể phục vụ nhiều request. Lưu trạng thái người dùng vào biến thành viên dễ làm lẫn dữ liệu giữa hai người. Bản cài đặt giữ keyword/id/ngày bằng biến cục bộ và tham số request. Đây là cách ánh xạ các thuộc tính UI trong sơ đồ sang Servlet.

## 16. Đăng nhập và phân quyền ở đâu?

`AccountDAO6.authenticate()` kiểm tra tài khoản ACTIVE và mật khẩu. `Password6` dùng PBKDF2-HMAC-SHA256, salt riêng, 600.000 vòng theo báo cáo; không lưu mật khẩu rõ. Đăng nhập thành công hủy phiên cũ, tạo phiên mới chứa `SessionUser6`. `AuthFilter6` kiểm tra vai trò theo `/customer/` và `/manager/`.

## 17. CSRF và escape HTML ở đâu?

Đăng nhập/đăng xuất dùng POST và token CSRF gắn với phiên; yêu cầu thiếu hoặc sai token bị chặn. `Html6.e()` đổi các ký tự `<`, `>`, `&`, dấu nháy trước khi đưa dữ liệu vào HTML. Escape HTML khác với escape LIKE và khác với truyền tham số SQL.

## 18. Tại sao sơ đồ dùng UI6 nhưng code là Servlet?

Báo cáo đã ánh xạ lớp giao diện sang Servlet ở Bảng 4.3. `execute(request,response)` nhận thao tác HTTP; chọn món/quay lại được thực hiện bằng liên kết. Không giữ thêm một bản giao diện có trạng thái riêng hoặc viết lại thành React.

## 19. Phân biệt lỗi và dữ liệu rỗng?

List rỗng: dữ liệu hợp lệ nhưng không có kết quả. Đầu vào sai: 400. Chưa đăng nhập: chuyển tới login. Sai quyền: 403. Mã không có: 404. Lỗi SQL/kết nối: 503 và log phía server, không báo “không có kết quả” để che lỗi.

## 20. Những phần nào chưa cài đặt/chưa kiểm chứng?

Chưa có màn hình tạo đơn, thanh toán, nhập kho và các module ngoài hai chức năng được giao. Dữ liệu mẫu nhập bằng SQL. Bản bàn giao đã kiểm tra Java/JDBC/HTTP cục bộ; Tomcat/MySQL phải được xác nhận bằng các lệnh trong SETUP hoặc workflow GitHub Actions. Không nhận là đã kiểm thử tải hay triển khai production.
