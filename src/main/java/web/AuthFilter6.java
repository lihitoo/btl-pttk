package web;
import util.SessionUser6;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

@WebFilter("/*")
public class AuthFilter6 implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Content-Security-Policy", "default-src 'self'; style-src 'self'; "
            + "form-action 'self'; frame-ancestors 'none'; base-uri 'self'");
        String path = request.getServletPath();
        if (path.startsWith("/assets/")) { chain.doFilter(req, res); return; }
        response.setHeader("Cache-Control", "no-store");
        if (path.equals("/login")) { chain.doFilter(req, res); return; }
        HttpSession session = request.getSession(false);
        SessionUser6 user = session == null ? null : (SessionUser6) session.getAttribute("user6");
        if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }

        // Cùng điều kiện kiểm tra quyền như Câu 4.b.
        boolean denied =
            (path.startsWith("/manager/")
                && !user.getRole().equals("MANAGER"))
            || (path.startsWith("/customer/")
                && !user.getRole().equals("CUSTOMER"));
        if (denied) {
            response.setStatus(403);
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(Html6.document("Không có quyền truy cập",
                Html6.error("Tài khoản không có quyền sử dụng chức năng này.")
                + Html6.link(request.getContextPath() + "/home", "Quay lại trang chủ"),
                user, (String) session.getAttribute("csrf6"), request.getContextPath()));
            return;
        }
        chain.doFilter(req, res);
    }
}
