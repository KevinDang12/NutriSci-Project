package com.nutrisci.meal;

import java.time.LocalDate;

/**
 * Represents a Snack meal (unlimited per day)
 */
public class Snack extends Meal {

    /**
     * Checks if the snack can be added to the date (always true for snacks)
     * @param date the date to check
     * @return true if snack can be added to the date, false otherwise
     */
    @Override
    public boolean canAddToDate(LocalDate date) {
        return true;
    }

    /**
     * Returns the meal type as SNACK
     * @return MealType.SNACK, the meal type as SNACK
     */
    @Override
    public MealType getMealType() {
        return MealType.SNACK;
    }

    /**
     * Returns the maximum allowed snacks per day (-1 means unlimited)
     * @return -1, the maximum allowed snacks per day
     */
    @Override
    public int getMaxAllowedPerDay() {
        return -1;
    }

    /**
     * Returns the recommended calories percentage for snack
     * @return 0.1, the recommended calories percentage for snack
     */
    public double getRecommendedCaloriesPercentage() {
        return 0.1;
    }
}
