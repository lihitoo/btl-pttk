package web;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/home")
public class HomeUI6 extends BaseUI6 {
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String role = user(request).getRole();
        if (role.equals("CUSTOMER")) response.sendRedirect(request.getContextPath() + "/customer/home");
        else if (role.equals("MANAGER")) response.sendRedirect(request.getContextPath() + "/manager/home");
        else show(request, response, "Trang chủ", "<p>Vai trò này chưa có module demo.</p>");
    }
}
