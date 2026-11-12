package dao;
import java.sql.Connection;
import java.util.Objects;

public abstract class DAO6 {
    protected final Connection con;
    protected DAO6(Connection con) {
        this.con = Objects.requireNonNull(con);
    }
}
