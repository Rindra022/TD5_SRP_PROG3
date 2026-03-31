package hei.td5_srp_prog3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockMovementDTO {
    private Integer id;
    private Instant creationDateTime;
    private String unit;
    private Double value;
    private String type;
}