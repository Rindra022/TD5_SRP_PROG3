package hei.td5_srp_prog3.service;

import hei.td5_srp_prog3.dto.DishDTO;
import hei.td5_srp_prog3.dto.DishIngredientRequest;
import hei.td5_srp_prog3.dto.IngredientDTO;
import hei.td5_srp_prog3.entity.Dish;
import hei.td5_srp_prog3.entity.DishIngredient;
import hei.td5_srp_prog3.entity.Ingredient;
import hei.td5_srp_prog3.exception.BadRequestException;
import hei.td5_srp_prog3.exception.ResourceNotFoundException;
import hei.td5_srp_prog3.repository.DishRepository;
import hei.td5_srp_prog3.repository.IngredientRepository;
import hei.td5_srp_prog3.type.Unit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public DishService(DishRepository dishRepository, IngredientRepository ingredientRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<DishDTO> getAll() {
        return dishRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public DishDTO updateIngredients(Integer dishId, List<DishIngredientRequest> requestBody) {
        if (requestBody == null || requestBody.isEmpty()) {
            throw new BadRequestException("Le corps de la requête est obligatoire et ne peut pas être vide.");
        }

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dish.id=" + dishId + " is not found"));

        List<DishIngredient> validDishIngredients = new ArrayList<>();

        for (DishIngredientRequest req : requestBody) {
            if (req.getId() == null) continue;

            Optional<Ingredient> dbIngredient = ingredientRepository.findById(req.getId());
            if (dbIngredient.isEmpty()) continue; // ingrédient inexistant → ignoré

            Ingredient fromDB = dbIngredient.get();

            boolean nameMatch = fromDB.getName().equals(req.getName());
            boolean priceMatch = req.getPrice() != null && fromDB.getPrice().equals(req.getPrice());
            boolean categoryMatch = req.getCategory() != null
                    && fromDB.getCategory().name().equals(req.getCategory());

            if (!nameMatch || !priceMatch || !categoryMatch) continue;

            Unit unit;
            try {
                unit = Unit.valueOf(req.getUnit().toUpperCase());
            } catch (Exception e) {
                continue;
            }

            DishIngredient di = new DishIngredient();
            di.setDish(dish);
            di.setIngredient(fromDB);
            di.setQuantity(req.getQuantityRequired() != null ? req.getQuantityRequired() : 0.0);
            di.setUnit(unit);

            validDishIngredients.add(di);
        }

        dishRepository.detachAllIngredients(dishId);

        if (!validDishIngredients.isEmpty()) {
            dishRepository.attachIngredients(dishId, validDishIngredients);
        }

        Dish updated = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish.id=" + dishId + " is not found"));
        return toDTO(updated);
    }

    private DishDTO toDTO(Dish dish) {
        List<IngredientDTO> ingredientDTOs = dish.getDishIngredients() == null
                ? List.of()
                : dish.getDishIngredients().stream()
                .map(di -> new IngredientDTO(
                        di.getIngredient().getId(),
                        di.getIngredient().getName(),
                        di.getIngredient().getCategory().name(),
                        di.getIngredient().getPrice()))
                .toList();

        return new DishDTO(dish.getId(), dish.getName(), dish.getSellingPrice(), ingredientDTOs);
    }
}