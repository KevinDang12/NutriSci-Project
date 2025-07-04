package com.nutrisci.swap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;

import com.nutrisci.meal.FoodItem;
import com.nutrisci.meal.Meal;
import com.nutrisci.meal.MealBuilder;

// Strategy for increasing calories in a meal by swapping food items
public class IncreaseCalorieStrategy implements FoodSwapStrategy {

    double calorieTargetIncrease;
    double maxProteinVariation = 0.05;
    double maxCarbsVariation = 0.10;

    // Executes the calorie increase swap on a meal
    // helped by AI
    @Override
    public SwapResult executeSwap(Meal meal) {
        SwapResult result = new SwapResult();
        result.originalMeal = meal;

        MealBuilder builder = new MealBuilder();
        builder.setMealType(meal.getMealType());
        meal.getFoodItems().forEach(foodItem -> builder.addFoodItem(foodItem, 1));
        Meal swappedMeal = builder.buildPreview();
        result.swappedMeal = swappedMeal;

        result.individualSwaps = new ArrayList<>();

        // Swap out low-calorie items for higher-calorie replacements
        for (FoodItem foodItem : meal.getFoodItems()) {
            double currentCalories = foodItem.getNutrientValue("calories");
            if (currentCalories < 200) {
                result.individualSwaps.add(foodItem);
                swappedMeal.removeFoodItem(foodItem);
                swappedMeal.addFoodItem(getReplacementFoodItem(foodItem), 1);
            }
        }
        
        result.wasSuccessful = true;
        result.swapTimestamp = LocalDateTime.now();
        return result;
    }

    // Returns a replacement food item (mocked for now)
    public FoodItem getReplacementFoodItem(FoodItem foodItem) {
        // Should be a food item from the database
        Map<String, Double> macroNutrients = new HashMap<>();
        macroNutrients.put("calories", 200.0);
        macroNutrients.put("protein", 50.0);
        macroNutrients.put("carbs", 20.0);
        macroNutrients.put("fat", 10.0);
        macroNutrients.put("fiber", 5.0);
        Map<String, Double> vitamins = new HashMap<>();
        vitamins.put("vitaminA", 6.0);
        vitamins.put("vitaminC", 8.0);
        Map<String, Double> minerals = new HashMap<>();
        minerals.put("calcium", 10.0);
        minerals.put("iron", 20.0);
        minerals.put("magnesium", 30.0);
        minerals.put("phosphorus", 40.0);
        minerals.put("potassium", 50.0);

        FoodItem broccoli = new FoodItem("Broccoli", "A delicious vegetable", macroNutrients, vitamins, minerals, "Vegetable", 5, "g");

        return broccoli;
    }
}
