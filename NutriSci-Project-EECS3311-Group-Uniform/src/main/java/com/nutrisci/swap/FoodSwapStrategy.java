package com.nutrisci.swap;

import java.util.List;

import com.nutrisci.meal.FoodItem;
import com.nutrisci.meal.Meal;
import com.nutrisci.model.Goal;

// Interface for food swap strategies (Strategy pattern)
public interface FoodSwapStrategy {
    // Executes a swap on the given meal and returns the result
    // helped by AI
    public SwapResult executeSwap(Meal meal, Goal goal);
    public boolean canSwap(Meal meal, Goal goal);
    public List<FoodItem> findReplacementCandidates(FoodItem original, Goal goal);
    public double calculateSwapBenefit(FoodItem original, FoodItem replacement, Goal goal);
}
