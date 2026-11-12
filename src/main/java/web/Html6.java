package web;
import util.SessionUser6;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Html6 {
    private Html6() { }

    public static String e(Object value) {
        if (value == null) return "";
        return value.toString().replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
    public static String money(BigDecimal amount) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        format.setMaximumFractionDigits(2);
        return format.format(amount) + " đ";
    }
    public static String time(LocalDateTime value) {
        return value == null ? "Chưa thanh toán" : value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    public static String url(String context, String path, Object... params) {
        StringBuilder out = new StringBuilder(context).append(path);
        for (int i = 0; i < params.length; i += 2) {
            out.append(i == 0 ? '?' : '&').append(params[i]).append('=')
                .append(URLEncoder.encode(String.valueOf(params[i + 1]), StandardCharsets.UTF_8));
        }
        return out.toString();
    }
    public static String link(String href, String label) {
        return "<a href=\"" + e(href) + "\">" + e(label) + "</a>";
    }
    public static String error(String message) {
        return "<p class=\"error\" role=\"alert\">" + e(message) + "</p>";
    }
    public static String table(String... headings) {
        StringBuilder out = new StringBuilder("<div class=\"table-wrap\"><table><thead><tr>");
        for (String h : headings) out.append("<th scope=\"col\">").append(e(h)).append("</th>");
        return out.append("</tr></thead><tbody>").toString();
    }
    public static String endTable() { return "</tbody></table></div>"; }

    // Chỉ nhận content đã được tạo trong Servlet; dữ liệu ngoài phải qua e().
    public static String document(String title, String content, SessionUser6 user, String csrf, String context) {
        String account = user == null ? "" : "<span>" + e(user.getFullName()) + " · " + e(user.getRole()) + "</span>"
            + "<form method=\"post\" action=\"" + e(context + "/logout") + "\">"
            + "<input type=\"hidden\" name=\"csrf\" value=\"" + e(csrf) + "\">"
            + "<button class=\"logout\" type=\"submit\">Đăng xuất</button></form>";
        return "<!doctype html><html lang=\"vi\"><head><meta charset=\"UTF-8\">"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
            + "<title>" + e(title) + " | RestMan6</title>"
            + "<link rel=\"stylesheet\" href=\"" + e(context + "/assets/style6.css") + "\"></head><body>"
            + "<header><a class=\"brand\" href=\"" + e(context + "/home") + "\">RestMan6</a>"
            + "<div class=\"account\">" + account + "</div></header><main>"
            + "<div class=\"eyebrow\">Đề 23 · B23DCCN876</div><h1>" + e(title) + "</h1>"
            + content + "</main><footer>Bài tập lớn RestMan · Dữ liệu minh họa · 01 sinh viên thực hiện</footer>"
            + "</body></html>";
    }
}
