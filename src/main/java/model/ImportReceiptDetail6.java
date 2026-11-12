package model;

import java.math.BigDecimal;

public class ImportReceiptDetail6 {
    private Long id;
    private Ingredient6 ingredient;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private ImportReceipt6 receipt;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Ingredient6 getIngredient() { return ingredient; }
    public void setIngredient(Ingredient6 value) { ingredient = value; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal value) { quantity = value; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal value) { unitPrice = value; }
    public ImportReceipt6 getReceipt() { return receipt; }
    public void setReceipt(ImportReceipt6 value) { receipt = value; }
}
