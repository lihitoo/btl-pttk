# Cài đặt RestMan6

## A. Dùng Docker (cách khởi động ít cấu hình)

1. Cài Docker và bật Docker trước khi chạy lệnh. Mở thư mục dự án trong terminal.
2. Chép `.env.example` thành `.env` (chỉ làm lần đầu, không ghi đè cấu hình đang dùng).
3. Chạy `docker compose up -d --build`.
4. Kiểm tra `docker compose ps` và `docker compose logs web`. Truy cập `http://localhost:8080/restman6/login` khi Tomcat đã triển khai WAR.

MySQL ở cổng `localhost:3307`, database `restman6`, user `restman6`, mật khẩu từ `.env`. Ứng dụng ở cổng `8080`. Cả hai cổng chỉ bind localhost. User ứng dụng chỉ có quyền **SELECT**; root chỉ dùng khởi tạo/quản trị demo.

`seed6.sql` chỉ tự nạp lần đầu khi volume trống. Sửa seed rồi restart không làm dữ liệu tự đổi. Muốn nạp lại: sao lưu dữ liệu cần giữ, chạy `docker compose down -v` rồi `docker compose up -d --build`. Lệnh `down -v` xóa volume CSDL demo của dự án.

## B. Cài JDK, Maven, Tomcat và MySQL trực tiếp

**Cấu hình cần có:** JDK 17 trở lên; Maven 3.9.x; Tomcat **9.x**; MySQL **8.4**. Không thay Tomcat 9 bằng Tomcat 10/11: mã đang dùng `javax.servlet`, không dùng `jakarta.servlet`.

### 1. Khởi tạo MySQL

Mở MySQL Workbench, đăng nhập bằng root. Chạy lần lượt toàn bộ nội dung:

```text
database/schema-mysql6.sql
database/seed6.sql
database/grant-local6.sql
database/verify6.sql
```

Hoặc trong MySQL CLI, đứng ở thư mục gốc dự án:

```sql
SOURCE database/schema-mysql6.sql;
SOURCE database/seed6.sql;
SOURCE database/grant-local6.sql;
SOURCE database/verify6.sql;
```

`schema-mysql6.sql` không ghi đè bảng đang có. Khi cần tạo lại, xem cảnh báo trong `database/reset-demo6.sql` trước. Schema và seed dùng UTF-8; không sửa tên bảng chữ hoa/chữ thường tùy ý vì Linux phân biệt tên bảng.

Mật khẩu app mẫu của cách B: `RestmanDemo6!`. Chạy script cấp quyền bằng root, không chạy bằng tài khoản chỉ có SELECT.

### 2. Cấu hình và build trên Windows PowerShell

```powershell
java -version
mvn -version
Copy-Item tools/env6.ps1.example tools/env6.ps1
. ./tools/env6.ps1
mvn clean package
mvn test -Pmysql-test
```

Sửa `tools/env6.ps1` khi máy có cổng, user hoặc mật khẩu khác. File này không được commit. `JAVA_HOME` phải trỏ tới JDK. `mvn` phải có trong PATH.

Chép `target/restman6.war` vào thư mục `webapps` của Tomcat. **Khởi động Tomcat từ cùng cửa sổ PowerShell đã nạp biến môi trường**:

```powershell
$env:CATALINA_HOME = 'C:\Tools\apache-tomcat-9'
Copy-Item target/restman6.war "$env:CATALINA_HOME\webapps\restman6.war"
& "$env:CATALINA_HOME\bin\catalina.bat" run
```

Đổi đường dẫn `CATALINA_HOME` thành thư mục Tomcat thật trên máy. Truy cập `http://localhost:8080/restman6/login`.

Nếu dùng Tomcat chạy dưới Windows Service hoặc IDE, biến trong terminal không tự truyền sang tiến trình đó. Khi ấy phải cấu hình env ở Service/Run configuration, hoặc dùng `catalina.bat run` như trên.

### 3. Cấu hình và build trên Linux/macOS

```bash
cp tools/env6.sh.example tools/env6.sh
source tools/env6.sh
mvn clean package
mvn test -Pmysql-test
export CATALINA_HOME=/duong-dan/toi/apache-tomcat-9
cp target/restman6.war "$CATALINA_HOME/webapps/"
"$CATALINA_HOME/bin/catalina.sh" run
```

### 4. Kiểm thử sau khi triển khai

```bash
python tools/http-test6.py http://localhost:8080/restman6
```

Script dùng thư viện chuẩn Python, không cần pip. Trên máy có `python3`, thay `python` bằng `python3`. Script gồm 50 kiểm tra: đăng nhập, phân quyền, tìm kiếm, bộ lọc, hóa đơn, quay lại, CSRF và các nhánh lỗi.

Thời gian trong bài được hiểu theo múi giờ `+07:00`; cấu hình mẫu đặt Java về `Asia/Bangkok`. Đây là dữ liệu DATETIME của nhà hàng, không phải thời gian UTC. Không tự chuyển ngày tháng trong seed sang UTC.

## C. Lỗi thường gặp

| Hiện tượng | Kiểm tra |
|---|---|
| Cổng 8080 đã dùng | Tắt Tomcat khác hoặc đổi cổng phía trái của Compose thành `8081:8080`, rồi mở cổng mới. |
| Cổng 3307 đã dùng | Đổi cổng phía trái của `3307:3306`; JDBC trong Docker vẫn dùng `db:3306`. |
| 404 ở `/login` | Đường dẫn đúng của bản WAR là `/restman6/login`. |
| 503 khi đăng nhập | Xem log Tomcat, kiểm tra MySQL đang chạy, user/password, schema/seed và các biến môi trường. Không ghi mật khẩu vào ảnh chụp log. |
| Không tìm thấy `com.mysql.cj.jdbc.Driver` | Build bằng Maven để Connector/J được đưa vào WAR. Không chỉ chép các file `.class` vào Tomcat. |
| `javax.servlet` không tồn tại | Dùng Tomcat 9 và dependency Servlet API 4.0.1. |
| Lỗi `CREATE TABLE ... already exists` | CSDL đã có bảng; không chạy lại schema lên dữ liệu hiện tại. Dùng CSDL demo rỗng hoặc chủ động reset sau sao lưu. |
| Số tiền khác báo cáo | Chọn tháng 10/2026, kiểm tra đã sửa seed/giao dịch hay chưa; chạy `verify6.sql`. |
| Hiện `10/01/2026` | Ô date hiển thị theo ngôn ngữ trình duyệt. Giá trị gửi đi vẫn là `2026-10-01`. |
| Không thấy món khi nhập `com` | Dữ liệu là `Cơm`; bản này không cam kết tìm kiếm bỏ dấu. Hãy tìm có dấu theo đề. |
| GitHub Actions thất bại | Đọc bước báo đỏ và artifact log. Chưa được coi là kiểm thử đạt chỉ vì có file workflow. |

## D. Trước buổi demo

Chạy đủ hai luồng trên máy sẽ trình diễn. Kiểm tra tài khoản, các mốc 505.000/210.000, thao tác quay lại và các thông báo lỗi. Không thay cấu hình hoặc xóa dữ liệu ngay trước lúc demo nếu chưa chạy lại kiểm thử.
