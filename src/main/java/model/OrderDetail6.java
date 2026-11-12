package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDetail6 {
    private Long id;
    private Order6 order;
    private Food6 food;
    private LocalDateTime calledAt;
    private int quantity;
    private BigDecimal unitPrice;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Order6 getOrder() { return order; }
    public void setOrder(Order6 value) { order = value; }
    public Food6 getFood() { return food; }
    public void setFood(Food6 value) { food = value; }
    public LocalDateTime getCalledAt() { return calledAt; }
    public void setCalledAt(LocalDateTime value) { calledAt = value; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int value) { quantity = value; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal value) { unitPrice = value; }

    public BigDecimal getLineAmount() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
