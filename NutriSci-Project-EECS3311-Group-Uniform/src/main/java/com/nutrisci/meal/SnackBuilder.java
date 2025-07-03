package com.nutrisci.meal;

import java.time.LocalDate;

public class SnackBuilder extends MealBuilder {
    private final SnackFactory factory = new SnackFactory();

    @Override
    public MealBuilder setMealType() {
        LocalDate date = LocalDate.now();
        mealBeingBuilt = factory.createMeal(date);
        mealTypeSet = true;
        return this;
    }
} 