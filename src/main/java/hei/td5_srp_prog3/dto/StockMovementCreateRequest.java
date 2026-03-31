package hei.td5_srp_prog3.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockMovementCreateRequest {
    private String unit;
    private Double value;
    private String type;
}