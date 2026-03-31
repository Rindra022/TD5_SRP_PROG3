package hei.td5_srp_prog3.repository;


import hei.td5_srp_prog3.configuration.DataSource;
import hei.td5_srp_prog3.dto.StockMovementCreateRequest;
import hei.td5_srp_prog3.entity.Ingredient;
import hei.td5_srp_prog3.entity.StockMovement;
import hei.td5_srp_prog3.entity.StockValue;
import hei.td5_srp_prog3.exception.BadRequestException;
import hei.td5_srp_prog3.type.CategoryEnum;
import hei.td5_srp_prog3.type.MovementTypeEnum;
import hei.td5_srp_prog3.type.Unit;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
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

    // f) GET — filtre par plage de dates
    public List<StockMovement> findStockMovementsByIngredientIdAndDateRange(
            Integer ingredientId, Instant from, Instant to) {

        String sql = """
            SELECT id, quantity, unit, type, creation_datetime
            FROM stock_movement
            WHERE id_ingredient = ?
              AND creation_datetime >= ?
              AND creation_datetime <= ?
            ORDER BY creation_datetime
            """;

        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ps.setTimestamp(2, Timestamp.from(from));
            ps.setTimestamp(3, Timestamp.from(to));
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

    // g) POST — sauvegarde une liste de mouvements
    public List<StockMovement> saveStockMovements(
            Integer ingredientId, List<StockMovementCreateRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new BadRequestException("Le corps de la requête est obligatoire.");
        }

        String sql = """
            INSERT INTO stock_movement (id_ingredient, quantity, unit, type, creation_datetime)
            VALUES (?, ?, ?::unit_type, ?::mouvement_type, ?)
            RETURNING id, quantity, unit, type, creation_datetime
            """;

        List<StockMovement> saved = new ArrayList<>();
        Connection conn = dataSource.getConnection();

        try {
            conn.setAutoCommit(false);
            for (StockMovementCreateRequest req : requests) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, ingredientId);
                    ps.setDouble(2, req.getValue());
                    ps.setString(3, req.getUnit().toUpperCase());
                    ps.setString(4, req.getType().toUpperCase());
                    ps.setTimestamp(5, Timestamp.from(Instant.now()));

                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        saved.add(mapStockMovement(rs));
                    }
                }
            }
            conn.commit();
            return saved;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { throw new RuntimeException(ex); }
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }




}