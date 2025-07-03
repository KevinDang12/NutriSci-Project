package com.nutrisci.meal;

import java.time.LocalDate;

public class BreakfastBuilder extends MealBuilder {
    private final BreakfastFactory factory = new BreakfastFactory();

    @Override
    public MealBuilder setMealType() {
        LocalDate date = LocalDate.now();
        mealBeingBuilt = factory.createMeal(date);
        mealTypeSet = true;
        return this;
    }
} 