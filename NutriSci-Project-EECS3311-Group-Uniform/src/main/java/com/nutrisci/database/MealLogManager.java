package com.nutrisci.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nutrisci.meal.Breakfast;
import com.nutrisci.meal.Dinner;
import com.nutrisci.meal.FoodItem;
import com.nutrisci.meal.Lunch;
import com.nutrisci.meal.Meal;
import com.nutrisci.meal.MealType;
import com.nutrisci.meal.Snack;

/**
 * MealLogManager handles all database operations for meals.
 */
public class MealLogManager {
    /**
     * Persists a meal to the database with all food items and nutritional data.
     * Uses transactions and prepared statements.
     * @param meal Meal to save
     * @return true if successful, false if error (with rollback)
     * helped by AI
     */
    public boolean saveMeal(Meal meal, long userId, Connection connection) {
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

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Import a list of food IDs to the meal logging page
     * @param mealId The meal id for importing the meal
     * @return The list of food IDs
     */
    public List<Long> importMeal(long mealId, Connection connection) {
        
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
     * helped by AI
     */
    public Map<Long, String> importMeals(long userId, Connection connection) {
        
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
    public boolean updateMeal(Meal meal, Connection connection) {
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
    public boolean canAddMealType(long userId, MealType type, LocalDate date, Connection connection) {
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
    public int getMealCountForType(long userId, MealType type, LocalDate date, Connection connection) {
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
    public List<MealType> getAvailableMealTypes(long userId, LocalDate date, Connection connection) {
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
    public boolean swapFoodInMeal(Long mealId, FoodItem original, FoodItem replacement, Connection connection) {
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
    public boolean deleteMeal(Long mealId, Connection connection) {
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
     * helped by AI
     */
    public List<Meal> getMealsForUser(long userId, LocalDate startDate, LocalDate endDate, Connection connection) {
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
                LocalDate entryDate = rs.getDate("EntryDate").toLocalDate();

                MealType mealType = MealType.valueOf(mealTypeStr);
                
                // Create meal directly from registry without validation (since we're loading from DB)
                Meal meal = createMealFromRegistry(mealType, mealId);

                // Load food items for this meal
                String foodSql = "SELECT FoodID FROM Meal_Food WHERE MealID = " + mealId;
                
                List<FoodItem> foodItems = new ArrayList<>();

                try (PreparedStatement foodPs = connection.prepareStatement(foodSql)) {
                    ResultSet foodRs = foodPs.executeQuery();
                    FoodLogManager foodManager = new FoodLogManager();
                    while (foodRs.next()) {
                        long foodId = foodRs.getLong("FoodID");
                        FoodItem item = foodManager.loadFoodItem(foodId, connection);
                        foodItems.add(item);
                    }
                }

                // Set food items directly on the meal
                meal.setFoodItems(foodItems);
                meal.setId(mealId);
                meal.setEntryDate(entryDate.atStartOfDay());
                meals.add(meal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meals;
    }

    /**
     * Creates a meal directly from the registry without validation (for loading from DB)
     */
    private Meal createMealFromRegistry(MealType type, long mealId) {
        Meal meal = null;
        switch (type) {
            case BREAKFAST:
                meal = new Breakfast();
                break;
            case LUNCH:
                meal = new Lunch();
                break;
            case DINNER:
                meal = new Dinner();
                break;
            case SNACK:
                meal = new Snack();
                break;
        }
        if (meal != null) {
            meal.setId(mealId);
        }
        return meal;
    }
}
