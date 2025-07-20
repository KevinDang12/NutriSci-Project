package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.nutrisci.database.DatabaseManager;
import com.nutrisci.model.User;

// Factory for creating and duplicating Meal objects
public class MealFactory {

    private static final Map<MealType, Meal> registry = Map.of(
        MealType.BREAKFAST, new Breakfast(),
        MealType.LUNCH, new Lunch(),
        MealType.DINNER, new Dinner(),
        MealType.SNACK, new Snack()
    );

    static DatabaseManager db;

    // Creates a meal of the given type for the given date
    public static Meal createMeal(MealType type, LocalDate date, long id) {
        Meal meal = registry.get(type);
        if (meal.canAddToDate(date)) {
            meal.setId(id);
            return meal;
        }
        return null;
    }

    public static Meal createMeal(MealType type, LocalDate date, long id, User user) {
        // Check if valid with user
        Meal meal = registry.get(type);
        if (meal.canAddToDate(date)) {
            meal.setId(id);
            return meal;
        }
        return null;
    }

    
    public static List<Meal> getMealsForDate(LocalDate date) {
        db = DatabaseManager.getInstance();
        // Get User ID
        return db.getMealsForUser(null, date, date);
    }

    public static List<Meal> getMealsForDateRange(LocalDate startDate, LocalDate endDate) {
        db = DatabaseManager.getInstance();
        // Get User ID
        return db.getMealsForUser(null, startDate, endDate);
    }

    public static boolean canAddMealType(MealType type, LocalDate date) {
        db = DatabaseManager.getInstance();
        // Get User ID
        return db.canAddMealType(0, type, date);
    }

    public static int getMealCountForType(MealType type, LocalDate date) {
        db = DatabaseManager.getInstance();
        // Get User ID
        return db.getMealCountForType(0, type, date);
    }

    public static List<MealType> getAvailableMealTypes(LocalDate date) {
        db = DatabaseManager.getInstance();
        // Get User ID
        return db.getAvailableMealTypes(0, date);
    }

    // Duplicates a meal for a new date
    public static Meal duplicateMeal(Meal originMeal, LocalDate newDate) {
        Meal newMeal = originMeal.copyMeal();
        newMeal.date = newDate;
        return newMeal;
    }
}
