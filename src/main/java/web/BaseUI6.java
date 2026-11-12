package web;
import util.SessionUser6;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public abstract class BaseUI6 extends HttpServlet {
    protected abstract void execute(HttpServletRequest request, HttpServletResponse response)
        throws SQLException, IOException, ServletException;

    @Override
    protected final void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handle(request, response);
    }
    @Override
    protected final void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handle(request, response);
    }
    private void handle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        try {
            if ("POST".equals(request.getMethod())
                    && !request.getServletPath().equals("/login")
                    && !request.getServletPath().equals("/logout")) {
                error(request, response, 405, "Trang này chỉ nhận yêu cầu GET.");
                return;
            }
            execute(request, response);
        } catch (IllegalArgumentException e) {
            error(request, response, 400, e.getMessage());
        } catch (SecurityException e) {
            error(request, response, 403, "Yêu cầu không hợp lệ. Hãy tải lại trang.");
        } catch (SQLException e) {
            log("Lỗi truy vấn dữ liệu", e);
            error(request, response, 503, "Không thể đọc dữ liệu. Kiểm tra kết nối MySQL và cấu hình máy chủ.");
        }
    }
    protected void show(HttpServletRequest request, HttpServletResponse response, String title, String body)
            throws ServletException, IOException {
        request.setAttribute("title6", title);
        request.setAttribute("content6", body);
        request.getRequestDispatcher("/WEB-INF/views/layout6.jsp").forward(request, response);
    }
    protected void error(HttpServletRequest request, HttpServletResponse response, int status, String message)
            throws ServletException, IOException {
        response.setStatus(status);
        show(request, response, "Thông báo", Html6.error(message)
            + Html6.link(request.getContextPath() + "/home", "Quay lại trang chủ"));
    }
    protected SessionUser6 user(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (SessionUser6) session.getAttribute("user6");
    }
    protected String csrf(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("csrf6");
        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute("csrf6", token);
        }
        return token;
    }
    protected void checkCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String saved = session == null ? null : (String) session.getAttribute("csrf6");
        if (saved == null || !saved.equals(request.getParameter("csrf"))) throw new SecurityException();
    }
}
