# Kịch bản demo đề 23

## Chuẩn bị

Ứng dụng đã khởi động, nạp dữ liệu `seed6.sql`; mở `/restman6/login`. Dùng dữ liệu mẫu, không dùng thông tin khách hàng thật. Mở sẵn IDE ở `src/main/java` và báo cáo BTL 41 trang.

## 1. Khách hàng tìm món

Đăng nhập `customer6 / Customer@123`. Chọn **Tìm kiếm thông tin món ăn**, nhập **Cơm** rồi tìm.

Kết quả: một dòng **Cơm gà**, giá hiện tại **65.000 đ**, trạng thái `AVAILABLE`. Chọn **Xem chi tiết**; nhấn quay lại để chứng minh từ khóa được giữ.

Thử từ khóa không tồn tại và chuỗi toàn khoảng trắng: kết quả rỗng khác với đầu vào không hợp lệ. Đăng xuất.

Khi được hỏi code: mở `SearchFoodUI6.execute()` → `FoodDAO6.searchFoodByName()` → `FoodDAO6.map()`. Chỉ vào `PreparedStatement`, tham số `?` và câu `LIKE`.

## 2. Quản lí xem thống kê

Đăng nhập `manager6 / Manager@123`. Chọn **Xem báo cáo** → **Thống kê món ăn theo doanh thu**. Chọn **01/10/2026–31/10/2026**, nhấn xem.

Đối chiếu: Cơm gà 245.000 đ; Phở bò 170.000 đ; Nước cam 90.000 đ; tổng **505.000 đ**.

Chọn Cơm gà. Có ba dòng: `1001`, `1003`, `1005`. Hai dòng đầu cùng đơn 101 nhưng là hai lần gọi, không gộp lại.

Mở dòng `1001`: hóa đơn **#201**, đơn **#101**, bàn **B01**. Hóa đơn có đủ Cơm gà và Nước cam, ba dòng, tổng **210.000 đ**. Dòng `1001` được tô màu. Nhấn quay lại hai lần: khoảng ngày và kết quả được giữ.

Khi được hỏi code: mở `FoodRevenueStatisticDAO6`, `OrderDetailDAO6`, `InvoiceDAO6`; cuối cùng chỉ vào `Order6.getTotalAmount()`.

## 3. Tình huống kiểm tra

- Đổi ngày bắt đầu sau ngày kết thúc: báo lỗi, không truy vấn dữ liệu.
- Chọn tháng 01/2025: không có dữ liệu, tổng bằng 0.
- Thử hóa đơn chưa thanh toán: đơn 103 không được tính vào doanh thu tháng 10.
- Đăng nhập khách hàng rồi nhập URL `/restman6/manager/statistics`: bị chặn 403.

## Những điểm cần giải thích chính xác

**Giá hiện tại khác giá bán:** Cơm gà đang có giá 65.000 đ, nhưng hai lần gọi ở đơn 101 có đơn giá 60.000 đ. Thống kê dùng giá đã bán trong `OrderDetail6`, không dùng giá đang niêm yết.

**Mốc thời gian:** đơn 105 gọi lúc 31/10 nhưng thanh toán đúng 00:00 ngày 01/11, nên không thuộc doanh thu tháng 10. Điều kiện dựa vào `Invoice6.paidAt`, không dựa vào `calledAt`.

**Phạm vi demo:** hai module được giao đã có giao diện và truy vấn dữ liệu. Các nghiệp vụ nhập kho/đặt bàn/gọi món có mô hình và dữ liệu mẫu, chưa có màn hình để thực hiện. Không nói rằng đã cài đặt toàn bộ use case của nhà hàng.
