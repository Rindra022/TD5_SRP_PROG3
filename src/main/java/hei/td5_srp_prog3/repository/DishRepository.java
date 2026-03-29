package hei.td5_srp_prog3.repository;

@Repository
public class DishRepository {

    private final JdbcTemplate jdbc;

    public DishRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Dish> findAll() {
        String sql = """
                SELECT d.id, d.name, d.dish_type, d.selling_price
                FROM dish d
                ORDER BY d.id
                """;
        List<Dish> dishes = jdbc.query(sql, this::mapDish);
        // Charge les ingrédients pour chaque plat
        for (Dish dish : dishes) {
            dish.setDishIngredients(findDishIngredientsByDishId(dish.getId()));
        }
        return dishes;
    }

    public Optional<Dish> findById(Integer id) {
        String sql = """
                SELECT d.id, d.name, d.dish_type, d.selling_price
                FROM dish d
                WHERE d.id = ?
                """;
        List<Dish> results = jdbc.query(sql, this::mapDish, id);
        if (results.isEmpty()) return Optional.empty();

        Dish dish = results.get(0);
        dish.setDishIngredients(findDishIngredientsByDishId(id));
        return Optional.of(dish);
    }

    public void detachAllIngredients(Integer dishId) {
        String sql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        jdbc.update(sql, dishId);
    }

    public void attachIngredients(Integer dishId, List<DishIngredient> dishIngredients) {
        String sql = """
                INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, ?, ?::unit_type)
                """;
        for (DishIngredient di : dishIngredients) {
            jdbc.update(sql,
                    dishId,
                    di.getIngredient().getId(),
                    di.getQuantity(),
                    di.getUnit().name());
        }
    }

    private List<DishIngredient> findDishIngredientsByDishId(Integer dishId) {
        String sql = """
                SELECT di.id_dish, di.id_ingredient, di.quantity_required, di.unit,
                       i.id AS ing_id, i.name AS ing_name, i.price, i.category
                FROM dish_ingredient di
                JOIN ingredient i ON i.id = di.id_ingredient
                WHERE di.id_dish = ?
                """;
        return jdbc.query(sql, this::mapDishIngredient, dishId);
    }

    private Dish mapDish(ResultSet rs, int rowNum) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getInt("id"));
        dish.setName(rs.getString("name"));
        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));

        double sellingPrice = rs.getDouble("selling_price");
        dish.setSellingPrice(rs.wasNull() ? null : sellingPrice);

        dish.setDishIngredients(new ArrayList<>());
        return dish;
    }

    private DishIngredient mapDishIngredient(ResultSet rs, int rowNum) throws SQLException {
        DishIngredient di = new DishIngredient();

        Dish dish = new Dish();
        dish.setId(rs.getInt("id_dish"));
        di.setDish(dish);

        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getInt("ing_id"));
        ingredient.setName(rs.getString("ing_name"));
        ingredient.setPrice(rs.getDouble("price"));
        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
        di.setIngredient(ingredient);

        di.setQuantity(rs.getDouble("quantity_required"));
        di.setUnit(Unit.valueOf(rs.getString("unit")));

        return di;
    }
}