package com.nutrisci.foodswap;

import java.util.List;

import com.nutrisci.meallog.Meal;

public interface FoodSwapStrategy {
    public SwapResult executeSwap(Meal meal);

    // public boolean canSwap(FoodItem foodItem, Goal goal);

    // public List<FoodItem> findReplacementCandidates(FoodItem foodItem, Goal goal);

    // public double caclulateSwapBenefit(FoodItem foodItem, FoodItem replacement, Goal goal);
}
