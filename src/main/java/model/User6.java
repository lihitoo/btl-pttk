package model;


public abstract class User6 {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private Account6 account;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public String getFullName() { return fullName; }
    public void setFullName(String value) { fullName = value; }
    public String getPhone() { return phone; }
    public void setPhone(String value) { phone = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { email = value; }
    public Account6 getAccount() { return account; }
    public void setAccount(Account6 value) { account = value; }
}
