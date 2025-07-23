package com.nutrisci.model;

import java.util.ArrayList;
import java.util.List;

// Factory for creating Goal objects
public class GoalFactory {
    // Creates a Goal object based on type and percentage
    public static Goal createGoal(GoalType type, int percent) {
        if (!validateGoalValue(percent)) {
            return null;
        }
        // For now, default to increase. In a real app, this would be configurable
        return new Goal(type, true, percent);
    }
    
    // Creates a Goal object with specified direction
    public static Goal createGoal(GoalType type, boolean increase, int percent) {
        if (!validateGoalValue(percent)) {
            return null;
        }
        return new Goal(type, increase, percent);
    }
    
    // Returns a list of available goal options for the UI
    public static List<GoalOption> getAvailableGoals() {
        List<GoalOption> options = new ArrayList<>();
        
        // Add options for each goal type with different percentages
        for (GoalType type : GoalType.values()) {
            options.add(new GoalOption(type, true, 5, "Increase " + type.getDisplayName() + " by 5%"));
            options.add(new GoalOption(type, true, 10, "Increase " + type.getDisplayName() + " by 10%"));
            options.add(new GoalOption(type, true, 15, "Increase " + type.getDisplayName() + " by 15%"));
            options.add(new GoalOption(type, false, 5, "Decrease " + type.getDisplayName() + " by 5%"));
            options.add(new GoalOption(type, false, 10, "Decrease " + type.getDisplayName() + " by 10%"));
            options.add(new GoalOption(type, false, 15, "Decrease " + type.getDisplayName() + " by 15%"));
        }
        
        return options;
    }
    
    // Validates that the goal percentage is acceptable
    public static boolean validateGoalValue(int percent) {
        return percent == 5 || percent == 10 || percent == 15;
    }
    
    // Helper class to represent goal options in the UI
    public static class GoalOption {
        private final GoalType type;
        private final boolean increase;
        private final int percent;
        private final String displayText;
        
        public GoalOption(GoalType type, boolean increase, int percent, String displayText) {
            this.type = type;
            this.increase = increase;
            this.percent = percent;
            this.displayText = displayText;
        }
        
        public GoalType getType() {
            return type;
        }
        
        public boolean isIncrease() {
            return increase;
        }
        
        public int getPercent() {
            return percent;
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