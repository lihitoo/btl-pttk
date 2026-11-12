package dao;
import model.*;
import util.Validation6;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO6 extends DAO6 {
    public OrderDetailDAO6(Connection con) { super(con); }

    public List<OrderDetail6> getFoodOrderHistory(Long foodId,
            LocalDateTime startTime, LocalDateTime endTime) throws SQLException {
        Validation6.positive(foodId);
        Validation6.range(startTime, endTime);
        String sql = """
            SELECT d.id, d.orderId, d.calledAt, d.quantity, d.unitPrice,
                   f.id AS foodId, f.name, f.price, f.description, f.status
            FROM OrderDetail6 d
            JOIN Food6 f ON f.id = d.foodId
            JOIN Invoice6 i ON i.orderId = d.orderId
            WHERE d.foodId = ? AND i.status = 'PAID'
              AND i.paidAt >= ? AND i.paidAt < ?
            ORDER BY d.calledAt, d.id
            """;
        List<OrderDetail6> result = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, foodId);
            ps.setTimestamp(2, Timestamp.valueOf(startTime));
            ps.setTimestamp(3, Timestamp.valueOf(endTime));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Food6 food = new Food6();
                    food.setId(rs.getLong("foodId"));
                    food.setName(rs.getString("name"));
                    food.setPrice(rs.getBigDecimal("price"));
                    food.setDescription(rs.getString("description"));
                    food.setStatus(rs.getString("status"));
                    Order6 order = new Order6();
                    order.setId(rs.getLong("orderId"));
                    OrderDetail6 line = new OrderDetail6();
                    line.setId(rs.getLong("id"));
                    line.setOrder(order);
                    line.setFood(food);
                    line.setCalledAt(rs.getTimestamp("calledAt").toLocalDateTime());
                    line.setQuantity(rs.getInt("quantity"));
                    line.setUnitPrice(rs.getBigDecimal("unitPrice"));
                    result.add(line);
                }
            }
        }
        return result;
    }
}
