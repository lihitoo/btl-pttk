package model;
import java.math.BigDecimal;

import java.time.LocalDateTime;

public class Invoice6 {
    private Long id;
    private Order6 order;
    private SalesStaff6 paidBy;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String paymentMethod;
    private String status;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Order6 getOrder() { return order; }
    public void setOrder(Order6 value) { order = value; }
    public SalesStaff6 getPaidBy() { return paidBy; }
    public void setPaidBy(SalesStaff6 value) { paidBy = value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime value) { createdAt = value; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime value) { paidAt = value; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String value) { paymentMethod = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }

    public BigDecimal getTotalAmount() {
        return order.getTotalAmount();
    }
}
