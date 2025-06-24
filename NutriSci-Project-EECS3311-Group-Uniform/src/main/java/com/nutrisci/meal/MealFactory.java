package com.nutrisci.meal;

import java.time.LocalDate;

// Factory for creating and duplicating Meal objects
public class MealFactory {

    // Creates a meal of the given type for the given date
    public static Meal createMeal(MealType type, LocalDate date) {
        Meal meal = new Snack(); // Only Snack implemented for now
        if (meal.canAddToDate(date)) {
            return meal;
        }
        return null;
    }

    // Duplicates a meal for a new date
    public static Meal duplicateMeal(Meal originMeal, LocalDate newDate) {
        Meal newMeal = new Snack();
        newMeal.date = newDate;
        newMeal.foodItems = originMeal.foodItems;
        return newMeal;
    }
}
