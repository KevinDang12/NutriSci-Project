package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.List;

import com.nutrisci.database.DatabaseManager;

/**
 * Represents a Dinner meal
 */
public class Dinner extends Meal {

    private long userId = 0;

    /**
     * Checks if dinner can be added to the date
     * @param date the date to check
     * @return true if dinner can be added to the date, false otherwise
     */
    @Override
    public boolean canAddToDate(LocalDate date) {
        DatabaseManager db = DatabaseManager.getInstance();
        // Get user id
        List<MealType> result = db.getAvailableMealTypes(userId, date);

        for (MealType mealType : result) {
            if (mealType.name().equals("DINNER")) {
                return false;
            }
        }

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
