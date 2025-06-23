package com.nutrisci.foodswap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.nutrisci.meallog.FoodItem;
import com.nutrisci.meallog.Meal;
import com.nutrisci.meallog.MealBuilder;

public class IncreaseCalorieStrategy implements FoodSwapStrategy {

    double calorieTargetIncrease;
    double maxProteinVariation = 0.05;
    double maxCarbsVariation = 0.10;

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

    // public boolean canSwap(Meal meal, Goal goal) {
    //     double currentCalories = meal.getTotalCalories();
    //     double targetCalories = goal.getCalories();
    //     return currentCalories < targetCalories;
    // }
    
}
