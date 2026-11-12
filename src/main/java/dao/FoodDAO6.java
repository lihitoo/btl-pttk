package dao;
import model.Food6;
import util.Validation6;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FoodDAO6 extends DAO6 {
    public FoodDAO6(Connection con) { super(con); }

    // Giữ nguyên phương thức và SQL trong Câu 4.b của báo cáo.
    public List<Food6> searchFoodByName(String keyword)
            throws SQLException {
        String pattern = Validation6.likePattern(keyword);
        String sql = "SELECT id,name,price,description,status "
            + "FROM Food6 WHERE LOWER(name) LIKE LOWER(?) "
            + "ESCAPE '!' ORDER BY name,id";
        List<Food6> result = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
        }
        return result;
    }

    public Optional<Food6> getFoodDetail(Long foodId)
            throws SQLException {
        Validation6.positive(foodId);
        String sql = "SELECT id,name,price,description,status "
                   + "FROM Food6 WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, foodId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public static Food6 map(ResultSet rs) throws SQLException {
        Food6 f = new Food6();
        f.setId(rs.getLong("id"));
        f.setName(rs.getString("name"));
        f.setPrice(rs.getBigDecimal("price"));
        f.setDescription(rs.getString("description"));
        f.setStatus(rs.getString("status"));
        return f;
    }
}
