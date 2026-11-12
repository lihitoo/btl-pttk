package config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database6 {
    private Database6() { }

    // Servlet mở một kết nối cho mỗi request và tự đóng bằng try-with-resources.
    public static Connection open() throws SQLException {
        String driver = setting("RESTMAN_DB_DRIVER", "com.mysql.cj.jdbc.Driver");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy JDBC driver: " + driver, e);
        }
        return DriverManager.getConnection(
            setting("RESTMAN_DB_URL", "jdbc:mysql://127.0.0.1:3306/restman6"
                + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Bangkok"
                + "&connectionTimeZone=Asia/Bangkok&allowPublicKeyRetrieval=true&useSSL=false"),
            setting("RESTMAN_DB_USER", "restman6"), setting("RESTMAN_DB_PASSWORD", ""));
    }

    private static String setting(String name, String fallback) {
        String value = System.getProperty(name); // Cho bộ test chạy riêng.
        if (value == null) value = System.getenv(name);
        return value == null ? fallback : value;
    }
}
