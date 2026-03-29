package hei.td5_srp_prog3.dto;

public class DishIngredientRequest {
    private Integer id;
    private String name;
    private String category;
    private Double price;
    private Double quantityRequired;
    private String unit;

    public DishIngredientRequest() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getQuantityRequired() { return quantityRequired; }
    public void setQuantityRequired(Double quantityRequired) { this.quantityRequired = quantityRequired; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}