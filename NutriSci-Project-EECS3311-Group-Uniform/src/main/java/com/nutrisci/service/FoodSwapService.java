package com.nutrisci.service;

import com.nutrisci.meal.FoodItem;
import com.nutrisci.model.Goal;
import com.nutrisci.model.GoalType;
import com.nutrisci.database.DatabaseManager;

import java.util.*;

// Service for smart food item swapping based on user goals
public class FoodSwapService {
    private DatabaseManager dbManager;
    
    public FoodSwapService() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Suggests a food swap based on the user's goal
     * @param currentFoodItems List of current food items in the meal
     * @param userGoal User's nutrition goal
     * @return FoodSwapSuggestion with original and replacement items
     */
    public FoodSwapSuggestion suggestSwap(List<FoodItem> currentFoodItems, Goal userGoal) {
        if (currentFoodItems == null || currentFoodItems.isEmpty() || userGoal == null) {
            return null;
        }
        
        // Find the food item with the lowest value for the goal nutrient
        FoodItem worstItem = findWorstFoodItem(currentFoodItems, userGoal.getType());
        if (worstItem == null) {
            return null;
        }
        
        // Find a better replacement
        FoodItem replacement = findBetterReplacement(worstItem, userGoal);
        if (replacement == null) {
            return null;
        }
        
        return new FoodSwapSuggestion(worstItem, replacement, userGoal);
    }
    
    /**
     * Find the food item with the lowest value for the target nutrient
     */
    private FoodItem findWorstFoodItem(List<FoodItem> foodItems, GoalType goalType) {
        FoodItem worstItem = null;
        double worstValue = Double.MAX_VALUE;
        
        for (FoodItem item : foodItems) {
            double nutrientValue = getNutrientValue(item, goalType);
            if (nutrientValue < worstValue) {
                worstValue = nutrientValue;
                worstItem = item;
            }
        }
        
        return worstItem;
    }
    
    /**
     * Find a better replacement food item
     */
    private FoodItem findBetterReplacement(FoodItem originalItem, Goal userGoal) {
        GoalType goalType = userGoal.getType();
        double originalValue = getNutrientValue(originalItem, goalType);
        
        // Get all available food items
        Map<Long, String> allFoodNames = dbManager.getFoodItems();
        List<FoodItem> candidates = new ArrayList<>();
        
        // Sample a subset of food items for performance (take first 100)
        int count = 0;
        for (Map.Entry<Long, String> entry : allFoodNames.entrySet()) {
            if (count >= 100) break; // Limit for performance
            
            try {
                FoodItem candidate = dbManager.loadFoodItem(entry.getKey());
                if (candidate != null && !candidate.getDescription().equals(originalItem.getDescription())) {
                    double candidateValue = getNutrientValue(candidate, goalType);
                    
                    // Check if this is a better choice based on goal direction
                    if (userGoal.isIncrease() && candidateValue > originalValue) {
                        candidates.add(candidate);
                    } else if (!userGoal.isIncrease() && candidateValue < originalValue) {
                        candidates.add(candidate);
                    }
                }
                count++;
            } catch (Exception e) {
                // Skip items that can't be loaded
                continue;
            }
        }
        
        // If no candidates found, return null
        if (candidates.isEmpty()) {
            return null;
        }
        
        // Sort candidates by nutrient value (best first for increase, worst first for decrease)
        candidates.sort((a, b) -> {
            double valueA = getNutrientValue(a, goalType);
            double valueB = getNutrientValue(b, goalType);
            return userGoal.isIncrease() ? 
                Double.compare(valueB, valueA) : // Higher values first for increase
                Double.compare(valueA, valueB);  // Lower values first for decrease
        });
        
        // Return the best candidate (or randomly select from top 5 for variety)
        int topCount = Math.min(5, candidates.size());
        int randomIndex = new Random().nextInt(topCount);
        return candidates.get(randomIndex);
    }
    
    /**
     * Get the nutrient value for a specific goal type
     */
    private double getNutrientValue(FoodItem item, GoalType goalType) {
        switch (goalType) {
            case CALORIES:
                return item.calculateCaloriesFromMacros();
            case PROTEIN:
                return item.getNutrientValue("PROTEIN");
            case FIBRE:
                return item.getNutrientValue("FIBRE, TOTAL DIETARY");
            default:
                return 0.0;
        }
    }
    
    /**
     * Data class to hold swap suggestion information
     */
    public static class FoodSwapSuggestion {
        private final FoodItem originalItem;
        private final FoodItem replacementItem;
        private final Goal userGoal;
        
        public FoodSwapSuggestion(FoodItem originalItem, FoodItem replacementItem, Goal userGoal) {
            this.originalItem = originalItem;
            this.replacementItem = replacementItem;
            this.userGoal = userGoal;
        }
        
        public FoodItem getOriginalItem() { return originalItem; }
        public FoodItem getReplacementItem() { return replacementItem; }
        public Goal getUserGoal() { return userGoal; }
        
        public String getImprovementDescription() {
            GoalType goalType = userGoal.getType();
            String direction = userGoal.isIncrease() ? "increase" : "decrease";
            String unit = getUnitForGoalType(goalType);
            
            double originalValue = getNutrientValue(originalItem, goalType);
            double replacementValue = getNutrientValue(replacementItem, goalType);
            double difference = Math.abs(replacementValue - originalValue);
            
            return String.format("This swap will %s your %s intake by %.1f %s", 
                direction, goalType.getDisplayName().toLowerCase(), difference, unit);
        }
        
        private double getNutrientValue(FoodItem item, GoalType goalType) {
            switch (goalType) {
                case CALORIES:
                    return item.calculateCaloriesFromMacros();
                case PROTEIN:
                    return item.getNutrientValue("PROTEIN");
                case FIBRE:
                    return item.getNutrientValue("FIBRE, TOTAL DIETARY");
                default:
                    return 0.0;
            }
        }
        
        private String getUnitForGoalType(GoalType goalType) {
            switch (goalType) {
                case CALORIES:
                    return "kcal";
                case PROTEIN:
                case FIBRE:
                    return "g";
                default:
                    return "";
            }
        }
    }
} 