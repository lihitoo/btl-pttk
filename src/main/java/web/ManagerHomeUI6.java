package web;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/manager/home")
public class ManagerHomeUI6 extends BaseUI6 {
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        show(request, response, "Trang chủ quản lí", "<p>Xin chào " + Html6.e(user(request).getFullName())
            + ".</p><p>" + Html6.link(request.getContextPath() + "/manager/reports", "Xem báo cáo") + "</p>");
    }
}
