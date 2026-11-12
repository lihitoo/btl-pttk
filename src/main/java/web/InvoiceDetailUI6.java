package web;
import config.Database6;
import dao.InvoiceDAO6;
import model.*;
import util.Validation6;
import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/manager/invoice")
public class InvoiceDetailUI6 extends BaseUI6 {
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        Long orderId = Validation6.id(request.getParameter("orderId"));
        Long detailId = Validation6.id(request.getParameter("detailId"));
        Long foodId = Validation6.id(request.getParameter("foodId"));
        LocalDate[] dates = Validation6.dates(request.getParameter("start"), request.getParameter("end"));
        LocalDateTime startTime = dates[0].atStartOfDay(), endTime = dates[1].plusDays(1).atStartOfDay();
        try (Connection con = Database6.open()) {
            Optional<Invoice6> result = new InvoiceDAO6(con).getInvoice(orderId);
            if (result.isEmpty()) { error(request, response, 404, "Hóa đơn không tồn tại."); return; }
            Invoice6 inv = result.get();
            // Không tin các id và ngày người dùng sửa trên URL.
            boolean found = false;
            for (OrderDetail6 d : inv.getOrder().getDetails()) {
                if (d.getId().equals(detailId) && d.getFood().getId().equals(foodId)) found = true;
            }
            if (!found || !"PAID".equals(inv.getStatus()) || inv.getPaidAt() == null
                    || inv.getPaidAt().isBefore(startTime) || !inv.getPaidAt().isBefore(endTime)) {
                error(request, response, 404, "Lần gọi món không thuộc hóa đơn hoặc bộ lọc đã chọn.");
                return;
            }
            Order6 order = inv.getOrder();
            StringBuilder body = new StringBuilder("<div class=\"info\"><p><strong>Hóa đơn #")
                .append(inv.getId()).append("</strong> · Đơn #").append(orderId)
                .append(" · <span class=\"badge\">").append(Html6.e(inv.getStatus())).append("</span></p>")
                .append("<p>Khách hàng: ").append(Html6.e(order.getCustomer().getFullName())).append(" · Bàn: ")
                .append(order.getTable() == null ? "Chưa gán" : Html6.e(order.getTable().getTableNumber())).append("</p>")
                .append("<p>Thanh toán: ").append(Html6.time(inv.getPaidAt())).append(" · ")
                .append(Html6.e(inv.getPaymentMethod())).append("</p><p>Nhân viên: ")
                .append(inv.getPaidBy() == null ? "" : Html6.e(inv.getPaidBy().getFullName())).append("</p></div>")
                .append(Html6.table("Mã dòng", "Món ăn", "Số lượng", "Đơn giá", "Thành tiền"));
            for (OrderDetail6 d : order.getDetails()) {
                body.append(d.getId().equals(detailId) ? "<tr class=\"selected\">" : "<tr>")
                    .append("<td>").append(d.getId()).append("</td><td>").append(Html6.e(d.getFood().getName()))
                    .append("</td><td>").append(d.getQuantity()).append("</td><td>").append(Html6.money(d.getUnitPrice()))
                    .append("</td><td>").append(Html6.money(d.getLineAmount())).append("</td></tr>");
            }
            body.append(Html6.endTable()).append("<p class=\"total\">Tổng hóa đơn: ")
                .append(Html6.money(inv.getTotalAmount())).append("</p>")
                .append("<p class=\"muted\">Dòng tô màu là lần gọi được chọn. Hóa đơn hiển thị đầy đủ tất cả các món.</p>")
                .append(Html6.link(Html6.url(request.getContextPath(), "/manager/history", "foodId", foodId,
                    "start", dates[0], "end", dates[1]), "Quay lại các lần gọi món"));
            show(request, response, "Chi tiết hóa đơn", body.toString());
        }
    }
}
