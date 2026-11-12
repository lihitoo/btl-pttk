package web;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/logout")
public class LogoutUI6 extends BaseUI6 {
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (!"POST".equals(request.getMethod())) {
            error(request, response, 405, "Hãy dùng nút Đăng xuất.");
            return;
        }
        checkCsrf(request);
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
