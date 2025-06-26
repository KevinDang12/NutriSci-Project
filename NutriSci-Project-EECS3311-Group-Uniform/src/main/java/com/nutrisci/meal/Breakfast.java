package com.nutrisci.meal;

import java.time.LocalDate;

/**
 * Represents a Breakfast meal
 */
public class Breakfast extends Meal {

    /**
     * Checks if breakfast can be added to the date
     * @param date the date to check
     * @return true if breakfast can be added to the date, false otherwise
     */
    @Override
    public boolean canAddToDate(LocalDate date) {
        // TODO: Implement this method
        // Call the DB to check if breakfast is allowed on this date for the user
        // Return true if allowed, false otherwise
        return true;
    }

    /**
     * Returns the meal type as BREAKFAST
     * @return MealType.BREAKFAST, the meal type as BREAKFAST
     */
    @Override
    public MealType getMealType() {
        return MealType.BREAKFAST;
    }

    /**
     * Returns the maximum allowed breakfasts per day
     * @return 1, the maximum allowed breakfasts per day
     */
    @Override
    public int getMaxAllowedPerDay() {
        return 1;
    }

    /* There is no time range class in Java */
    // public TimeRange getTypicalTimeRange() {
        
    // }

    /**
     * Returns the recommended calories percentage for breakfast
     * @return 0.25, the recommended calories percentage for breakfast
     */
    public double getRecommendedCaloriesPercentage() {
        return 0.25;
    }
}
