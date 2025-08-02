package com.nutrisci.database;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import com.nutrisci.meal.FoodItem;
import com.nutrisci.meal.Meal;
import com.nutrisci.meal.MealType;
import com.nutrisci.model.User;

import java.io.InputStream;
import java.io.IOException;

/**
 * DatabaseManager handles all database operations for meals, users, and food items.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private MealLogManager mealLogManager;
    private FoodLogManager foodLogManager;
    private UserManager userManager;
    private Connection connection;
    private Properties dbProperties;

    // Private constructor for singleton
    private DatabaseManager() {
        loadConfiguration();
        setupConnection();
        mealLogManager = new MealLogManager();
        foodLogManager = new FoodLogManager();
        userManager = new UserManager();
    }

    /**
     * Returns the singleton instance of DatabaseManager (thread-safe, double-checked locking)
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Loads database configuration from a properties file (db.properties).
     * Expects db.url, db.user, db.password for Azure MySQL.
     * helped by AI
     */
    private void loadConfiguration() {
        dbProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                dbProperties.load(input);
                System.out.println("Database properties loaded successfully!");
                System.out.println("Properties found: " + dbProperties.keySet());
            } else {
                System.err.println("Database configuration file (db.properties) not found!");
                throw new IOException("Database configuration file not found");
            }
        } catch (IOException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets up the connection to Azure MySQL using JDBC and loaded properties.
     * helped by AI
     */
    private void setupConnection() {
        try {
            String url = dbProperties.getProperty("db.url");
            String user = dbProperties.getProperty("db.user");
            String password = dbProperties.getProperty("db.password");
            
            System.out.println("Attempting to connect to database...");
            System.out.println("URL: " + url);
            System.out.println("User: " + user);
            System.out.println("Password: " + (password != null ? "***" : "NULL"));
            
            connection = DriverManager.getConnection(url, user, password);
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("Database connection established successfully!");
            } else {
                System.err.println("Failed to establish database connection!");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
    }

    /**
     * Use Meal Manager to save the meal
     * @param meal The meal to save
     * @param userId The user ID
     * @return true if successful, otherwise false
     */
    public boolean saveMeal(Meal meal, long userId) {
        return mealLogManager.saveMeal(meal, userId, connection);
    }

    /**
     * Use Meal Manager to update the meal
     * @param meal The meal to update
     * @return true if successful, otherwise false
     */
    public boolean updateMeal(Meal meal) {
        return mealLogManager.updateMeal(meal, connection);
    }

    /**
     * Use Meal Manager to delete the meal
     * @param mealId The meal ID
     * @return true if successful, otherwise false
     */
    public boolean deleteMeal(Long mealId) {
        return mealLogManager.deleteMeal(mealId, connection);
    }

    /**
     * Use Meal Manager to get the meals for the user
     * @param userId The user ID
     * @param startDate The start date
     * @param endDate The end date
     * @return The list of meals
     */
    public List<Meal> getMealsForUser(long userId, LocalDate startDate, LocalDate endDate) {
        return mealLogManager.getMealsForUser(userId, startDate, endDate, connection);
    }

    /**
     * Use Meal Manager to import the meals
     * @param userId The user ID
     * @return The map of meals
     */
    public Map<Long, String> importMeals(long userId) {
        return mealLogManager.importMeals(userId, connection);
    }

    /**
     * Use Meal Manager to import the meal
     * @param mealId The meal ID
     * @return The list of food IDs
     */
    public List<Long> importMeal(long mealId) {
        return mealLogManager.importMeal(mealId, connection);
    }

    /**
     * Use Meal Manager to swap the food in the meal
     * @param mealId The meal ID
     * @param original The original food item
     * @param replacement The replacement food item
     * @return true if successful, otherwise false
     */
    public boolean swapFoodInMeal(Long mealId, FoodItem original, FoodItem replacement) {
        return mealLogManager.swapFoodInMeal(mealId, original, replacement, connection);
    }

    /**
     * Use Meal Manager to check if the meal type can be added
     * @param userId The user ID
     * @param type The meal type
     * @param date The date
     * @return true if the meal type can be added, otherwise false
     */
    public boolean canAddMealType(long userId, MealType type, LocalDate date) {
        return mealLogManager.canAddMealType(userId, type, date, connection);
    }

    /**
     * Use Meal Manager to get the meal count for the meal type
     * @param userId The user ID
     * @param type The meal type
     * @param date The date
     * @return The meal count
     */
    public int getMealCountForType(long userId, MealType type, LocalDate date) {
        return mealLogManager.getMealCountForType(userId, type, date, connection);
    }

    /**
     * Use Meal Manager to get the available meal types
     * @param userId The user ID
     * @param date The date
     * @return The list of meal types
     */
    public List<MealType> getAvailableMealTypes(long userId, LocalDate date) {
        return mealLogManager.getAvailableMealTypes(userId, date, connection);
    }

    /**
     * Use Food Manager to load the food item
     * @param foodId The food ID
     * @return The food item
     */
    public FoodItem loadFoodItem(Long foodId) {
        return foodLogManager.loadFoodItem(foodId, connection);
    }

    /**
     * Use Food Manager to get the food items
     * @return The map of food items
     */
    public Map<Long, String> getFoodItems() {
        return foodLogManager.getFoodItems(connection);
    }

    /**
     * Use User Manager to save the user
     * @param user The user to save
     * @return true if successful, otherwise false
     */
    public boolean saveUser(User user) {
        return userManager.saveUser(user, connection);
    }

    /**
     * Use User Manager to update the user profile
     * @param user The user to update
     * @return true if successful, otherwise false
     */
    public boolean updateUserProfile(User user) {
        return userManager.updateUserProfile(user, connection);
    }

    /**
     * Use User Manager to authenticate the user
     * @param email The email of the user
     * @param password The password of the user
     * @return The user if successful, otherwise null
     */
    public User authenticateUser(String email, String password) {
        return userManager.authenticateUser(email, password, connection);
    }

    /**
     * Use User Manager to check if the user exists
     * @param email The email of the user
     * @return true if the user exists, otherwise false
     */
    public boolean checkIfUserExists(String email) {
        return userManager.checkIfUserExists(email, connection);
    }

    /**
     * Properly closes database connections and cleans up resources.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            // Close prepared statements, release pool, etc.
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
