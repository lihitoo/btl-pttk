package web;
import config.Database6;
import dao.FoodRevenueStatisticDAO6;
import model.FoodRevenueStatistic6;
import util.Validation6;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/manager/statistics")
public class FoodRevenueStatisticUI6 extends BaseUI6 {
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String c = request.getContextPath(), start = request.getParameter("start"), end = request.getParameter("end");
        // Ngày mặc định giúp tái hiện số liệu tháng 10/2026 trong báo cáo, không hard-code kết quả.
        String a = start == null ? "2026-10-01" : start, b = end == null ? "2026-10-31" : end;
        StringBuilder body = new StringBuilder("<form class=\"filters\" method=\"get\" action=\"")
            .append(Html6.e(c + "/manager/statistics")).append("\"><div><label for=\"start\">Ngày bắt đầu</label>")
            .append("<input id=\"start\" name=\"start\" type=\"date\" required value=\"").append(Html6.e(a))
            .append("\"></div><div><label for=\"end\">Ngày kết thúc</label>")
            .append("<input id=\"end\" name=\"end\" type=\"date\" required value=\"").append(Html6.e(b))
            .append("\"></div><button type=\"submit\">Xem thống kê</button></form>");
        if (start != null || end != null) {
            try {
                LocalDate[] dates = Validation6.dates(start, end);
                LocalDateTime startTime = dates[0].atStartOfDay();
                LocalDateTime endTime = dates[1].plusDays(1).atStartOfDay();
                try (Connection con = Database6.open()) {
                    List<FoodRevenueStatistic6> rows = new FoodRevenueStatisticDAO6(con)
                        .getFoodRevenueStatistic(startTime, endTime);
                    body.append("<p class=\"muted\">Hóa đơn PAID · thời điểm thanh toán từ ")
                        .append(dates[0]).append(" đến hết ").append(dates[1]).append(".</p>");
                    if (rows.isEmpty()) body.append("<p>Không có dữ liệu thống kê trong khoảng thời gian này.</p>");
                    body.append(Html6.table("Mã món", "Tên món ăn", "Doanh thu", "Chi tiết"));
                    BigDecimal total = BigDecimal.ZERO;
                    for (FoodRevenueStatistic6 f : rows) {
                        total = total.add(f.getRevenue());
                        body.append("<tr><td>").append(f.getId()).append("</td><td>").append(Html6.e(f.getName()))
                            .append("</td><td>").append(Html6.money(f.getRevenue())).append("</td><td>")
                            .append(Html6.link(Html6.url(c, "/manager/history", "foodId", f.getId(),
                                "start", dates[0], "end", dates[1]), "Các lần gọi món")).append("</td></tr>");
                    }
                    body.append(Html6.endTable()).append("<p class=\"total\">Tổng doanh thu: ")
                        .append(Html6.money(total)).append("</p>");
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(400);
                body.append(Html6.error(e.getMessage()));
            }
        } else body.append("<p>Chọn khoảng ngày và bấm Xem thống kê.</p>");
        body.append(Html6.link(c + "/manager/reports", "Quay lại chọn báo cáo"));
        show(request, response, "Thống kê món ăn theo doanh thu", body.toString());
    }
}
