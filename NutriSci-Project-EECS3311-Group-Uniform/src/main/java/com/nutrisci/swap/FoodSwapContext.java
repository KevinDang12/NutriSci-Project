package com.nutrisci.swap;

import java.util.ArrayList;
import java.util.List;
import com.nutrisci.meal.Meal;
import com.nutrisci.model.GoalType;

// Context for executing food swap strategies
public class FoodSwapContext {
    FoodSwapStrategy currentStrategy;
    // Map<GoalType, FoodSwapStrategy> strategyMap ;

    // Executes the current swap strategy on a meal
    public SwapResult executeSwap(Meal meal) {
        // return currentStrategy.executeSwap(meal);
        return null;
    }

    public SwapResult executeSwap(Meal meal, GoalType specificGoal) {
        // return currentStrategy.executeSwap(meal);
        return null;
    }

    public void selectStrategy(GoalType goalType) {
        
    }

    public SwapResult previewSwap(Meal meal) {
        return null;
    }

    public boolean canPerformSwap(Meal meal) {
        return true;
    }

    // public List<SwapRecommendation> getSwapRecommendations(Meal meal) {

    // }

    // Executes the current swap strategy on multiple meals
    // helped by AI
    public List<SwapResult> executeMultipleSwaps(List<Meal> meals) {
        List<SwapResult> results = new ArrayList<>();
        for (Meal meal : meals) {
            results.add(executeSwap(meal));
        }
        return results;
    }

    public Meal undoLastSwap(Meal meal) {
        return null;
    }

    public List<SwapResult> getSwapHistory(int days) {
        return null;
    }

    public void registerStrategy(GoalType goalType, FoodSwapStrategy strategy) {

    }

    // public void setUserPreferences(SwapPreferences preferences) {

    // }
}
