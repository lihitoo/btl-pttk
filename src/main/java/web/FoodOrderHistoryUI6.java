package web;
import config.Database6;
import dao.FoodDAO6;
import dao.OrderDetailDAO6;
import model.*;
import util.Validation6;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/manager/history")
public class FoodOrderHistoryUI6 extends BaseUI6 {
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String c = request.getContextPath();
        Long foodId = Validation6.id(request.getParameter("foodId"));
        LocalDate[] dates = Validation6.dates(request.getParameter("start"), request.getParameter("end"));
        try (Connection con = Database6.open()) {
            Optional<Food6> food = new FoodDAO6(con).getFoodDetail(foodId);
            if (food.isEmpty()) { error(request, response, 404, "Món ăn không tồn tại."); return; }
            List<OrderDetail6> rows = new OrderDetailDAO6(con).getFoodOrderHistory(foodId,
                dates[0].atStartOfDay(), dates[1].plusDays(1).atStartOfDay());
            StringBuilder body = new StringBuilder("<p>Bộ lọc thanh toán: ")
                .append(dates[0]).append(" → ").append(dates[1]).append("</p><h2>")
                .append(Html6.e(food.get().getName())).append("</h2>");
            if (rows.isEmpty()) body.append("<p>Không có lần gọi món nào trong khoảng đã chọn.</p>");
            body.append(Html6.table("Mã dòng", "Mã đơn", "Thời gian gọi", "SL", "Đơn giá", "Thành tiền", "Hóa đơn"));
            BigDecimal total = BigDecimal.ZERO;
            for (OrderDetail6 d : rows) {
                total = total.add(d.getLineAmount());
                body.append("<tr><td>").append(d.getId()).append("</td><td>").append(d.getOrder().getId())
                    .append("</td><td>").append(Html6.time(d.getCalledAt())).append("</td><td>").append(d.getQuantity())
                    .append("</td><td>").append(Html6.money(d.getUnitPrice())).append("</td><td>")
                    .append(Html6.money(d.getLineAmount())).append("</td><td>")
                    .append(Html6.link(Html6.url(c, "/manager/invoice", "orderId", d.getOrder().getId(),
                        "detailId", d.getId(), "foodId", foodId, "start", dates[0], "end", dates[1]), "Xem hóa đơn"))
                    .append("</td></tr>");
            }
            body.append(Html6.endTable()).append("<p class=\"total\">Tổng của món: ").append(Html6.money(total)).append("</p>")
                .append(Html6.link(Html6.url(c, "/manager/statistics", "start", dates[0], "end", dates[1]), "Quay lại thống kê"));
            show(request, response, "Các lần món được gọi", body.toString());
        }
    }
}
