package model;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ImportReceipt6 {
    private Long id;
    private Supplier6 supplier;
    private WarehouseStaff6 createdBy;
    private LocalDateTime importDate;
    private List<ImportReceiptDetail6> details = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Supplier6 getSupplier() { return supplier; }
    public void setSupplier(Supplier6 value) { supplier = value; }
    public WarehouseStaff6 getCreatedBy() { return createdBy; }
    public void setCreatedBy(WarehouseStaff6 value) { createdBy = value; }
    public LocalDateTime getImportDate() { return importDate; }
    public void setImportDate(LocalDateTime value) { importDate = value; }
    public List<ImportReceiptDetail6> getDetails() { return details; }
    public void setDetails(List<ImportReceiptDetail6> value) { details = value; }

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (ImportReceiptDetail6 d : details) {
            total = total.add(d.getQuantity().multiply(d.getUnitPrice()));
        }
        return total;
    }
}
