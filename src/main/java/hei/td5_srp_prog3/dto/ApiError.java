package hei.td5_srp_prog3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {
    private int status;
    private String message;
}