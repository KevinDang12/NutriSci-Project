package com.nutrisci.swap;

import java.time.LocalDateTime;
import java.util.List;
import com.nutrisci.meal.FoodItem;
import com.nutrisci.meal.Meal;

// Holds the result of a food swap operation
public class SwapResult {
    Meal originalMeal;
    Meal swappedMeal;
    List<FoodItem> individualSwaps;
    String swapSummary;
    double goalProgressImprovement;
    LocalDateTime swapTimestamp;
    boolean wasSuccessful;

    // Returns a summary of the nutrient improvement after swap
    public String getNutrientImprovement() {
        double caloriesImprovement = swappedMeal.getTotalCalories() - originalMeal.getTotalCalories();
        swapSummary = "Reduced calories by: " + caloriesImprovement + " calories";
        return swapSummary;
    }

    // Returns whether the swap was successful
    public boolean wasSuccessful() {
        return wasSuccessful;
    }

    // Returns the change in calories after swap
    public double getCalorieChange() {
        return swappedMeal.getTotalCalories() - originalMeal.getTotalCalories();
    }

    public double getProteinChange() {
        double result = 0;
        for (FoodItem item : swappedMeal.getFoodItems()) {
            result += item.getNutrientValue("PROTEIN");
        }

        for (FoodItem item : originalMeal.getFoodItems()) {
            result -= item.getNutrientValue("PROTEIN");
        }

        return result;
    }

    public double getGoalRelevantChange() {
        return 0.0;
    }

    // Returns the number of food swaps performed
    public int getSwapCount() {
        return individualSwaps.size();
    }

    // Returns the list of food items that were swapped
    public List<FoodItem> getFoodSwaps() {
        return individualSwaps;
    }

    public double calculateOverallBenefit() {
        return 0.0;
    }

    public String getRecommendedFollowUp() {
        return "";
    }

    // Exports the swap result as a string
    // helped by AI
    public String exportToString() {
        return "Swap Result: " + swapSummary + "\n" +
                "Calorie Change: " + getCalorieChange() + "\n" +
                "Swap Count: " + getSwapCount() + "\n" +
                "Food Swaps: " + getFoodSwaps() + "\n" +
                "Was Successful: " + wasSuccessful() + "\n" +
                "Swap Timestamp: " + swapTimestamp;
    }
 
}
