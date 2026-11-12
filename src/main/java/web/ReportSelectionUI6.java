package web;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/manager/reports")
public class ReportSelectionUI6 extends BaseUI6 {
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String c = request.getContextPath();
        String body = "<ul class=\"report-list\"><li>"
            + Html6.link(c + "/manager/statistics", "Thống kê món ăn theo doanh thu")
            + "</li><li class=\"muted\">Thống kê nguyên liệu — chưa cài đặt</li>"
            + "<li class=\"muted\">Thống kê khách hàng — chưa cài đặt</li>"
            + "<li class=\"muted\">Thống kê nhà cung cấp — chưa cài đặt</li></ul>"
            + Html6.link(c + "/manager/home", "Quay lại trang chủ");
        show(request, response, "Chọn báo cáo", body);
    }
}
