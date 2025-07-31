package com.nutrisci.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nutrisci.meal.FoodItem;

public class FoodDatabaseManager {

    private static UserDatabaseManager instance;

    public static UserDatabaseManager getInstance() {
        if (instance == null) {
            instance = new UserDatabaseManager();
        }
        return instance;
    }

    /**
     * Loads a food item with all its nutritional data from the database.
     * helped by AI
     */
    public FoodItem loadFoodItem(Long foodId, Connection connection) {
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
    public List<FoodItem> searchFoodItems(String searchTerm, Connection connection) {
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
    public List<FoodItem> getFoodItemsByGroup(String foodGroup, Connection connection) {
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
    public Map<Long, String> getFoodItems(Connection connection) {
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
}
