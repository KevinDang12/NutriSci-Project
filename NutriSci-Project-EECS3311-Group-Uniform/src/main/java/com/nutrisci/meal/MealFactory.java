package com.nutrisci.meal;

import java.time.LocalDate;

// Factory for creating and duplicating Meal objects
public class MealFactory {
    /**
     * Creates a meal of the given type for the given date
     * @param type The select meal type
     * @param date The entry date
     * @param id The meal ID
     * @return An instance of the meal object
     */
    public Meal createMeal(MealType type, LocalDate date, long id) {
        Meal meal = selectMealType(type);
        if (meal.canAddToDate(date)) {
            meal.setId(id);
            return meal;
        }
        return null;
    }

    /**
     * Get the instance of the selected meal type
     * @param type The type of meal to create
     * @return The instance of the meal
     */
    private Meal selectMealType(MealType type) {
        Meal meal = null;

        switch (type) {
            case BREAKFAST:
                meal = new Breakfast();
                break;
            case LUNCH:
                meal = new Lunch();
                break;
            case DINNER:
                meal = new Dinner();
                break;
            case SNACK:
                meal = new Snack();
                break;
            default:
                return null;
        }

        return meal;
    }

    // Duplicates a meal for a new date
    public Meal duplicateMeal(Meal originMeal, LocalDate newDate) {
        Meal newMeal = originMeal.copyMeal();
        newMeal.date = newDate;
        return newMeal;
    }
}
