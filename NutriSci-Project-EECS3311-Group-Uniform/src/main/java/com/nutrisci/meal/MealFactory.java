package com.nutrisci.meal;

import java.time.LocalDate;

// Factory for creating and duplicating Meal objects
public abstract class MealFactory {

    // Creates a meal of the given type for the given date
    // public static Meal createMeal(MealType type, LocalDate date) {
    //     Meal meal = null;
    //     switch (type) {
    //         case SNACK:
    //             meal = new Snack();
    //             break;
    //         case LUNCH:
    //             meal = new Lunch();
    //             break;
    //         case DINNER:
    //             meal = new Dinner();
    //             break;
    //         case BREAKFAST:
    //             meal = new Breakfast();
    //             break;
    //         default:
    //             throw new IllegalArgumentException("Invalid meal type: " + type);
    //     }

    //     if (meal.canAddToDate(date)) {
    //         return meal;
    //     }
    //     return null;
    // }

    public abstract Meal createMeal(LocalDate date);

    // public static Meal createMeal(MealType type, LocalDate date, User user) {
        
    // }

    
    // public static List<Meal> getMealsForDate(LocalDate date) {

    // }

    // Duplicates a meal for a new date
    public static Meal duplicateMeal(Meal originMeal, LocalDate newDate) throws CloneNotSupportedException {
        Meal newMeal = (Meal) originMeal.clone();
        newMeal.date = newDate;
        return newMeal;
    }
}
