package com.nutrisci.meal;

import java.time.LocalDate;

// Represents a Snack meal (unlimited per day)
public class Snack extends Meal {

    // Checks if the snack can be added to the date (always true for snacks)
    @Override
    public boolean canAddToDate(LocalDate date) {
        return true;
    }

    // Returns the meal type as SNACK
    @Override
    public MealType getMealType() {
        return MealType.SNACK;
    }

    // Returns the maximum allowed snacks per day (-1 means unlimited)
    @Override
    public int getMaxAllowedPerDay() {
        return -1;
    }
}
