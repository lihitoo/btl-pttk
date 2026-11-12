package dao;
import model.*;
import util.Validation6;
import java.sql.*;
import java.util.Optional;

public class InvoiceDAO6 extends DAO6 {
    public InvoiceDAO6(Connection con) { super(con); }

    // Đọc đầu hóa đơn và tất cả dòng món trên cùng một snapshot.
    public Optional<Invoice6> getInvoice(Long orderId) throws SQLException {
        Validation6.positive(orderId);
        boolean autoCommit = con.getAutoCommit();
        int isolation = con.getTransactionIsolation();
        if (!autoCommit) throw new SQLException("getInvoice cần một Connection mới.");
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            con.setAutoCommit(false);
            Optional<Invoice6> result = load(orderId);
            con.commit();
            return result;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(autoCommit);
            con.setTransactionIsolation(isolation);
        }
    }

    private Optional<Invoice6> load(Long orderId) throws SQLException {
        String head = """
            SELECT i.id,i.createdAt,i.paidAt,i.paidById,i.paymentMethod,i.status,
                   o.createdAt AS orderCreated,o.orderType,o.status AS orderStatus,
                   o.customerId,o.tableId,o.handledById,u.fullName AS customerName,
                   t.tableNumber,p.fullName AS staffName
            FROM Invoice6 i JOIN Order6 o ON o.id=i.orderId
            JOIN User6 u ON u.id=o.customerId
            LEFT JOIN DiningTable6 t ON t.id=o.tableId
            LEFT JOIN User6 p ON p.id=i.paidById
            WHERE i.orderId=?
            """;
        Invoice6 inv;
        try (PreparedStatement ps = con.prepareStatement(head)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Customer6 customer = new Customer6();
                customer.setId(rs.getLong("customerId"));
                customer.setFullName(rs.getString("customerName"));
                Order6 order = new Order6();
                order.setId(orderId);
                order.setCustomer(customer);
                order.setCreatedAt(rs.getTimestamp("orderCreated").toLocalDateTime());
                order.setOrderType(rs.getString("orderType"));
                order.setStatus(rs.getString("orderStatus"));
                if (rs.getObject("tableId") != null) {
                    DiningTable6 table = new DiningTable6();
                    table.setId(rs.getLong("tableId"));
                    table.setTableNumber(rs.getString("tableNumber"));
                    order.setTable(table);
                }
                if (rs.getObject("handledById") != null) {
                    SalesStaff6 handler = new SalesStaff6();
                    handler.setId(rs.getLong("handledById"));
                    order.setHandledBy(handler);
                }
                inv = new Invoice6();
                inv.setId(rs.getLong("id"));
                inv.setOrder(order);
                inv.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                Timestamp paid = rs.getTimestamp("paidAt");
                inv.setPaidAt(paid == null ? null : paid.toLocalDateTime());
                inv.setStatus(rs.getString("status"));
                inv.setPaymentMethod(rs.getString("paymentMethod"));
                if (rs.getObject("paidById") != null) {
                    SalesStaff6 staff = new SalesStaff6();
                    staff.setId(rs.getLong("paidById"));
                    staff.setFullName(rs.getString("staffName"));
                    inv.setPaidBy(staff);
                }
                order.setInvoice(inv);
            }
        }
        // Đoạn nạp dòng món giữ nguyên từ báo cáo (Câu 4.b).
        String detail = "SELECT d.id AS detailId,d.calledAt,d.quantity,"
            + "d.unitPrice,f.id,f.name,f.price,f.description,f.status "
            + "FROM OrderDetail6 d JOIN Food6 f ON f.id=d.foodId "
            + "WHERE d.orderId=? ORDER BY d.id";
        try (PreparedStatement ps = con.prepareStatement(detail)) {
            ps.setLong(1, orderId);
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    OrderDetail6 d = new OrderDetail6();
                    d.setId(r.getLong("detailId"));
                    d.setOrder(inv.getOrder());
                    d.setFood(FoodDAO6.map(r));
                    d.setCalledAt(r.getTimestamp("calledAt").toLocalDateTime());
                    d.setQuantity(r.getInt("quantity"));
                    d.setUnitPrice(r.getBigDecimal("unitPrice"));
                    inv.getOrder().getDetails().add(d);
                }
            }
        }
        return Optional.of(inv);
    }
}
