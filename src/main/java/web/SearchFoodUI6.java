package web;
import config.Database6;
import dao.FoodDAO6;
import model.Food6;
import util.Validation6;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/customer/search")
public class SearchFoodUI6 extends BaseUI6 {
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String c = request.getContextPath(), keyword = request.getParameter("keyword");
        StringBuilder body = new StringBuilder("<form class=\"filters\" method=\"get\" action=\"")
            .append(Html6.e(c + "/customer/search")).append("\"><div><label for=\"keyword\">Tên món ăn</label>")
            .append("<input id=\"keyword\" name=\"keyword\" maxlength=\"150\" value=\"")
            .append(Html6.e(keyword)).append("\"></div><button type=\"submit\">Tìm kiếm</button></form>");
        if (keyword != null) {
            try {
                keyword = Validation6.keyword(keyword);
                try (Connection con = Database6.open()) {
                    List<Food6> foods = new FoodDAO6(con).searchFoodByName(keyword);
                    body.append("<p>Tìm thấy <strong>").append(foods.size()).append("</strong> món ăn.</p>");
                    if (foods.isEmpty()) body.append("<p>Không tìm thấy món ăn phù hợp.</p>");
                    else {
                        body.append(Html6.table("Mã món", "Tên món ăn", "Giá hiện tại", "Trạng thái", "Thao tác"));
                        for (Food6 f : foods) {
                            body.append("<tr><td>").append(f.getId()).append("</td><td>").append(Html6.e(f.getName()))
                                .append("</td><td>").append(Html6.money(f.getPrice())).append("</td><td>")
                                .append(Html6.e(f.getStatus())).append("</td><td>")
                                .append(Html6.link(Html6.url(c, "/customer/food", "id", f.getId(), "keyword", keyword), "Xem chi tiết"))
                                .append("</td></tr>");
                        }
                        body.append(Html6.endTable());
                    }
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(400);
                body.append(Html6.error(e.getMessage()));
            }
        }
        body.append("<p>").append(Html6.link(c + "/customer/home", "Quay lại trang chủ")).append("</p>");
        show(request, response, "Tìm kiếm thông tin món ăn", body.toString());
    }
}
