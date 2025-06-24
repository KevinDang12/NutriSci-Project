package com.nutrisci.visualization;

import com.nutrisci.meal.Meal;
import com.nutrisci.meal.FoodItem;

import java.util.List;

public class ChartNutritionalCalculator {
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