package com.nutrisci.swap;

import com.nutrisci.meal.Meal;

public interface FoodSwapStrategy {
    public SwapResult executeSwap(Meal meal);
}
