package hei.td5_srp_prog3.entity;

import hei.td5_srp_prog3.type.CategoryEnum;

import java.time.Instant;
import java.util.List;

public class Ingredient {
    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private List<StockMovement> stockMovementList;

    public Ingredient() {}

    public StockValue getStockValueAt(Instant t, Unit requestedUnit) {
        List<StockMovement> filtered = stockMovementList.stream()
                .filter(stm -> !stm.getCreationDateTime().isAfter(t))
                .toList();

        double quantityValue = 0.0;
        for (StockMovement sm : filtered) {
            if (sm.getType() == MovementTypeEnum.IN) {
                quantityValue += sm.getValue().getQuantity();
            } else if (sm.getType() == MovementTypeEnum.OUT) {
                quantityValue -= sm.getValue().getQuantity();
            }
        }

        StockValue stockValue = new StockValue();
        stockValue.setQuantity(quantityValue);
        stockValue.setUnit(requestedUnit);
        return stockValue;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }
    public List<StockMovement> getStockMovementList() { return stockMovementList; }
    public void setStockMovementList(List<StockMovement> list) { this.stockMovementList = list; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(price, that.price)
                && category == that.category;
    }

    @Override
    public int hashCode() { return Objects.hash(id, name, price, category); }

    @Override
    public String toString() {
        return "Ingredient{id=" + id + ", name='" + name + "', price=" + price + ", category=" + category + '}';
    }
}