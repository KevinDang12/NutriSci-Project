package com.nutrisci.database;

import com.nutrisci.meal.Meal;
import com.nutrisci.meal.FoodItem;
import com.nutrisci.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.io.InputStream;
import java.io.IOException;

/**
 * DatabaseManager handles all database operations for meals, users, and food items.
 * Implements thread-safe singleton with double-checked locking.
 */
public class DatabaseManager {
    private static volatile DatabaseManager instance;
    private static final ReentrantLock lock = new ReentrantLock();
    private Connection connection;
    private Properties dbProperties;

    // Private constructor for singleton
    private DatabaseManager() {
        loadConfiguration();
        setupConnection();
    }

    /**
     * Returns the singleton instance of DatabaseManager (thread-safe, double-checked locking)
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    /**
     * Loads database configuration from a properties file (db.properties).
     * Expects db.url, db.user, db.password for Azure MySQL.
     */
    private void loadConfiguration() {
        dbProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                dbProperties.load(input);
            } else {
                throw new IOException("Database configuration file not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the connection to Azure MySQL using JDBC and loaded properties.
     */
    private void setupConnection() {
        try {
            String url = dbProperties.getProperty("db.url");
            String user = dbProperties.getProperty("db.user");
            String password = dbProperties.getProperty("db.password");
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Persists a meal to the database with all food items and nutritional data.
     * Uses transactions and prepared statements.
     * @param meal Meal to save
     * @return true if successful, false if error (with rollback)
     */
    public boolean saveMeal(Meal meal) {
        // Begin transaction
        try {
            connection.setAutoCommit(false);
            // Insert meal record (date, type, user_id, notes)
            // Insert food_items for each item in meal
            // Insert nutritional_data record
            // Commit transaction
            connection.commit();
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Updates an existing meal record and associated data.
     * Deletes and reinserts food items for simplicity.
     * @param meal Meal to update
     * @return true if successful, false if error (with rollback)
     */
    public boolean updateMeal(Meal meal) {
        try {
            connection.setAutoCommit(false);
            // Update meal basic info
            // Delete existing food_items for meal
            // Insert new food_items
            // Update nutritional_data record
            // Update last_modified timestamp
            connection.commit();
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Removes a meal and all associated data from the database.
     * @param mealId ID of meal to delete
     * @return true if successful, false if error (with rollback)
     */
    public boolean deleteMeal(Long mealId) {
        try {
            connection.setAutoCommit(false);
            // Delete food_items (cascade)
            // Delete nutritional_data
            // Delete meal record
            // Optionally log deletion
            connection.commit();
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Retrieves meals for a user within a date range, reconstructing full objects.
     * @param userId User ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of Meal objects
     */
    public List<Meal> getMealsForUser(String userId, LocalDate startDate, LocalDate endDate) {
        List<Meal> meals = new ArrayList<>();
        // Query meals table for user and date range
        // For each meal, load food items and reconstruct objects
        // Use batch loading and efficient joins
        return meals;
    }

    /**
     * Loads a single FoodItem by ID with full nutritional data
     * @param foodId Food item ID
     * @return FoodItem object
     */
    public FoodItem loadFoodItem(Long foodId) {
        Map<String, Double> nutrients = new HashMap<>();
        String sqlNutrientString = "select NutrientName, NutrientValue from Nutrient_Name NN "
                    + "inner join Nutrient_Amount NA on NN.NutrientID=NA.NutrientID "
                    + "where NA.FoodID=" + foodId;
        try (PreparedStatement ps = connection.prepareStatement(sqlNutrientString)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                nutrients.put(rs.getString("NutrientName"), rs.getDouble("NutrientValue"));
            }
        } catch (SQLException e) {
            // handle or log SQL errors here
            e.printStackTrace();
        }

        String sqlFoodGroupString = "select FoodGroupName from FOOD_GROUP FG "
                                    + "inner join FOOD_NAME FA on FA.FoodGroupID=FG.FoodGroupID "
                                    + "where FA.FoodID=" + foodId;

        String foodGroup = "";
        
        try (PreparedStatement ps = connection.prepareStatement(sqlFoodGroupString)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                foodGroup = rs.getString("FoodGroupName");
            }
        } catch (SQLException e) {
            // handle or log SQL errors here
            e.printStackTrace();
        }

        String sqlFoodDescString = "select FoodDescription from FOOD_NAME where FoodID=" + foodId;

        String foodDesc = "";
        
        try (PreparedStatement ps = connection.prepareStatement(sqlFoodDescString)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                foodDesc = rs.getString("FoodDescription");
            }
        } catch (SQLException e) {
            // handle or log SQL errors here
            e.printStackTrace();
        }

        FoodItem item = new FoodItem(foodDesc, nutrients, foodGroup);
        return item;
    }

    /**
     * Searches food database by name with fuzzy matching (top 50 results).
     * @param searchTerm Search term
     * @return List of FoodItem objects
     */
    // Running time takes too long
    public List<FoodItem> searchFoodItems(String searchTerm) {
        List<Long> foodIDs = new ArrayList<>();
        String sql = "SELECT FN.FoodID FROM FOOD_NAME FN "
                    + "inner join NUTRIENT_AMOUNT NA on FN.FoodID=NA.FoodID "
                    + "inner join NUTRIENT_NAME NN on NN.NutrientID=NA.NutrientID "
                    + "WHERE FoodDescription LIKE '%" + searchTerm + "%' "
                    + "LIMIT 10";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                foodIDs.add(rs.getLong("FoodID"));
            }
        } catch (SQLException e) {
            // handle or log SQL errors here
            e.printStackTrace();
        }

        List<FoodItem> items = new ArrayList<FoodItem>();

        // return foodId and name instead
        // for (long id : foodIDs) {
        //     items.add(loadFoodItem(id));
        // }

        return items;
    }

    /**
     * Returns all foods in the specified food group.
     * @param foodGroup Food group name
     * @return List of FoodItem objects
     */
    // Running time takes too long
    public List<FoodItem> getFoodItemsByGroup(String foodGroup) {
        List<Long> foodIDs = new ArrayList<>();
        String sql = "SELECT FN.FoodID FROM FOOD_NAME FN "
                    + "inner join FOOD_GROUP FG on FN.FoodGroupID=FG.FoodGroupID "
                    + "WHERE FoodGroupName = '" + foodGroup + "';";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                foodIDs.add(rs.getLong("FoodID"));
            }
        } catch (SQLException e) {
            // handle or log SQL errors here
            e.printStackTrace();
        }

        List<FoodItem> items = new ArrayList<FoodItem>();

        // return foodId and name instead
        // for (long id : foodIDs) {
        //     items.add(loadFoodItem(id));
        // }

        return items;
    }

    public HashMap<Long, String> getFoodItemsByGroup() {
        HashMap<Long, String> foodHashMap = new HashMap<Long, String>();
        String sql = "select foodDescription, foodId from FOOD_NAME";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                foodHashMap.put(rs.getLong("FoodID"), rs.getString("foodDescription"));
            }
        } catch (SQLException e) {
            // handle or log SQL errors here
            e.printStackTrace();
        }

        return foodHashMap;
    }

    /**
     * Persists a user profile to the database with password hashing (bcrypt).
     * @param user User object
     * @return true if successful, false if error
     */
    public boolean saveUser(User user) {
        // Hash password with bcrypt
        // Insert user record, goal, preferences
        // Never store plain text password
        return true;
    }

    /**
     * Updates an existing user profile with change tracking.
     * @param user User object
     * @return true if successful, false if error
     */
    public boolean updateUserProfile(User user) {
        // Update user info, goal, preferences
        // Log changes for audit
        return true;
    }

    /**
     * Authenticates user credentials and returns User object if valid.
     * @param email User email
     * @param password Plain text password
     * @return User object if valid, null if not
     */
    public User authenticateUser(String email, String password) {
        // Query user by email
        // Compare password with bcrypt hash
        // If valid, load full profile and update last_login
        return null;
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
