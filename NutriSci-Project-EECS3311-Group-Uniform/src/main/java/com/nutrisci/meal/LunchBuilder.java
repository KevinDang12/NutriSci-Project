package com.nutrisci.meal;

import java.time.LocalDate;

public class LunchBuilder extends MealBuilder {
    private final LunchFactory factory = new LunchFactory();

    @Override
    public MealBuilder setMealType() {
        LocalDate date = LocalDate.now();
        mealBeingBuilt = factory.createMeal(date);
        mealTypeSet = true;
        return this;
    }
} 