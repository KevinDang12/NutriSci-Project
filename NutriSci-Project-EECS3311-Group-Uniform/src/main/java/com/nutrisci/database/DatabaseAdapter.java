package com.nutrisci.database;

import com.nutrisci.meal.Meal;
import com.nutrisci.meal.MealType;
import com.nutrisci.meal.FoodItem;
import com.nutrisci.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Database adapter interface for different database types.
 * This allows the application to work with different database systems
 * without changing the main business logic.
 * 
 * @author NutriSci Team
 * @version 1.0
 */
public interface DatabaseAdapter {
    
    /**
     * Save a meal to the database
     * @param meal The meal to save
     * @param userId The user ID
     * @return true if successful, false otherwise
     */
    boolean saveMeal(Meal meal, long userId);
    
    /**
     * Update an existing meal
     * @param meal The meal to update
     * @return true if successful, false otherwise
     */
    boolean updateMeal(Meal meal);
    
    /**
     * Delete a meal from the database
     * @param mealId The meal ID to delete
     * @return true if successful, false otherwise
     */
    boolean deleteMeal(Long mealId);
    
    /**
     * Get meals for a user within a date range
     * @param userId The user ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of meals
     */
    List<Meal> getMealsForUser(long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Save a user to the database
     * @param user The user to save
     * @return true if successful, false otherwise
     */
    boolean saveUser(User user);
    
    /**
     * Update an existing user
     * @param user The user to update
     * @return true if successful, false otherwise
     */
    boolean updateUserProfile(User user);
    
    /**
     * Authenticate a user
     * @param email User email
     * @param password User password
     * @return User object if authenticated, null otherwise
     */
    User authenticateUser(String email, String password);
    
    /**
     * Check if a user exists
     * @param email The email to check
     * @return true if user exists, false otherwise
     */
    boolean checkIfUserExists(String email);
    
    /**
     * Load a food item by ID
     * @param foodId The food ID
     * @return FoodItem object
     */
    FoodItem loadFoodItem(Long foodId);
    
    /**
     * Get all food items
     * @return Map of food IDs and descriptions
     */
    Map<Long, String> getFoodItems();
    
    /**
     * Import meals for a user
     * @param userId The user ID
     * @return Map of meal IDs and descriptions
     */
    Map<Long, String> importMeals(long userId);
    
    /**
     * Import food items for a meal
     * @param mealId The meal ID
     * @return List of food IDs
     */
    List<Long> importMeal(long mealId);
    
    /**
     * Swap food in a meal
     * @param mealId The meal ID
     * @param original The original food item
     * @param replacement The replacement food item
     * @return true if successful, false otherwise
     */
    boolean swapFoodInMeal(Long mealId, FoodItem original, FoodItem replacement);
    
    /**
     * Check if a meal type can be added for a user on a date
     * @param userId The user ID
     * @param type The meal type
     * @param date The date
     * @return true if can add, false otherwise
     */
    boolean canAddMealType(long userId, MealType type, LocalDate date);
    
    /**
     * Get meal count for a type on a date
     * @param userId The user ID
     * @param type The meal type
     * @param date The date
     * @return Number of meals
     */
    int getMealCountForType(long userId, MealType type, LocalDate date);
    
    /**
     * Get available meal types for a user on a date
     * @param userId The user ID
     * @param date The date
     * @return List of meal types
     */
    List<MealType> getAvailableMealTypes(long userId, LocalDate date);
    
    /**
     * Close the database connection
     */
    void closeConnection();
} 