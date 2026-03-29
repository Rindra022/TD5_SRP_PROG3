package hei.td5_srp_prog3.dto;

public class IngredientDTO {
    private Integer id;
    private String name;
    private String category;
    private Double price;

    public IngredientDTO() {}

    public IngredientDTO(Integer id, String name, String category, Double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}