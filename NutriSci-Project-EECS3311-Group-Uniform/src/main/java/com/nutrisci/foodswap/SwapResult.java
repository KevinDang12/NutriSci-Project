package com.nutrisci.foodswap;

import java.time.LocalDateTime;
import java.util.List;

import com.nutrisci.meallog.FoodItem;
import com.nutrisci.meallog.Meal;

public class SwapResult {
    Meal originalMeal;
    Meal swappedMeal;
    List<FoodItem> individualSwaps;
    String swapSummary;
    double goalProgressImprovement;
    LocalDateTime swapTimestamp;
    boolean wasSuccessful;

    public String getNutrientImprovement() {
        double caloriesImprovement = swappedMeal.getTotalCalories() - originalMeal.getTotalCalories();
        swapSummary = "Reduced calories by: " + caloriesImprovement + " calories";
        return swapSummary;
    }

    public boolean wasSuccessful() {
        return wasSuccessful;
    }

    public double getCalorieChange() {
        return swappedMeal.getTotalCalories() - originalMeal.getTotalCalories();
    }

    public int getSwapCount() {
        return individualSwaps.size();
    }

    public List<FoodItem> getFoodSwaps() {
        return individualSwaps;
    }

    public String exportToString() {
        return "Swap Result: " + swapSummary + "\n" +
                "Calorie Change: " + getCalorieChange() + "\n" +
                "Swap Count: " + getSwapCount() + "\n" +
                "Food Swaps: " + getFoodSwaps() + "\n" +
                "Was Successful: " + wasSuccessful() + "\n" +
                "Swap Timestamp: " + swapTimestamp;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
