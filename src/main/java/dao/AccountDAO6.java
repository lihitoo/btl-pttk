package dao;
import util.Password6;
import util.SessionUser6;
import java.sql.*;
import java.util.Optional;

public class AccountDAO6 extends DAO6 {
    public AccountDAO6(Connection con) { super(con); }

    public Optional<SessionUser6> authenticate(String username, String password) throws SQLException {
        if (username == null || username.isBlank() || username.length() > 80
                || password == null || password.isEmpty() || password.length() > 128)
            return Optional.empty();
        String sql = "SELECT u.id,u.fullName,a.role,a.passwordHash FROM Account6 a "
            + "JOIN User6 u ON u.id=a.userId WHERE a.username=? AND a.status='ACTIVE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && Password6.verify(password, rs.getString("passwordHash"))) {
                    return Optional.of(new SessionUser6(rs.getLong("id"),
                        rs.getString("fullName"), rs.getString("role")));
                }
                return Optional.empty();
            }
        }
    }
}
