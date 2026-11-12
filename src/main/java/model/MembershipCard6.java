package model;

import java.time.LocalDate;

public class MembershipCard6 {
    private Long id;
    private Customer6 customer;
    private SalesStaff6 issuedBy;
    private LocalDate issueDate;
    private String status;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Customer6 getCustomer() { return customer; }
    public void setCustomer(Customer6 value) { customer = value; }
    public SalesStaff6 getIssuedBy() { return issuedBy; }
    public void setIssuedBy(SalesStaff6 value) { issuedBy = value; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate value) { issueDate = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
}
