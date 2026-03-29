package hei.td5_srp_prog3.entity;

import Rindra.type.Unit;

public class DishIngredient {
    private Dish dish;
    private Ingredient ingredient;
    private Double quantityRequired;
    private Unit unit;

    public DishIngredient(){}



    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Double getQuantity() {
        return quantityRequired;
    }


    public void setQuantity(Double quantity) {
        this.quantityRequired = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                // !!!=> I delete dish here cause risk of infinite loop due to class dish who already has List<DishIngredient
                ", ingredient=" + ingredient +
                ", quantityRequired=" + quantityRequired +
                ", unit=" + unit +
                '}';
    }
}
