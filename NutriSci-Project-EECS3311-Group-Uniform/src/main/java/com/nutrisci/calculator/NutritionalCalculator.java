package com.nutrisci.calculator;

import java.util.List;
import com.nutrisci.meal.FoodItem;

// Performs nutritional calculations for meals
public class NutritionalCalculator {
    // Calculates the total nutrition for a list of food items
    public NutritionalData calculateMealNutrition(List<FoodItem> foodItems){
        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        double totalFiber = 0;

        for (FoodItem item : foodItems) {
            totalCalories += item.calculateCaloriesFromMacros();
            totalProtein += item.getNutrientValue("PROTEIN");
            totalCarbs += item.getNutrientValue("CARBOHYDRATE, TOTAL (BY DIFFERENCE)");
            totalFat += item.getNutrientValue("FAT (TOTAL LIPIDS)");
            totalFiber += item.getNutrientValue("FIBRE, TOTAL DIETARY");
        }
    
        NutritionalData result = new NutritionalData();
        result.setCalories(totalCalories);
        result.setProtein(totalProtein);
        result.setCarbs(totalCarbs);
        result.setFat(totalFat);
        result.setFiber(totalFiber);
        return result;
    }

    // Validates that nutritional data is within reasonable bounds
    public boolean validateNutritionalData(NutritionalData data){
        if (data == null) {
            return false;
        }

        // check for negative values
        if (data.getCalories() < 0 || 
        data.getProtein() < 0 || 
        data.getCarbs() < 0 || 
        data.getFat() < 0) {
            return false;
        }

        // check for the range of meal
        if (data.getCalories() > 2000 || 
        data.getProtein() > 100 || 
        data.getCarbs() > 300 || 
        data.getFat() > 100) {
            return false;
        }

        return true;
    }
}