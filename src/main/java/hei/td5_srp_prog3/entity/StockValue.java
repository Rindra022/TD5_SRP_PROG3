package hei.td5_srp_prog3.entity;

import hei.td5_srp_prog3.type.Unit;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class StockValue {
    private Double quantity;
    private Unit unit;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockValue that = (StockValue) o;
        return Objects.equals(quantity, that.quantity) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, unit);
    }

    @Override
    public String toString() {
        return "StockValue{" +
                "quantity=" + quantity +
                ", unit=" + unit +
                '}';
    }
}
