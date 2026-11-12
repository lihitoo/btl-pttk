package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order6 {
    private Long id;
    private Customer6 customer;
    private DiningTable6 table;
    private SalesStaff6 handledBy;
    private LocalDateTime createdAt;
    private String orderType;
    private String status;
    private List<OrderDetail6> details = new ArrayList<>();
    private Invoice6 invoice;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Customer6 getCustomer() { return customer; }
    public void setCustomer(Customer6 value) { customer = value; }
    public DiningTable6 getTable() { return table; }
    public void setTable(DiningTable6 value) { table = value; }
    public SalesStaff6 getHandledBy() { return handledBy; }
    public void setHandledBy(SalesStaff6 value) { handledBy = value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime value) { createdAt = value; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String value) { orderType = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public List<OrderDetail6> getDetails() { return details; }
    public void setDetails(List<OrderDetail6> value) { details = value; }
    public Invoice6 getInvoice() { return invoice; }
    public void setInvoice(Invoice6 value) { invoice = value; }

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderDetail6 d : details) total = total.add(d.getLineAmount());
        return total;
    }
}
