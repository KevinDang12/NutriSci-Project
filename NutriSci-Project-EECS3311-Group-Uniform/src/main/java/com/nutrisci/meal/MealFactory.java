package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.List;

// Factory for creating and duplicating Meal objects
public class MealFactory {

    // Creates a meal of the given type for the given date
    public static Meal createMeal(MealType type, LocalDate date) {
        Meal meal = null;
        switch (type) {
            case SNACK:
                meal = new Snack();
                break;
            case LUNCH:
                meal = new Lunch();
                break;
            case DINNER:
                meal = new Dinner();
                break;
            case BREAKFAST:
                meal = new Breakfast();
                break;
            default:
                throw new IllegalArgumentException("Invalid meal type: " + type);
        }

        if (meal.canAddToDate(date)) {
            return meal;
        }
        return null;
    }

    // Creates a meal of the given type for the given date and user
    // public static Meal createMeal(MealType type, LocalDate date, User user) {
        
    // }

    
    // Gets all meals for the given date, a backend call
    // public static List<Meal> getMealsForDate(LocalDate date) {

    // }

    // Gets all meals for the given date range, a backend call
    // public static List<Meal> getMealsForDateRange(LocalDate startDate, LocalDate endDate) {
    //     return null;
    // }

    public static boolean canAddMeal(MealType type, LocalDate date) {
        if (type == MealType.SNACK) {
            return true;
        }

        // Make call to backend to check if meal can be added
        return false;
    }

    // Duplicates a meal for a new date
    public static Meal duplicateMeal(Meal originMeal, LocalDate newDate) throws CloneNotSupportedException {
        Meal newMeal = (Meal) originMeal.clone();
        newMeal.date = newDate;
        return newMeal;
    }

    public static List<MealType> getAvailableMealTypes(LocalDate date) {
        // Make call to backend to get available meal types
        return null;
    }
}
