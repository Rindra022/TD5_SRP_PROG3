package hei.td5_srp_prog3.dto;


public class StockValueDTO {
    private String unit;
    private Double quantity;

    public StockValueDTO() {}

    public StockValueDTO(String unit, Double quantity) {
        this.unit = unit;
        this.quantity = quantity;
    }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
}