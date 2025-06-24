package com.nutrisci.swap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.nutrisci.meal.Meal;

public class FoodSwapContext {
    FoodSwapStrategy currentStrategy;
    // Map<GoalType, FoodSwapStrategy> strategyMap ;

    public SwapResult executeSwap(Meal meal) {
        return currentStrategy.executeSwap(meal);
    }

    // public SwapResult previewSwap(Meal meal) {
    //     return currentStrategy.executeSwap(meal);
    // }

    public List<SwapResult> executeMultipleSwaps(List<Meal> meals) {
        List<SwapResult> results = new ArrayList<>();
        for (Meal meal : meals) {
            results.add(executeSwap(meal));
        }
        return results;
    }
}
