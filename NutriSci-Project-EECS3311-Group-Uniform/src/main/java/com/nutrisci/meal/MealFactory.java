package com.nutrisci.meal;

import java.time.LocalDate;

public class MealFactory {

    /**
     * Creates a meal of the given type for the given date
     * @param type The type of meal to create
     * @param date The date for the meal
     * @return The created meal
     */
    public static Meal createMeal(MealType type, LocalDate date) {
        Meal meal = new Snack();
        if (meal.canAddToDate(date)) {
            return meal;
        }
        return null;
    }

    /**
     * Duplicates a meal for a new date
     * @param originMeal The meal to duplicate
     * @param newDate The new date for the duplicated meal
     * @return The duplicated meal
     */
    public static Meal duplicateMeal(Meal originMeal, LocalDate newDate) {
        Meal newMeal = new Snack();
        newMeal.date = newDate;
        newMeal.foodItems = originMeal.foodItems;
        return newMeal;
    }
}
