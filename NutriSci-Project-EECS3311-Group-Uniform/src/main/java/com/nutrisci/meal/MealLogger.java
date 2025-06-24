package com.nutrisci.meal;

import java.util.HashMap;
import java.util.Map;

// This class demonstrates how to build and log a meal using MealBuilder and FoodItem
public class MealLogger {
    public static void main(String[] args) {
        //meal building and logging
        MealBuilder builder = new MealBuilder();
        builder.setMealType(MealType.SNACK);
        Map<String, Double> macroNutrients = new HashMap<>();

        // Set up macronutrients
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

        // Create a food item and add it to the meal
        FoodItem apple = new FoodItem("Apple", "A delicious fruit", macroNutrients, vitamins, minerals, "Fruit", 2, "g");
        builder.addFoodItem(apple, 1);
        Meal meal = builder.buildPreview();

        // Print meal details
        System.out.println("Meal type: " + meal.getMealType());
        System.out.println("Total calories: " + meal.getTotalCalories());
        System.out.println("Food item count: " + meal.getFoodItemCount());
        System.out.println("Food items:");
        for (FoodItem foodItem : meal.foodItems) {
            System.out.println(foodItem.getDisplayName());
            System.out.println("- Calories: " + foodItem.getNutrientValue("calories"));
            System.out.println("- Protein: " + foodItem.getNutrientValue("protein"));
            System.out.println("- Carbs: " + foodItem.getNutrientValue("carbs"));
            System.out.println("- Fat: " + foodItem.getNutrientValue("fat"));
            System.out.println("- Fiber: " + foodItem.getNutrientValue("fiber"));
            System.out.println("- Vitamin A: " + foodItem.getNutrientValue("vitaminA"));
            System.out.println("- Vitamin C: " + foodItem.getNutrientValue("vitaminC"));
            System.out.println("- Calcium: " + foodItem.getNutrientValue("calcium"));
            System.out.println("- Iron: " + foodItem.getNutrientValue("iron"));
            System.out.println("- Magnesium: " + foodItem.getNutrientValue("magnesium"));
            System.out.println("- Phosphorus: " + foodItem.getNutrientValue("phosphorus"));
            System.out.println("- Potassium: " + foodItem.getNutrientValue("potassium"));
        }
    }
}
