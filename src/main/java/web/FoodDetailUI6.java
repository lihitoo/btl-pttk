package web;
import config.Database6;
import dao.FoodDAO6;
import model.Food6;
import util.Validation6;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/customer/food")
public class FoodDetailUI6 extends BaseUI6 {
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        Long foodId = Validation6.id(request.getParameter("id"));
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";
        String c = request.getContextPath();
        String back = keyword.isEmpty() ? c + "/customer/search" : Html6.url(c, "/customer/search", "keyword", keyword);
        try (Connection con = Database6.open()) {
            Optional<Food6> result = new FoodDAO6(con).getFoodDetail(foodId);
            if (result.isEmpty()) {
                response.setStatus(404);
                show(request, response, "Chi tiết món ăn", Html6.error("Món ăn không tồn tại.")
                    + Html6.link(back, "Quay lại kết quả tìm kiếm"));
                return;
            }
            Food6 f = result.get();
            String body = "<div class=\"info\"><h2>" + Html6.e(f.getName()) + "</h2><p>Mã món: " + f.getId()
                + "</p><p>Giá hiện tại: <strong>" + Html6.money(f.getPrice()) + "</strong></p>"
                + "<p>Mô tả: " + Html6.e(f.getDescription()) + "</p><p>Trạng thái: " + Html6.e(f.getStatus())
                + "</p></div><p>" + Html6.link(back, "Quay lại kết quả tìm kiếm") + "</p>";
            show(request, response, "Chi tiết món ăn", body);
        }
    }
}
