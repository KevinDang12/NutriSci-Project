package com.nutrisci.meal;

import java.time.LocalDate;

import com.nutrisci.calculator.NutritionalData;

public interface MealObserver {
    void onMealChanged(MealEvent event, Meal meal);
    void onDailyNutritionChanged(LocalDate date, NutritionalData newTotal);
}
