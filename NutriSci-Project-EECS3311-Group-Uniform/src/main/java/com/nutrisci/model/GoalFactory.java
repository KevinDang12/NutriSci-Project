package com.nutrisci.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating Goal objects with validation.
 * Provides a centralized way to create different types of nutritional goals.
 * 
 * @author NutriSci Team
 * @version 1.0
 */
public class GoalFactory {
    
    /**
     * Creates a Goal object based on the specified type and value.
     * Currently supports INCREASE_PROTEIN goals for Deliverable 1.
     * 
     * @param type The type of goal to create
     * @param value The target value for the goal
     * @return A new Goal object, or null if creation fails
     */
    public static Goal createGoal(GoalType type, int value) {
        if (!validateGoalValue(type, value)) {
            return null;
        }
        
        switch (type) {
            case INCREASE_PROTEIN:
                return new IncreaseProteinGoal(value);
            case INCREASE_CALORIES:
            case DECREASE_CALORIES:
                // Not implemented for Deliverable 1
                return null;
            default:
                return null;
        }
    }
    
    /**
     * Returns a list of available goal options for UI dropdown population.
     * 
     * @return List of GoalOption objects
     */
    public static List<GoalOption> getAvailableGoals() {
        List<GoalOption> options = new ArrayList<>();
        
        // Add protein increase options
        options.add(new GoalOption(GoalType.INCREASE_PROTEIN, 3, "Increase protein by 3g"));
        options.add(new GoalOption(GoalType.INCREASE_PROTEIN, 5, "Increase protein by 5g"));
        options.add(new GoalOption(GoalType.INCREASE_PROTEIN, 10, "Increase protein by 10g"));
        
        return options;
    }
    
    /**
     * Validates that the goal value is acceptable for the given goal type.
     * 
     * @param type The type of goal
     * @param value The target value
     * @return true if the value is valid, false otherwise
     */
    public static boolean validateGoalValue(GoalType type, int value) {
        switch (type) {
            case INCREASE_PROTEIN:
                // Only allow 3, 5, or 10 grams for Deliverable 1
                return value == 3 || value == 5 || value == 10;
            case INCREASE_CALORIES:
            case DECREASE_CALORIES:
                // Not implemented for Deliverable 1
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Helper class to represent goal options in the UI.
     */
    public static class GoalOption {
        private final GoalType type;
        private final int value;
        private final String displayText;
        
        public GoalOption(GoalType type, int value, String displayText) {
            this.type = type;
            this.value = value;
            this.displayText = displayText;
        }
        
        public GoalType getType() {
            return type;
        }
        
        public int getValue() {
            return value;
        }
        
        public String getDisplayText() {
            return displayText;
        }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
} 