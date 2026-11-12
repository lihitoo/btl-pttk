package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Combo6 {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String status;
    private List<ComboDetail6> details = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public String getName() { return name; }
    public void setName(String value) { name = value; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal value) { price = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public List<ComboDetail6> getDetails() { return details; }
    public void setDetails(List<ComboDetail6> value) { details = value; }
}
