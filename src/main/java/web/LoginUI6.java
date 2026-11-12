package web;
import config.Database6;
import dao.AccountDAO6;
import util.SessionUser6;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/login")
public class LoginUI6 extends BaseUI6 {
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        if (user(request) != null) { response.sendRedirect(request.getContextPath() + "/home"); return; }
        String message = "";
        String username = request.getParameter("username");
        if ("POST".equals(request.getMethod())) {
            checkCsrf(request);
            try (Connection con = Database6.open()) {
                Optional<SessionUser6> result = new AccountDAO6(con).authenticate(username, request.getParameter("password"));
                if (result.isPresent()) {
                    request.getSession().invalidate();
                    HttpSession session = request.getSession(true);
                    session.setAttribute("user6", result.get());
                    csrf(request);
                    response.sendRedirect(request.getContextPath() + "/home");
                    return;
                }
            }
            response.setStatus(401);
            message = Html6.error("Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản bị khóa.");
        }
        String body = message + "<form class=\"login-form\" method=\"post\" action=\""
            + Html6.e(request.getContextPath() + "/login") + "\">"
            + "<input type=\"hidden\" name=\"csrf\" value=\"" + Html6.e(csrf(request)) + "\">"
            + "<label for=\"username\">Tên đăng nhập</label>"
            + "<input id=\"username\" name=\"username\" maxlength=\"80\" required autocomplete=\"username\" value=\""
            + Html6.e(username) + "\">"
            + "<label for=\"password\">Mật khẩu</label>"
            + "<input id=\"password\" name=\"password\" type=\"password\" maxlength=\"128\" required autocomplete=\"current-password\">"
            + "<button type=\"submit\">Đăng nhập</button></form>";
        show(request, response, "Đăng nhập", body);
    }
}
