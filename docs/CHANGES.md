# Phần hoàn thiện so với code được in trong báo cáo

## Giữ nguyên

Tên model/DAO/Servlet và URL; 24 model/17 bảng; hai truy vấn FoodDAO6; SQL thống kê/lịch sử; đoạn nạp chi tiết hóa đơn; công thức tính tiền; PAID theo paidAt; [startTime,endTime); giao diện dựa trên hình báo cáo; hai tài khoản demo.

## Bổ sung để chạy

Import, constructor, getter/setter; đọc cấu hình môi trường; JDBC mapping còn thiếu; transaction hóa đơn; HTML/CSS/layout JSP; đăng nhập và token CSRF; dữ liệu mẫu cho đủ các bảng; Maven WAR; Docker Compose; hướng dẫn chạy và kiểm thử.

Các ngày mặc định 01–31/10/2026 chỉ giúp chọn đúng bộ dữ liệu minh họa. Doanh thu không bị gán cố định; mọi giá trị được lấy từ MySQL/DAO.

Các trang chủ chưa có ảnh minh chứng riêng dùng cùng bố cục với trang trong báo cáo. Các báo cáo ngoài module đã giao được hiển thị “chưa cài đặt”, không tạo kết quả giả.

## Cần phân biệt khi bảo vệ

- Giao diện trong sơ đồ có các thao tác như search()/selectFood(); bản Servlet nhận thao tác bằng URL và execute(request,response), như giải thích trong Câu 4.b.
- Biến UI được đặt cục bộ trong request, không dùng field trạng thái dùng chung giữa người dùng.
- Không giữ DemoServer/HSQLDB trong `src/main`; mã triển khai chính là Servlet/JSP và Connector/J.
- Bài này không sửa lại file báo cáo Word. Kết quả kiểm thử mới nằm trong `docs/test-results`, không thay đổi số liệu kiểm thử cũ trong Word.
- GitHub workflow là cấu hình để kiểm thử môi trường đích; chưa phải bằng chứng đã thực thi.

## Giới hạn bảo mật của demo

Tài khoản và mật khẩu mẫu là công khai, chỉ dùng localhost. Các cổng Compose không bind ra Internet. SSL JDBC tắt cho đường truyền nội bộ demo; chưa có kiểm thử tải/rate limiting/triển khai HTTPS. Không đưa bản cấu hình mẫu lên máy chủ công khai rồi gọi đó là bản production.
