package util;
import java.io.Serializable;

public class SessionUser6 implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Long id;
    private final String fullName;
    private final String role;

    public SessionUser6(Long id, String fullName, String role) {
        this.id = id;
        this.fullName = fullName;
        this.role = role;
    }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
}
