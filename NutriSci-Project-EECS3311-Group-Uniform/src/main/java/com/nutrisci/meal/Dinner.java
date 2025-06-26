package com.nutrisci.meal;

import java.time.LocalDate;

/**
 * Represents a Dinner meal
 */
public class Dinner extends Meal {

    /**
     * Checks if dinner can be added to the date
     * @param date the date to check
     * @return true if dinner can be added to the date, false otherwise
     */
    @Override
    public boolean canAddToDate(LocalDate date) {
        // TODO: Implement this method
        // Call the DB to check if dinner is allowed on this date for the user
        // Return true if allowed, false otherwise
        return true;
    }

    /**
     * Returns the meal type as DINNER
     * @return MealType.DINNER, the meal type as DINNER
     */
    @Override
    public MealType getMealType() {
        return MealType.DINNER;
    }

    /**
     * Returns the maximum allowed dinners per day
     * @return 1, the maximum allowed dinners per day
     */
    @Override
    public int getMaxAllowedPerDay() {
        return 1;
    }

    /**
     * Returns the recommended calories percentage for dinner
     * @return 0.3, the recommended calories percentage for dinner
     */
    public double getRecommendedCaloriesPercentage() {
        return 0.3;
    }
    
}
