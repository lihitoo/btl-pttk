# RestMan6 — Demo đề 23

**MSSV: B23DCCN876.** Java 17 · Servlet/JSP · JDBC · MySQL 8.4 · Tomcat 9.

Dự án cài đặt **hai module trong báo cáo BTL**: tìm kiếm/xem chi tiết món ăn; thống kê doanh thu/xem lần gọi món/xem hóa đơn. Có đăng nhập, phân quyền và đăng xuất. Không cài thêm Spring, React, Hibernate hoặc thư viện giao diện.

**Trạng thái kiểm thử:** nguồn Java đã biên dịch và kiểm thử cục bộ; **chưa chạy xác nhận trên Tomcat/MySQL trong phiên xây dựng** do môi trường không tải được các runtime. Cấu hình triển khai và bộ kiểm thử đích có sẵn. Không dùng kết quả HSQLDB để kết luận MySQL đã đạt. Xem [kết quả kiểm thử](docs/TEST_STATUS.md).

## 1. Chạy bằng Docker

Cần Docker có lệnh `docker compose` và Internet để tải image/thư viện lần đầu. Mở terminal trong thư mục có `compose.yaml`.

Windows PowerShell:
```powershell
Copy-Item .env.example .env
docker compose up -d --build
docker compose logs -f web
```

Linux/macOS:
```bash
cp .env.example .env
docker compose up -d --build
docker compose logs -f web
```

Sau khi Tomcat khởi động, mở **http://localhost:8080/restman6/login**. `Ctrl+C` thoát màn hình log, không tắt ứng dụng. MySQL tự tạo 17 bảng và nạp `seed6.sql` khi volume còn trống.

| Vai trò demo | Tên đăng nhập | Mật khẩu mẫu |
|---|---|---|
| Khách hàng | `customer6` | `Customer@123` |
| Quản lí | `manager6` | `Manager@123` |

Các tài khoản này chỉ dùng cho bài tập trên máy cá nhân. Không đưa cấu hình/mật khẩu thật vào GitHub. `.env` đã có trong `.gitignore`.

Tắt và giữ dữ liệu: `docker compose down`. **Đặt lại dữ liệu demo (xóa toàn bộ dữ liệu trong volume của dự án):** `docker compose down -v`, sau đó chạy lại `docker compose up -d --build`. Không dùng `-v` khi cần giữ dữ liệu vừa nhập.

Không dùng Docker: xem [SETUP.md](docs/SETUP.md) để chạy trực tiếp bằng JDK, Maven, Tomcat và MySQL.

## 2. Demo đúng số liệu báo cáo

Khách hàng: đăng nhập → tìm `Cơm` → chi tiết Cơm gà → quay lại kết quả.

Quản lí: đăng nhập → Xem báo cáo → Thống kê món ăn theo doanh thu → chọn **01/10/2026–31/10/2026** → xem Cơm gà → chọn dòng `1001` → hóa đơn `201`.

| Kết quả tháng 10/2026 | Số tiền |
|---|---:|
| Cơm gà | 245.000 đ |
| Phở bò | 170.000 đ |
| Nước cam | 90.000 đ |
| Tổng doanh thu | **505.000 đ** |
| Hóa đơn #201, ba dòng món | **210.000 đ** |

Giá và tổng tiền được tính từ dữ liệu SQL, không gán sẵn vào giao diện. [Kịch bản demo chi tiết](docs/DEMO.md).

## 3. Đọc code ở đâu?

```text
src/main/java/
  model/    24 lớp thực thể, thuộc tính và getter/setter
  dao/      SQL + PreparedStatement + đóng gói kết quả
  web/      Servlet nhận yêu cầu, kiểm tra quyền, tạo nội dung giao diện
  config/   Database6 mở kết nối từ biến môi trường
  util/     Validation6, Password6, SessionUser6
src/main/webapp/
  assets/style6.css              giao diện theo ảnh báo cáo
  WEB-INF/views/layout6.jsp      khung trang dùng chung
src/test/java/checks/            kiểm thử Java và JDBC
database/                      17 bảng, dữ liệu mẫu và câu SQL đối chiếu
docs/                          hướng dẫn demo, vấn đáp, đối chiếu báo cáo
```

Bắt đầu với `Food6.java` → `FoodDAO6.java` → `SearchFoodUI6.java`. Sau đó đọc `FoodRevenueStatisticDAO6.java`, `OrderDetailDAO6.java`, `InvoiceDAO6.java`. Xem [VAN_DAP.md](docs/VAN_DAP.md) và [REPORT_CODE_MAP.md](docs/REPORT_CODE_MAP.md).

**Phạm vi:** các bảng và model bao quát hệ thống; chương trình chạy hai module đã được giao. Nhập kho, tạo combo, đặt bàn, gọi món và thanh toán chưa có màn hình thao tác. Dữ liệu gọi món/hóa đơn được nạp bằng SQL để phục vụ báo cáo. Các mục thống kê khác ghi rõ “chưa cài đặt”, không có nút giả chạy.

## 4. Build và kiểm thử

```bash
mvn clean package            # chạy 33 kiểm tra Java, tạo target/restman6.war
mvn test -Pmysql-test         # thêm 56 kiểm tra JDBC trên MySQL đã nạp seed6.sql
python tools/http-test6.py http://localhost:8080/restman6
```

Với Maven chạy ngoài Docker, cấu hình `RESTMAN_DB_URL`, `RESTMAN_DB_USER`, `RESTMAN_DB_PASSWORD` trước khi chạy profile `mysql-test`. Nếu kết nối MySQL trong Compose, dùng cổng **3307**; MySQL cài trực tiếp thường là **3306**. Dữ liệu cần ở trạng thái `seed6.sql` ban đầu. Kiểm thử JDBC và HTTP chỉ đọc dữ liệu, không reset database.

Ảnh trong `docs/screenshots/` được kết xuất từ HTML của Servlet trên bộ thích nghi HTTP/HSQLDB cục bộ, **không phải ảnh nghiệm thu Tomcat/MySQL**. Cách dựng lại ảnh và giới hạn được ghi trong [TEST_STATUS.md](docs/TEST_STATUS.md).

## 5. Đưa lên GitHub

Giải nén và đưa **nội dung thư mục `RestMan6`** lên repository; `pom.xml`, `README.md`, `src/`, `database/`, `.github/` phải ở gốc repo. Không chỉ upload mỗi ZIP. Không commit `.env`, `target/`, mật khẩu thật hoặc file dữ liệu máy cá nhân.

Có sẵn workflow `.github/workflows/demo6.yml` để build và kiểm thử trên Tomcat/MySQL khi được chạy trong GitHub Actions. Workflow **chưa được chạy trong phiên tạo mã nguồn**. Sau khi push, kiểm tra tab **Actions**, xem log và artifact kết quả; cần xử lí mọi lỗi trước khi demo với giảng viên.

## Tài liệu chính

- [Cài đặt và sửa lỗi thường gặp](docs/SETUP.md)
- [Kịch bản demo](docs/DEMO.md)
- [Ôn vấn đáp](docs/VAN_DAP.md)
- [Đối chiếu code với báo cáo](docs/REPORT_CODE_MAP.md)
- [Những phần bổ sung](docs/CHANGES.md)
- [Trạng thái kiểm thử trung thực](docs/TEST_STATUS.md)
