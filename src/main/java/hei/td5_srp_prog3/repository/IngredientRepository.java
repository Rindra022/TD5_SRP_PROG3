package hei.td5_srp_prog3.repository;

import hei.td5_srp_prog3.entity.Ingredient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class IngredientRepository {

    private final JdbcTemplate jdbc;

    public IngredientRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Ingredient> findAll() {
        String sql = "SELECT id, name, price, category FROM ingredient ORDER BY id";
        return jdbc.query(sql, this::mapIngredient);
    }

    public Optional<Ingredient> findById(Integer id) {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";
        List<Ingredient> results = jdbc.query(sql, this::mapIngredient, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<StockMovement> findStockMovementsByIngredientId(Integer ingredientId) {
        String sql = """
                SELECT id, quantity, unit, type, creation_datetime
                FROM stock_movement
                WHERE id_ingredient = ?
                ORDER BY creation_datetime
                """;
        return jdbc.query(sql, this::mapStockMovement, ingredientId);
    }

    private Ingredient mapIngredient(ResultSet rs, int rowNum) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getInt("id"));
        ingredient.setName(rs.getString("name"));
        ingredient.setPrice(rs.getDouble("price"));
        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
        return ingredient;
    }

    private StockMovement mapStockMovement(ResultSet rs, int rowNum) throws SQLException {
        StockMovement sm = new StockMovement();
        sm.setId(rs.getInt("id"));
        sm.setType(MovementTypeEnum.valueOf(rs.getString("type")));
        sm.setCreationDateTime(rs.getTimestamp("creation_datetime").toInstant());

        StockValue sv = new StockValue();
        sv.setQuantity(rs.getDouble("quantity"));
        sv.setUnit(Unit.valueOf(rs.getString("unit")));
        sm.setValue(sv);

        return sm;
    }
}