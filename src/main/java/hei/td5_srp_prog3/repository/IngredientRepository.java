package hei.td5_srp_prog3.repository;


import hei.td5_srp_prog3.configuration.DataSource;
import hei.td5_srp_prog3.entity.Ingredient;
import hei.td5_srp_prog3.entity.StockMovement;
import hei.td5_srp_prog3.entity.StockValue;
import hei.td5_srp_prog3.type.CategoryEnum;
import hei.td5_srp_prog3.type.MovementTypeEnum;
import hei.td5_srp_prog3.type.Unit;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class IngredientRepository {

    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // GET /ingredients
    public List<Ingredient> findAll() {
        String sql = "SELECT id, name, price, category FROM ingredient ORDER BY id";
        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            List<Ingredient> ingredients = new ArrayList<>();
            while (rs.next()) {
                ingredients.add(mapIngredient(rs));
            }
            return ingredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }

    // GET /ingredients/{id}
    public Optional<Ingredient> findById(Integer id) {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";
        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapIngredient(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }

    // GET /ingredients/{id}/stock
    public List<StockMovement> findStockMovementsByIngredientId(Integer ingredientId) {
        String sql = """
                SELECT id, quantity, unit, type, creation_datetime
                FROM stock_movement
                WHERE id_ingredient = ?
                ORDER BY creation_datetime
                """;
        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();
            List<StockMovement> movements = new ArrayList<>();
            while (rs.next()) {
                movements.add(mapStockMovement(rs));
            }
            return movements;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }

    private Ingredient mapIngredient(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getInt("id"));
        ingredient.setName(rs.getString("name"));
        ingredient.setPrice(rs.getDouble("price"));
        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
        return ingredient;
    }

    private StockMovement mapStockMovement(ResultSet rs) throws SQLException {
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