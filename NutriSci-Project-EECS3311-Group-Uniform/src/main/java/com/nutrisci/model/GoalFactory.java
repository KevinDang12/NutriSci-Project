package com.nutrisci.model;

import java.util.ArrayList;
import java.util.List;

// Factory for creating Goal objects
public class GoalFactory {
    // Creates a Goal object based on type and value
    public static Goal createGoal(GoalType type, int value) {
        if (!validateGoalValue(type, value)) {
            return null;
        }
        switch (type) {
            case INCREASE_PROTEIN:
                return new IncreaseProteinGoal(value);
            case INCREASE_CALORIES: // implement later
            case DECREASE_CALORIES: // implement later
                return null;
            default:
                return null;
        }
    }
    // Returns a list of available goal options for the UI
    public static List<GoalOption> getAvailableGoals() {
        List<GoalOption> options = new ArrayList<>();
        options.add(new GoalOption(GoalType.INCREASE_PROTEIN, 3, "Increase protein by 3g"));
        options.add(new GoalOption(GoalType.INCREASE_PROTEIN, 5, "Increase protein by 5g"));
        options.add(new GoalOption(GoalType.INCREASE_PROTEIN, 10, "Increase protein by 10g"));
        return options;
    }
    // Validates that the goal value is acceptable for the type
    public static boolean validateGoalValue(GoalType type, int value) {
        switch (type) {
            case INCREASE_PROTEIN:
                return value == 3 || value == 5 || value == 10;
            case INCREASE_CALORIES:
            case DECREASE_CALORIES:
                return false;
            default:
                return false;
        }
    }
    // Helper class to represent goal options in the UI
    public static class GoalOption {
        private final GoalType type; // Type of goal
        private final int value; // Value for the goal
        private final String displayText; // Text for UI display
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