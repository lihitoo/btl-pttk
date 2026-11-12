package model;

import java.math.BigDecimal;

public class Ingredient6 {
    private Long id;
    private String name;
    private String unit;
    private BigDecimal stockQuantity;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public String getName() { return name; }
    public void setName(String value) { name = value; }
    public String getUnit() { return unit; }
    public void setUnit(String value) { unit = value; }
    public BigDecimal getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(BigDecimal value) { stockQuantity = value; }
}
