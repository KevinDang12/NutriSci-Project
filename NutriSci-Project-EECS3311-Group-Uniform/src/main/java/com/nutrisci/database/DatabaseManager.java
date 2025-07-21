package com.nutrisci.database;

import com.nutrisci.meal.Meal;
import com.nutrisci.meal.MealBuilder;
import com.nutrisci.meal.FoodItem;
import com.nutrisci.model.Gender;
import com.nutrisci.model.Goal;
import com.nutrisci.model.GoalFactory;
import com.nutrisci.model.GoalType;
import com.nutrisci.model.Units;
import com.nutrisci.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Instant;

import org.mindrot.jbcrypt.BCrypt;

import java.io.InputStream;
import java.io.IOException;
import com.nutrisci.meal.MealType;

/**
 * DatabaseManager handles all database operations for meals, users, and food items.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
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
            instance = new DatabaseManager();
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
    public boolean saveMeal(Meal meal, long userId) {
        // Generate a unique mealID (using epoch seconds for simplicity)
        long mealID = meal.getId();

        // String insertMealLogSQL = "INSERT INTO Meal_Log (UserID, MealID, MealType, EntryDate) VALUES (" + userId + ", " + mealID + ", '" + meal.getMealType() + "', '" + LocalDate.now() + "')";
        String insertMealLogSQL = "INSERT INTO Meal_Log (UserID, MealID, MealType, EntryDate) VALUES (?, ?, ?, ?)";

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
            try (PreparedStatement ps = connection.prepareStatement(insertMealLogSQL)) {
                
                ps.setLong(1, userId);
                ps.setLong(2, mealID);
                ps.setString(3, meal.getMealType().name());

                LocalDate localDate = LocalDate.now();
                java.sql.Date date = java.sql.Date.valueOf(localDate);
                ps.setDate(4, date);

                ps.executeUpdate();
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
     * Import a list of food IDs to the meal logging page
     * @param mealId The meal id for importing the meal
     * @return The list of food IDs
     */
    public List<Long> importMeal(long mealId) {
        
        String importMealSQL = "SELECT FoodID FROM Meal_Food WHERE MealID=" + mealId;

        List<Long> result = new ArrayList<>();

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(importMealSQL)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    long id = rs.getLong("FoodID");
                    result.add(id);
                }
            }

            connection.commit();

            return result;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Retrieve a list of meals from the user ID
     * @param userId The user ID to retrieve the meal
     * @return A Map of the meal IDs and their description
     */
    public Map<Long, String> importMeals(long userId) {
        
        String importMealSQL = "SELECT MealID, MealType FROM Meal_Log WHERE UserID=" + userId;

        Map<Long, String> result = new HashMap<>();

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(importMealSQL)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    long id = rs.getLong("MealID");
                    LocalDateTime dateTime = Instant.ofEpochSecond(id)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
                    String formatted = dateTime.format(formatter);
                    String mealType = rs.getString("MealType");
                    result.put(id, formatted + " - " + mealType);
                }
            }

            connection.commit();

            return result;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
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

    /**
     * Check if you can add a meal type for the user on a certain date
     * @param userId The User ID
     * @param type The Meal Type
     * @param date The current Date
     * @return true if successful, otherwise false
     */
    public boolean canAddMealType(long userId, MealType type, LocalDate date) {
        String checkForMeal = "Select MealType from Meal_Log where UserID = ? and EntryDate = ?";

        try (PreparedStatement ps = connection.prepareStatement(checkForMeal)) {

            ps.setLong(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String mealType = rs.getString("MealType");
                
                if (mealType.equals(type.name()) && !type.name().equals("SNACK")) {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the number of meals for a certain day
     * @param userId The user ID
     * @param type The type of meal
     * @param date The current date
     * @return The number of meals for a meal type
     */
    public int getMealCountForType(long userId, MealType type, LocalDate date) {
        String mealCountString = "Select Count(MealType) as amount from Meal_Log where UserID = ? and MealType = ? and EntryDate = ?";

        try (PreparedStatement ps = connection.prepareStatement(mealCountString)) {

            ps.setLong(1, userId);
            ps.setString(2, type.name());
            ps.setDate(3, java.sql.Date.valueOf(date));

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

    /**
     * Get a list of available meals that the user can log
     * @param userId The User ID
     * @param date The current date
     * @return A list of meals that the user has already logged for the date
     */
    public List<MealType> getAvailableMealTypes(long userId, LocalDate date) {
        String mealTypeString = "SELECT MealType FROM Meal_Log WHERE UserID = ? AND EntryDate = ? GROUP BY MealType";

        List<MealType> availabMealTypes = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(mealTypeString)) {

            ps.setLong(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
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

    /**
     * Perform a food swap on the meal
     * @param mealId The Meal ID
     * @param original The original food item
     * @param replacement The replacement food item
     * @return true if successful, otherwise false
     */
    public boolean swapFoodInMeal(Long mealId, FoodItem original, FoodItem replacement) {
        long originalId = original.getId();
        long replacementId = replacement.getId();

        // SQL: Get the list of food items in Meal_Food by meal ID, update the original food with new food item.
        String sql = "UPDATE Meal_Food SET FoodID=" + replacementId + " where MealID=" + mealId + " and FoodID=" + originalId;

        try {
            connection.setAutoCommit(false);

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
    public List<Meal> getMealsForUser(long userId, LocalDate startDate, LocalDate endDate) {
        List<Meal> meals = new ArrayList<>();
        String sql = "SELECT * FROM Meal_Log WHERE UserID = ? AND EntryDate BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(startDate));
            ps.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long mealId = rs.getLong("MealID");
                String mealTypeStr = rs.getString("MealType");
                // LocalDate entryDate = rs.getDate("EntryDate").toLocalDate();

                MealType mealType = MealType.valueOf(mealTypeStr);
                MealBuilder mealBuilder = new MealBuilder().setMealType(mealType);

                // Load food items for this meal
                String foodSql = "SELECT FoodID FROM Meal_Food WHERE MealID = " + mealId;
                
                List<FoodItem> foodItems = new ArrayList<>();

                try (PreparedStatement foodPs = connection.prepareStatement(foodSql)) {
                    ResultSet foodRs = foodPs.executeQuery();
                    while (foodRs.next()) {
                        long foodId = foodRs.getLong("FoodID");
                        FoodItem item = loadFoodItem(foodId);
                        foodItems.add(item);
                    }
                }

                mealBuilder.setFoodItems(foodItems);
                Meal meal = mealBuilder.buildPreview();
                meal.setId(mealId);
                meals.add(meal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        FoodItem item = new FoodItem(foodId, foodDesc, nutrients, foodGroup);
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

        return items;
    }

    /**
     * Retrieve the Food IDs and their description
     * @return Map of he Food IDs and their description
     */
    public Map<Long, String> getFoodItems() {
        Map<Long, String> foodHashMap = new HashMap<Long, String>();
        String sql = "select foodId, foodDescription from FOOD_NAME";

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
     * Save a user profile to the database with password hashing (bcrypt).
     * @param user User object
     * @return true if successful, false if error
     */
    public boolean saveUser(User user) {
        String password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        long userID = Instant.now().getEpochSecond();

        String saveUserSQL = "INSERT INTO Meal_User VALUES (" +
        userID + ", " +
        user.getName() + ", " + 
        user.getEmail() + ", " +
        password + ", " +
        user.getGender().name() + ", " +
        user.getDateOfBirth() + " , " +
        user.getHeight() + ", " +
        user.getWeight() + ", " +
        user.getUnits() + ", " +
        user.getUserGoal().getGoalType().name() + ", " +
        user.getUserGoal().getGoalDescription() + ")";

        try (PreparedStatement ps = connection.prepareStatement(saveUserSQL)) {

            ps.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { 
                connection.setAutoCommit(true); 
            } catch (SQLException e) {
                e.printStackTrace(); 
            }
        }
    }

    /**
     * Updates an existing user profile with change tracking.
     * @param user User object
     * @return true if successful, false if error
     */
    public boolean updateUserProfile(User user) {
        String password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        // Get user from User object
        // long userID = user.getId();
        long userID = 0;

        String updateUserSQL = "UPDATE User_Meal SET" +
        "UserPassword=" +  password +
        ", Gender=" + user.getGender().name() +
        ", DoB=" + user.getDateOfBirth() + 
        ", Height=" + user.getHeight() + 
        ", Weight=" + user.getWeight() + 
        ", Units=" + user.getUnits() + 
        ", GoalType=" + user.getUserGoal().getGoalType().name() +
        ", GoalDescription=" + user.getUserGoal().getGoalDescription() +
        " WHERE UserID=" + userID;

        try (PreparedStatement ps = connection.prepareStatement(updateUserSQL)) {
            ps.executeUpdate();
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
     * Authenticates user credentials and returns User object if valid.
     * @param email User email
     * @param password Plain text password
     * @return User object if valid, null if not
     */
    public User authenticateUser(String userId, String password) {
        String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String checkForMeal = "Select * from Meal_User where UserID=" + userId + " and UserPassword=" + hashPassword;

        try (PreparedStatement ps = connection.prepareStatement(checkForMeal)) {

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return null; // No matching user
            }

            User user = new User();
            // user.setId(); // Set User ID
            user.setName(rs.getString("Username"));
            user.setEmail(rs.getString("Email"));
            user.setPassword(password);
            user.setGender(Gender.valueOf(rs.getString("Gender")));
            user.setDateOfBirth(rs.getDate("DoB").toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            user.setHeight(rs.getDouble("Height"));
            user.setWeight(rs.getDouble("Weight"));
            user.setUnits(Units.valueOf(rs.getString("Units")));

            /**
             * FIX: Need a better way to create Goal
             */
            Matcher matcher = Pattern.compile("\\d+").matcher(rs.getString("GoalDescription"));
            int value = 0;

            if (matcher.find()) {
                value = Integer.parseInt(matcher.group());
            }

            // FIX: Cannot set description as of now, it must use regex to get the number
            Goal userGoal = GoalFactory.createGoal(GoalType.valueOf(rs.getString("GoalType")), value);
            user.setUserGoal(userGoal);

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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
