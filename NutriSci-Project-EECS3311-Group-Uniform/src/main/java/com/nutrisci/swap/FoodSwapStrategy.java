package com.nutrisci.swap;

import com.nutrisci.meal.Meal;

// Interface for food swap strategies (Strategy pattern)
public interface FoodSwapStrategy {
    // Executes a swap on the given meal and returns the result
    // helped by AI
    public SwapResult executeSwap(Meal meal);
}
