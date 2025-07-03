package com.nutrisci.meal;

import java.time.LocalDate;

public class DinnerBuilder extends MealBuilder {
    private final DinnerFactory factory = new DinnerFactory();

    @Override
    public MealBuilder setMealType() {
        LocalDate date = LocalDate.now();
        mealBeingBuilt = factory.createMeal(date);
        mealTypeSet = true;
        return this;
    }
} 