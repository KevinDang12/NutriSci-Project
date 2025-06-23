package com.nutrisci.meallog;

import java.time.LocalDate;

public class Snack extends Meal {

    /**
     * Checks if the snack can be added to the date
     * @param date The date to check
     * @return True typically since Snacks have no daily limit
     */
    @Override
    public boolean canAddToDate(LocalDate date) {
        return true;
    }

    /**
     * Returns the meal type as SNACK
     * @return The meal type as SNACK
     */
    @Override
    public MealType getMealType() {
        return MealType.SNACK;
    }

    /**
     * Returns the maximum allowed snacks per day
     * @return (-1) no limit enforced
     */
    @Override
    public int getMaxAllowedPerDay() {
        return -1;
    }
}
