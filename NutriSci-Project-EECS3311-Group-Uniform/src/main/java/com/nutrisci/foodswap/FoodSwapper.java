package com.nutrisci.foodswap;

import java.util.HashMap;
import java.util.Map;

import com.nutrisci.meallog.FoodItem;
import com.nutrisci.meallog.Meal;
import com.nutrisci.meallog.MealBuilder;
import com.nutrisci.meallog.MealType;

public class FoodSwapper {
    public static void main(String[] args) {
        FoodSwapContext context = new FoodSwapContext();
        context.currentStrategy = new IncreaseCalorieStrategy();

        MealBuilder builder = new MealBuilder();
        builder.setMealType(MealType.SNACK);
        Map<String, Double> macroNutrients = new HashMap<>();

        macroNutrients.put("calories", 100.0);
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

        FoodItem apple = new FoodItem("Apple", "A delicious fruit", macroNutrients, vitamins, minerals, "Fruit", 2, "g");

        builder.addFoodItem(apple, 1);
        Meal meal = builder.buildPreview();
        SwapResult result = context.executeSwap(meal);
        System.out.println("Original meal: ");
        for (FoodItem foodItem : meal.getFoodItems()) {
            System.out.println(foodItem.getDisplayName());
            System.out.println("Calories: " + foodItem.getNutrientValue("calories"));
        }

        System.out.println("================================================");

        System.out.println("Swapped meal: ");
        for (FoodItem foodItem : result.swappedMeal.getFoodItems()) {
            System.out.println(foodItem.getDisplayName());
            System.out.println("Calories: " + foodItem.getNutrientValue("calories"));
        }
    }
}
