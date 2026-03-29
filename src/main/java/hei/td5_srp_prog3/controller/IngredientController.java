package hei.td5_srp_prog3.controller;


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
}