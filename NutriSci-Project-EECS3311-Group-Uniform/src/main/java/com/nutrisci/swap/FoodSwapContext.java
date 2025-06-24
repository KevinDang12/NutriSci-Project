package com.nutrisci.swap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.nutrisci.meal.Meal;

// Context for executing food swap strategies
public class FoodSwapContext {
    FoodSwapStrategy currentStrategy;
    // Map<GoalType, FoodSwapStrategy> strategyMap ;

    // Executes the current swap strategy on a meal
    public SwapResult executeSwap(Meal meal) {
        return currentStrategy.executeSwap(meal);
    }

    // Executes the current swap strategy on multiple meals
    // helped by AI
    public List<SwapResult> executeMultipleSwaps(List<Meal> meals) {
        List<SwapResult> results = new ArrayList<>();
        for (Meal meal : meals) {
            results.add(executeSwap(meal));
        }
        return results;
    }
}
