package com.nutrisci.database;

import com.nutrisci.meal.Meal;
import com.nutrisci.meal.MealType;
import com.nutrisci.meal.FoodItem;
import com.nutrisci.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * MySQL database adapter implementation.
 * This adapter wraps the existing DatabaseManager to provide
 * MySQL-specific database operations while maintaining the same interface.
 * 
 * @author NutriSci Team
 * @version 1.0
 */
public class MySQLDatabaseAdapter implements DatabaseAdapter {
    
    private DatabaseManager databaseManager;
    
    /**
     * Constructor that initializes the MySQL database adapter
     */
    public MySQLDatabaseAdapter() {
        this.databaseManager = DatabaseManager.getInstance();
    }
    
    @Override
    public boolean saveMeal(Meal meal, long userId) {
        return databaseManager.saveMeal(meal, userId);
    }
    
    @Override
    public boolean updateMeal(Meal meal) {
        return databaseManager.updateMeal(meal);
    }
    
    @Override
    public boolean deleteMeal(Long mealId) {
        return databaseManager.deleteMeal(mealId);
    }
    
    @Override
    public List<Meal> getMealsForUser(long userId, LocalDate startDate, LocalDate endDate) {
        return databaseManager.getMealsForUser(userId, startDate, endDate);
    }
    
    @Override
    public boolean saveUser(User user) {
        return databaseManager.saveUser(user);
    }
    
    @Override
    public boolean updateUserProfile(User user) {
        return databaseManager.updateUserProfile(user);
    }
    
    @Override
    public User authenticateUser(String email, String password) {
        return databaseManager.authenticateUser(email, password);
    }
    
    @Override
    public boolean checkIfUserExists(String email) {
        return databaseManager.checkIfUserExists(email);
    }
    
    @Override
    public FoodItem loadFoodItem(Long foodId) {
        return databaseManager.loadFoodItem(foodId);
    }
    
    @Override
    public Map<Long, String> getFoodItems() {
        return databaseManager.getFoodItems();
    }
    
    @Override
    public Map<Long, String> importMeals(long userId) {
        return databaseManager.importMeals(userId);
    }
    
    @Override
    public List<Long> importMeal(long mealId) {
        return databaseManager.importMeal(mealId);
    }
    
    @Override
    public boolean swapFoodInMeal(Long mealId, FoodItem original, FoodItem replacement) {
        return databaseManager.swapFoodInMeal(mealId, original, replacement);
    }
    
    @Override
    public boolean canAddMealType(long userId, MealType type, LocalDate date) {
        return databaseManager.canAddMealType(userId, type, date);
    }
    
    @Override
    public int getMealCountForType(long userId, MealType type, LocalDate date) {
        return databaseManager.getMealCountForType(userId, type, date);
    }
    
    @Override
    public List<MealType> getAvailableMealTypes(long userId, LocalDate date) {
        return databaseManager.getAvailableMealTypes(userId, date);
    }
    
    @Override
    public void closeConnection() {
        databaseManager.closeConnection();
    }
} 