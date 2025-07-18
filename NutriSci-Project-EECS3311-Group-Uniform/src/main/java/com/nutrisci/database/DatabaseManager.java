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
import com.nutrisci.meal.MealType;

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
    // public boolean saveMeal(Meal meal) {
    //     // Begin transaction
    //     try {
    //         connection.setAutoCommit(false);
    //         // Insert meal record (date, type, user_id, notes)
    //         // Insert food_items for each item in meal
    //         // Insert nutritional_data record
    //         // Commit transaction
    //         connection.commit();
    //         return true;
    //     } catch (SQLException e) {
    //         try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
    //         e.printStackTrace();
    //         return false;
    //     } finally {
    //         try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
    //     }
    // }

    public boolean saveMeal(Meal meal) {
        // Generate a unique mealID (using epoch seconds for simplicity)
        long mealID = meal.getId();

        // TODO: Replace 'userId' with the actual user ID as needed
        String insertMealLogSQL = "INSERT INTO Meal_Log (UserID, MealID, MealType, EntryDate) VALUES (1, " + mealID + ", '" + meal.getMealType() + "', '" + LocalDate.now() + "')";

        // Build a single SQL statement for all food items
        StringBuilder insertMealFoodSQL = new StringBuilder("INSERT INTO Meal_Food (MealID, FoodID) VALUES ");
        List<FoodItem> foodItems = meal.getFoodItems();
        for (int i = 0; i < foodItems.size(); i++) {
            FoodItem item = foodItems.get(i);
            insertMealFoodSQL.append("(").append(mealID).append(", ").append(item.getId()).append(")");
            if (i < foodItems.size() - 1) {
                insertMealFoodSQL.append(", ");
            }
        }
        insertMealFoodSQL.append(";");

        try {
            connection.setAutoCommit(false);

            // Execute Meal_Log insert
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(insertMealLogSQL);
            }

            // Execute Meal_Food insert if there are food items
            if (!foodItems.isEmpty()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(insertMealFoodSQL.toString());
                }
            }

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
        long mealID = meal.getId(); // use getter for id
        List<FoodItem> foodItems = meal.getFoodItems();

        // Build a single SQL statement for all food items
        StringBuilder insertMealFoodSQL = new StringBuilder("INSERT INTO Meal_Food (MealID, FoodID) VALUES ");
        for (int i = 0; i < foodItems.size(); i++) {
            FoodItem item = foodItems.get(i);
            insertMealFoodSQL.append("(").append(mealID).append(", ").append(item.getId()).append(")");
            if (i < foodItems.size() - 1) {
                insertMealFoodSQL.append(", ");
            }
        }
        insertMealFoodSQL.append(";");

        try {
            connection.setAutoCommit(false);

            // Delete existing Meal_Food entries for this mealID
            String deleteSQL = "DELETE FROM Meal_Food WHERE MealID = " + mealID;
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(deleteSQL);
            }

            // Insert new Meal_Food entries if there are food items
            if (!foodItems.isEmpty()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(insertMealFoodSQL.toString());
                }
            }

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

    public boolean canAddMealType(long userId, MealType type, LocalDate date) {
        String checkForMeal = "Select MealType from Meal_Log where UserID=" + userId + " and EntryDate=" + date;

        try (PreparedStatement ps = connection.prepareStatement(checkForMeal)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String mealType = rs.getString("MealType");
                
                if (mealType.equals(type.name())) {
                    return true;
                }
            }

            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getMealCountForType(long userId, MealType type, LocalDate date) {
        String mealCountString = "Select Count(MealType) as amount from Meal_Log where UserID=" + userId + " and MealType = " + type + " and EntryDate=" + date;

        try (PreparedStatement ps = connection.prepareStatement(mealCountString)) {

            ResultSet rs = ps.executeQuery();
            int amount = 0;
            if (rs.next()) {
                amount = rs.getInt("amount");
            }
            return amount;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<MealType> getAvailableMealTypes(long userId, LocalDate date) {
        String mealCountString = "Select MealType from Meal_Log where UserID=" + userId + " and EntryDate=" + date;

        List<MealType> availabMealTypes = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(mealCountString)) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String type = rs.getString("MealType");
                MealType mealCategory = MealType.valueOf(type);
                availabMealTypes.add(mealCategory);
            }
            
            return availabMealTypes;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean swapFoodInMeal(Long mealId, FoodItem original, FoodItem replacement) {
        long originalId = original.getId();
        long replacementId = replacement.getId();

        // SQL: Get the list of food items in Meal_Food by meal ID, update the original food with new food item.

        // Build a single SQL statement for all food items
        String sql = "UPDATE Meal_Food SET FoodID=" + replacementId + " where MealID=" + mealId + " and FoodID=" + originalId;

        try {
            connection.setAutoCommit(false);

            // Insert new Meal_Food entries if there are food items
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(sql);
            }

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

            // Delete from Meal_Food first (to avoid FK constraint issues)
            String deleteMealFoodSQL = "DELETE FROM Meal_Food WHERE MealID = " + mealId;
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(deleteMealFoodSQL);
            }

            // Delete from Meal_Log
            String deleteMealLogSQL = "DELETE FROM Meal_Log WHERE MealID = " + mealId;
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(deleteMealLogSQL);
            }

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
        String sql = "SELECT MealID FROM Meal_Log WHERE UserID = " + userId +" AND EntryDate BETWEEN " + startDate + " AND " + endDate;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long mealId = rs.getLong("MealID");
                String mealTypeStr = rs.getString("MealType");
                LocalDate entryDate = rs.getDate("EntryDate").toLocalDate();
                MealType mealType = MealType.valueOf(mealTypeStr);
                Meal meal = com.nutrisci.meal.MealFactory.createMeal(mealType, entryDate, mealId);
                if (meal == null) continue;
                // Load food items for this meal
                String foodSql = "SELECT FoodID FROM Meal_Food WHERE MealID = " + mealId;
                try (PreparedStatement foodPs = connection.prepareStatement(foodSql)) {
                    ResultSet foodRs = foodPs.executeQuery();
                    while (foodRs.next()) {
                        long foodId = foodRs.getLong("FoodID");
                        FoodItem item = loadFoodItem(foodId);
                        meal.addFoodItem(item);
                    }
                }
                meals.add(meal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meals;
    }

    // public List<Long> getMealIdsForUserBetweenDates(String userId, LocalDate startDate, LocalDate endDate) {
    //     List<Long> mealIds = new ArrayList<>();
    //     String sql = "SELECT MealID FROM Meal_Log WHERE UserID = " + userId +" AND EntryDate BETWEEN " + startDate + " AND " + endDate;
    //     try (PreparedStatement ps = connection.prepareStatement(sql)) {
    //         ResultSet rs = ps.executeQuery();
    //         while (rs.next()) {
    //             mealIds.add(rs.getLong("MealID"));
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     return mealIds;
    // }

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

    public HashMap<Long, String> getFoodItems() {
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
