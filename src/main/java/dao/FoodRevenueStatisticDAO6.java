package dao;
import model.FoodRevenueStatistic6;
import util.Validation6;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FoodRevenueStatisticDAO6 extends DAO6 {
    public FoodRevenueStatisticDAO6(Connection con) { super(con); }

    public List<FoodRevenueStatistic6> getFoodRevenueStatistic(
            LocalDateTime startTime, LocalDateTime endTime) throws SQLException {
        Validation6.range(startTime, endTime);
        String sql = """
            SELECT f.id, f.name, f.price, f.description, f.status,
                   SUM(d.quantity * d.unitPrice) AS revenue
            FROM Food6 f
            JOIN OrderDetail6 d ON d.foodId = f.id
            JOIN Order6 o ON o.id = d.orderId
            JOIN Invoice6 i ON i.orderId = o.id
            WHERE i.status = 'PAID'
              AND i.paidAt >= ? AND i.paidAt < ?
            GROUP BY f.id, f.name, f.price, f.description, f.status
            ORDER BY revenue DESC, f.id ASC
            """;
        List<FoodRevenueStatistic6> result = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startTime));
            ps.setTimestamp(2, Timestamp.valueOf(endTime));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FoodRevenueStatistic6 f = new FoodRevenueStatistic6();
                    f.setId(rs.getLong("id"));
                    f.setName(rs.getString("name"));
                    f.setPrice(rs.getBigDecimal("price"));
                    f.setDescription(rs.getString("description"));
                    f.setStatus(rs.getString("status"));
                    f.setRevenue(rs.getBigDecimal("revenue"));
                    result.add(f);
                }
            }
        }
        return result;
    }
}
