package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.List;

import com.nutrisci.database.DatabaseManager;

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
        DatabaseManager db = DatabaseManager.getInstance();
        // Get user id
        List<MealType> result = db.getAvailableMealTypes(id, date);

        for (MealType mealType : result) {
            if (mealType.name().equals("BREAKFAST")) {
                return false;
            }
        }

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
