package hei.td5_srp_prog3.repository;


import hei.td5_srp_prog3.configuration.DataSource;
import hei.td5_srp_prog3.entity.Dish;
import hei.td5_srp_prog3.entity.DishIngredient;
import hei.td5_srp_prog3.entity.Ingredient;
import hei.td5_srp_prog3.type.CategoryEnum;
import hei.td5_srp_prog3.type.DishTypeEnum;
import hei.td5_srp_prog3.type.Unit;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DishRepository {

    private final DataSource dataSource;

    public DishRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // GET /dishes
    public List<Dish> findAll() {
        String sql = "SELECT id, name, dish_type, selling_price FROM dish ORDER BY id";
        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            List<Dish> dishes = new ArrayList<>();
            while (rs.next()) {
                Dish dish = mapDish(rs);
                dish.setDishIngredients(findDishIngredientsByDishId(dish.getId()));
                dishes.add(dish);
            }
            return dishes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }

    // findById (utilisé en interne)
    public Optional<Dish> findById(Integer id) {
        String sql = "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?";
        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Dish dish = mapDish(rs);
                dish.setDishIngredients(findDishIngredientsByDishId(id));
                return Optional.of(dish);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }

    // PUT /dishes/{id}/ingredients — supprime les associations
    public void detachAllIngredients(Integer dishId) {
        String sql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }

    // PUT /dishes/{id}/ingredients — recrée les associations
    public void attachIngredients(Integer dishId, List<DishIngredient> dishIngredients) {
        String sql = """
                INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, ?, ?::unit_type)
                """;
        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient di : dishIngredients) {
                ps.setInt(1, dishId);
                ps.setInt(2, di.getIngredient().getId());
                ps.setDouble(3, di.getQuantity());
                ps.setString(4, di.getUnit().name());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }

    private List<DishIngredient> findDishIngredientsByDishId(Integer dishId) {
        String sql = """
                SELECT di.quantity_required, di.unit,
                       i.id AS ing_id, i.name AS ing_name, i.price, i.category
                FROM dish_ingredient di
                JOIN ingredient i ON i.id = di.id_ingredient
                WHERE di.id_dish = ?
                """;
        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();
            List<DishIngredient> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapDishIngredient(rs, dishId));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }

    private Dish mapDish(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getInt("id"));
        dish.setName(rs.getString("name"));
        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
        double sellingPrice = rs.getDouble("selling_price");
        dish.setSellingPrice(rs.wasNull() ? null : sellingPrice);
        return dish;
    }

    private DishIngredient mapDishIngredient(ResultSet rs, Integer dishId) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getInt("ing_id"));
        ingredient.setName(rs.getString("ing_name"));
        ingredient.setPrice(rs.getDouble("price"));
        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

        Dish dish = new Dish();
        dish.setId(dishId);

        DishIngredient di = new DishIngredient();
        di.setDish(dish);
        di.setIngredient(ingredient);
        di.setQuantity(rs.getDouble("quantity_required"));
        di.setUnit(Unit.valueOf(rs.getString("unit")));
        return di;
    }
}