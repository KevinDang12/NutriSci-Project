package com.nutrisci.meal;

import java.time.LocalDate;

/**
 * Represents a Lunch meal
 */
public class Lunch extends Meal {

    /**
     * Checks if lunch can be added to the date
     * @param date the date to check
     * @return true if lunch can be added to the date, false otherwise
     */
    @Override
    public boolean canAddToDate(LocalDate date) {
        // TODO: Implement this method
        // Call the DB to check if lunch is allowed on this date for the user
        // Return true if allowed, false otherwise
        return true;
    }

    /**
     * Returns the meal type as LUNCH
     * @return MealType.LUNCH, the meal type as LUNCH
     */
    @Override
    public MealType getMealType() {
        return MealType.LUNCH;
    }

    /**
     * Returns the maximum allowed lunches per day
     * @return 1, the maximum allowed lunches per day
     */
    @Override
    public int getMaxAllowedPerDay() {
        return 1;
    }

    /**
     * Returns the recommended calories percentage for lunch
     * @return 0.35, the recommended calories percentage for lunch
     */
    public double getRecommendedCaloriesPercentage() {
        return 0.35;
    }
}
