package com.nutrisci.foodswap;

import com.nutrisci.meallog.Meal;

public interface FoodSwapStrategy {
    public SwapResult executeSwap(Meal meal);
}
