package com.nutriSci.calculator;

import java.util.List;
//import com.nutriSci.calculator.FoodItem;

public class NutritionalCalculator {
    
    public NutritionalData calculateMealNutrition(List<FoodItem> foodItems){
        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        double totalFiber = 0;

        for (FoodItem item : foodItems) {
            totalCalories += item.getNutrientValue("calories");
            totalProtein += item.getNutrientValue("protein");
            totalCarbs += item.getNutrientValue("carbs");
            totalFat += item.getNutrientValue("fat");
            totalFiber += item.getNutrientValue("fiber");
        }
    
    NutritionalData result = new NutritionalData(totalCalories, totalProtein, totalCarbs, totalFat, totalFiber);
    return result;
    
    }
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