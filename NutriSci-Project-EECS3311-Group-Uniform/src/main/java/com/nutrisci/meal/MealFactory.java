package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.nutrisci.model.User;

// Factory for creating and duplicating Meal objects
public class MealFactory {
    private MealManager mealManager;

    private final Map<MealType, Meal> registry = Map.of(
        MealType.BREAKFAST, new Breakfast(),
        MealType.LUNCH, new Lunch(),
        MealType.DINNER, new Dinner(),
        MealType.SNACK, new Snack()
    );

    // Creates a meal of the given type for the given date
    public Meal createMeal(MealType type, LocalDate date, long id) {
        Meal meal = registry.get(type);
        mealManager = new MealManager();
        if (meal.canAddToDate(date)) {
            meal.setId(id);
            return meal;
        }
        return null;
    }

    public Meal createMeal(MealType type, LocalDate date, long id, User user) {
        // Check if valid with user
        Meal meal = registry.get(type);
        if (meal.canAddToDate(date)) {
            meal.setId(id);
            return meal;
        }
        return null;
    }

    
    public List<Meal> getMealsForDate(LocalDate date) {
        // Get User ID
        return mealManager.getMealsForDate(date);
    }

    public List<Meal> getMealsForDateRange(LocalDate startDate, LocalDate endDate) {
        // Get User ID
        return mealManager.getMealsForDateRange(startDate, endDate);
    }

    public boolean canAddMealType(MealType type, LocalDate date) {
        // Get User ID
        return mealManager.canAddMealType(type, date);
    }

    public int getMealCountForType(MealType type, LocalDate date) {
        // Get User ID
        return mealManager.getMealCountForType(type, date);
    }

    public List<MealType> getAvailableMealTypes(LocalDate date) {
        // Get User ID
        return mealManager.getAvailableMealTypes(date);
    }

    // Duplicates a meal for a new date
    public Meal duplicateMeal(Meal originMeal, LocalDate newDate) {
        Meal newMeal = originMeal.copyMeal();
        newMeal.date = newDate;
        return newMeal;
    }
}
