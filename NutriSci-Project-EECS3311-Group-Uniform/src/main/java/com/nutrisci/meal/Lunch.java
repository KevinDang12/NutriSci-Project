package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a Lunch meal
 */
public class Lunch extends Meal {

    private MealManager mealManager;

    /**
     * Checks if lunch can be added to the date
     * @param date the date to check
     * @return true if lunch can be added to the date, false otherwise
     */
    @Override
    public boolean canAddToDate(LocalDate date) {
        mealManager = new MealManager();
        // Get user id
        List<MealType> result = mealManager.getAvailableMealTypes(date);

        for (MealType mealType : result) {
            if (mealType.name().equals("LUNCH")) {
                return false;
            }
        }

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
