package hei.td5_srp_prog3.controller;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    // GET /dishes
    @GetMapping
    public ResponseEntity<List<DishDTO>> getAll() {
        return ResponseEntity.ok(dishService.getAll());
    }

    // PUT /dishes/{id}/ingredients
    @PutMapping("/{id}/ingredients")
    public ResponseEntity<DishDTO> updateIngredients(
            @PathVariable Integer id,
            @RequestBody(required = false) List<DishIngredientRequest> body) {

        return ResponseEntity.ok(dishService.updateIngredients(id, body));
    }
}