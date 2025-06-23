package com.nutrisci.visualization;

import com.nutrisci.meallog.Meal;
import com.nutrisci.meallog.FoodItem;

import java.util.List;

public class NutritionalCalculator {
    public double[] calculateMacros(List<Meal> meals) {
        double protein = 0, carbs = 0, fat = 0;

        for (Meal meal : meals) {
            for (FoodItem item : meal.getFoodItems()) {
                protein += item.getNutrientValue("protein");
                carbs += item.getNutrientValue("carbs");
                fat += item.getNutrientValue("fat");
            }
        }

        return new double[] { protein, carbs, fat };
    }

    public double calculateTotalCalories(double protein, double carbs, double fat) {
        return protein * 4 + carbs * 4 + fat * 9;
    }
}