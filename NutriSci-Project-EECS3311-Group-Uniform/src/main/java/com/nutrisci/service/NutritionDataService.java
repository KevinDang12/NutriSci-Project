package com.nutrisci.service;

import com.nutrisci.calculator.NutritionalCalculator;
import com.nutrisci.calculator.NutritionalData;
import com.nutrisci.database.DatabaseManager;
import com.nutrisci.meal.Meal;
import com.nutrisci.model.GoalType;
import com.nutrisci.util.UserSessionManager;
import com.nutrisci.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Service to fetch nutrition data for charts
public class NutritionDataService {
    private DatabaseManager dbManager;
    private NutritionalCalculator calculator;
    private UserSessionManager userSessionManager;
    
    public NutritionDataService() {
        this.dbManager = DatabaseManager.getInstance();
        this.calculator = new NutritionalCalculator();
        this.userSessionManager = UserSessionManager.getInstance();
    }
    
    /**
     * Get the current user's ID from the session
     * @return The current user's ID, or 0 if no user is logged in
     */
    private long getCurrentUserId() {
        User currentUser = userSessionManager.getCurrentUser();
        return currentUser != null ? currentUser.getId() : 0;
    }
    
    /**
     * Get daily nutrition data for a specific nutrient
     */
    public Map<String, Double> getDailyNutritionData(GoalType nutrientType) {
        Map<String, Double> data = new HashMap<>();
        LocalDate today = LocalDate.now();
        
        long userId = getCurrentUserId();
        if (userId == 0) {
            return data; // Return empty data if no user logged in
        }
        
        // Get meals for today
        List<Meal> meals = dbManager.getMealsForUser(userId, today, today);
        
        // Group by meal type and calculate totals
        Map<String, NutritionalData> mealTypeTotals = new HashMap<>();
        
        for (Meal meal : meals) {
            String mealType = meal.getMealType().toString();
            NutritionalData mealNutrition = calculator.calculateMealNutrition(meal.getFoodItems());
            
            if (mealTypeTotals.containsKey(mealType)) {
                mealTypeTotals.put(mealType, mealTypeTotals.get(mealType).add(mealNutrition));
            } else {
                mealTypeTotals.put(mealType, mealNutrition);
            }
        }
        
        // Extract the specific nutrient data
        for (Map.Entry<String, NutritionalData> entry : mealTypeTotals.entrySet()) {
            double value = getNutrientValue(entry.getValue(), nutrientType);
            data.put(entry.getKey(), value);
        }
        
        return data;
    }
    
    /**
     * Get monthly nutrition data for a specific nutrient
     */
    public Map<String, Double> getMonthlyNutritionData(GoalType nutrientType) {
        Map<String, Double> data = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        
        long userId = getCurrentUserId();
        if (userId == 0) {
            return data; // Return empty data if no user logged in
        }
        
        // Get meals for the current month
        List<Meal> meals = dbManager.getMealsForUser(userId, startOfMonth, today);
        
        // Group by week and calculate totals
        Map<String, NutritionalData> weekTotals = new HashMap<>();
        
        for (Meal meal : meals) {
            LocalDate mealDate = meal.getCreatedAt() != null ? 
                meal.getCreatedAt().toLocalDate() : LocalDate.now();
            String weekKey = getWeekKey(mealDate);
            
            NutritionalData mealNutrition = calculator.calculateMealNutrition(meal.getFoodItems());
            
            if (weekTotals.containsKey(weekKey)) {
                weekTotals.put(weekKey, weekTotals.get(weekKey).add(mealNutrition));
            } else {
                weekTotals.put(weekKey, mealNutrition);
            }
        }
        
        // Extract the specific nutrient data
        for (Map.Entry<String, NutritionalData> entry : weekTotals.entrySet()) {
            double value = getNutrientValue(entry.getValue(), nutrientType);
            data.put(entry.getKey(), value);
        }
        
        return data;
    }
    
    /**
     * Get the nutrient value from NutritionalData based on GoalType
     */
    private double getNutrientValue(NutritionalData data, GoalType nutrientType) {
        switch (nutrientType) {
            case CALORIES:
                return data.getCalories();
            case PROTEIN:
                return data.getProtein();
            case FIBRE:
                return data.getFiber();
            default:
                return 0.0;
        }
    }
    
    /**
     * Get week key for grouping (e.g., "Week 1", "Week 2")
     */
    private String getWeekKey(LocalDate date) {
        int weekOfMonth = ((date.getDayOfMonth() - 1) / 7) + 1;
        return "Week " + weekOfMonth;
    }
    
    /**
     * Get the unit label for a nutrient type
     */
    public String getUnitLabel(GoalType nutrientType) {
        switch (nutrientType) {
            case CALORIES:
                return "kcal";
            case PROTEIN:
                return "g";
            case FIBRE:
                return "g";
            default:
                return "";
        }
    }
} 