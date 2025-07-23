package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a Breakfast meal
 */
public class Breakfast extends Meal {

    private MealManager mealManager;

    /**
     * Checks if breakfast can be added to the date
     * @param date the date to check
     * @return true if breakfast can be added to the date, false otherwise
     */
    @Override
    public boolean canAddToDate(LocalDate date) {
        mealManager = new MealManager();
        
        List<MealType> result = mealManager.getAvailableMealTypes(date);

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
