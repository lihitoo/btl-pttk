# Trạng thái kiểm thử — 12/11/2026

## Đã thực hiện

| Hạng mục | Kết quả | Môi trường |
|---|---:|---|
| Biên dịch nguồn chính và test | Đạt | OpenJDK 21.0.12.1; javac --release 17; Servlet API 4.0.1. |
| Kiểm tra Java | 33/33 | Validation, BigDecimal, HTML escaping, password. |
| Kiểm tra JDBC | 56/56 | DAO của dự án; HSQLDB 1.8 cục bộ; dữ liệu từ seed6.sql. |
| Kiểm tra HTTP | 50/50 | Các Servlet/Filter chính, qua bộ thích nghi HTTP cục bộ; không phải Tomcat. |
| Đối chiếu code báo cáo | 11/11 đoạn | Giữ nội dung đoạn mã; so sánh bỏ qua khác biệt khoảng trắng. |
| Kết xuất bố cục | 11 màn hình | HTML do Servlet trả về, kết xuất trong Chromium; có màn hình rộng 390px. |

Log gốc nằm trong `test-results/unit6.txt`, `jdbc-hsqldb6.txt`, `http-local6.txt`, `layout-local6.txt`, `source-check6.txt`. Không lấy lại kết quả 45/45 hoặc 16/16 từ báo cáo cũ.

## Chưa xác nhận

**Chưa chạy Maven package, WAR/JSP trên Tomcat 9, MySQL 8.4, Docker Compose hoặc GitHub Actions trong môi trường đích.** Phiên tạo dự án không có Maven, Tomcat, MySQL/Docker và không tải được các gói qua mạng. Vì vậy không gắn nhãn “Tomcat/MySQL đã đạt”. Có cấu hình build/deploy và các bài test để chạy lại trên môi trường đó.

## Cách thử cục bộ đã dùng

Nguồn Java được biên dịch bằng javac với `--release 17`. Để chạy DAO, dùng HSQLDB đã có sẵn và chuyển **bản sao DDL dùng trong test**: AUTO_INCREMENT → IDENTITY, DATETIME → TIMESTAMP, TEXT → VARCHAR; UNIQUE chuyển sang ràng buộc cấp bảng; quote tên POSITION; bỏ câu cấu hình database/charset của MySQL. Không thay các truy vấn DAO trong `src/main`. Seed được nạp cùng nội dung dữ liệu. Bản MySQL gốc vẫn giữ trong `database/schema-mysql6.sql`.

Một bộ thích nghi nội bộ ánh xạ HttpExchange sang request/response/session để gọi Servlet và AuthFilter thật. Ở điểm forward JSP, bộ test gọi cùng `Html6.document()`. Đây chỉ là phép thử Java/HTTP, không chứng minh JSP đã được Tomcat biên dịch hoặc mọi hành vi container đúng.

Trình duyệt trong môi trường không cho điều hướng URL. Các ảnh trong `screenshots/` được tạo bằng cách lấy HTML từ các response đã kiểm thử, đưa HTML và CSS vào tài liệu trong bộ nhớ của Chromium để kiểm tra bố cục. **Không coi đó là kiểm thử bấm chuột xuyên luồng trên Tomcat.** Bộ thích nghi và công cụ môi trường không được đưa vào code ứng dụng để tránh làm phức tạp bài bảo vệ.

## Xác nhận môi trường đích trước khi nộp

1. Chạy Docker Compose theo README hoặc Tomcat/MySQL trực tiếp theo SETUP.
2. Chạy `mvn test -Pmysql-test` với cấu hình MySQL thật; kỳ vọng 33 kiểm tra Java và 56 kiểm tra JDBC.
3. Chạy `python tools/http-test6.py http://localhost:8080/restman6`; kỳ vọng 50 kiểm tra HTTP qua Tomcat.
4. Demo bằng trình duyệt theo DEMO.md, kiểm tra hình và chụp lại ảnh trên máy của mình.
5. Hoặc chạy workflow GitHub Actions đã chuẩn bị, xem log và artifact. Workflow chưa chạy thì không ghi “CI passed”.

Cần kiểm tra riêng cookie/session thực tế, JSP compilation, collation tiếng Việt, ràng buộc MySQL và ngày biên trên môi trường đích. Chưa kiểm thử tải, truy cập đồng thời hay triển khai production.
