package hei.td5_srp_prog3.controller;


import hei.td5_srp_prog3.dto.IngredientDTO;
import hei.td5_srp_prog3.dto.StockMovementCreateRequest;
import hei.td5_srp_prog3.dto.StockMovementDTO;
import hei.td5_srp_prog3.dto.StockValueDTO;
import hei.td5_srp_prog3.service.IngredientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getAll() {
        return ResponseEntity.ok(ingredientService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ingredientService.getById(id));
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<StockValueDTO> getStock(
            @PathVariable Integer id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit) {

        return ResponseEntity.ok(ingredientService.getStockAt(id, at, unit));
    }

    // f) GET /ingredients/{id}/stockMovements?from=...&to=...
    @GetMapping("/{id}/stockMovements")
    public ResponseEntity<List<StockMovementDTO>> getStockMovements(
            @PathVariable Integer id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        return ResponseEntity.ok(ingredientService.getStockMovements(id, from, to));
    }

    // g) POST /ingredients/{id}/stockMovements
    @PostMapping("/{id}/stockMovements")
    public ResponseEntity<List<StockMovementDTO>> addStockMovements(
            @PathVariable Integer id,
            @RequestBody(required = false) List<StockMovementCreateRequest> body) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ingredientService.addStockMovements(id, body));
    }
}