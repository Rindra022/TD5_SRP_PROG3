package hei.td5_srp_prog3.service;

import hei.td5_srp_prog3.dto.IngredientDTO;
import hei.td5_srp_prog3.dto.StockValueDTO;
import hei.td5_srp_prog3.entity.Ingredient;
import hei.td5_srp_prog3.entity.StockMovement;
import hei.td5_srp_prog3.entity.StockValue;
import hei.td5_srp_prog3.exception.BadRequestException;
import hei.td5_srp_prog3.exception.ResourceNotFoundException;
import hei.td5_srp_prog3.repository.IngredientRepository;
import hei.td5_srp_prog3.type.Unit;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<IngredientDTO> getAll() {
        return ingredientRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public IngredientDTO getById(Integer id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ingredient.id=" + id + " is not found"));
        return toDTO(ingredient);
    }

    public StockValueDTO getStockAt(Integer id, String atParam, String unitParam) {
        if (atParam == null || unitParam == null) {
            throw new BadRequestException(
                    "Either mandatory query parameter `at` or `unit` is not provided.");
        }

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ingredient.id=" + id + " is not found"));

        Unit unit;
        try {
            unit = Unit.valueOf(unitParam.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unit invalide : " + unitParam + ". Valeurs acceptées : KG, PCS, L");
        }

        Instant at;
        try {
            at = LocalDateTime.parse(atParam, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Format de date invalide. Utilisez : yyyy-MM-ddTHH:mm:ss (ex: 2024-01-06T12:00:00)");
        }

        List<StockMovement> movements = ingredientRepository.findStockMovementsByIngredientId(id);
        ingredient.setStockMovementList(movements);

        StockValue stockValue = ingredient.getStockValueAt(at, unit);
        return new StockValueDTO(stockValue.getUnit().name(), stockValue.getQuantity());
    }

    private IngredientDTO toDTO(Ingredient i) {
        return new IngredientDTO(i.getId(), i.getName(), i.getCategory().name(), i.getPrice());
    }
}