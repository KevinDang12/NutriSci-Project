package com.nutrisci.swap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nutrisci.meal.FoodItem;
import com.nutrisci.meal.Meal;
import com.nutrisci.meal.MealBuilder;
import com.nutrisci.model.Goal;

public class IncreaseProteinStrategy implements FoodSwapStrategy {
    
    double calorieTargetIncrease;
    double maxProteinVariation = 0.1;
    double maxCarbsVariation = 0.05;

    // Executes the calorie increase swap on a meal
    // helped by AI
    @Override
    public SwapResult executeSwap(Meal meal, Goal goal) {
        SwapResult result = new SwapResult();
        result.originalMeal = meal;

        MealBuilder builder = new MealBuilder();
        builder.setMealType(meal.getMealType());
        meal.getFoodItems().forEach(foodItem -> builder.addFoodItem(foodItem));
        Meal swappedMeal = builder.buildPreview();
        result.swappedMeal = swappedMeal;

        result.individualSwaps = new ArrayList<>();

        // Swap out low-calorie items for higher-calorie replacements
        for (FoodItem foodItem : meal.getFoodItems()) {
            double currentCalories = foodItem.getNutrientValue("calories");
            if (currentCalories < 200) {
                result.individualSwaps.add(foodItem);
                swappedMeal.removeFoodItem(foodItem);
                swappedMeal.addFoodItem(findHighCalorieAlternatives(foodItem));
            }
        }
        
        result.wasSuccessful = true;
        result.swapTimestamp = LocalDateTime.now();
        return result;
    }

    @Override
    public boolean canSwap(Meal meal, Goal goal) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'canSwap'");
    }

    @Override
    public List<FoodItem> findReplacementCandidates(FoodItem original, Goal goal) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findReplacementCandidates'");
    }

    @Override
    public double calculateSwapBenefit(FoodItem original, FoodItem replacement, Goal goal) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculateSwapBenefit'");
    }

    // Returns a replacement food item (mocked for now)
    public FoodItem findHighCalorieAlternatives(FoodItem original) {
        // Should be a food item from the database
        // Make a backend call to the database for foodswap
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

        FoodItem broccoli = new FoodItem("Broccoli", macroNutrients, "Vegetable");

        return broccoli;
    }

    // public CalorieAnalysis analyzeCalorieDensity(Meal meal) {

    // }

    public double calculateHealthyCalorieIncrease(FoodItem original, FoodItem replacement) {
        return 0.0;
    }
}
