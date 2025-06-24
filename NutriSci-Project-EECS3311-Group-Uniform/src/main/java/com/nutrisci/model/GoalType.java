package com.nutrisci.model;

// Enum for different types of nutritional goals
public enum GoalType {
    INCREASE_PROTEIN("Increase Protein Intake"), // Increase protein
    INCREASE_CALORIES("Increase Calorie Intake"), // Increase calories
    DECREASE_CALORIES("Decrease Calorie Intake"); // Decrease calories
    
    private final String displayName; // Display name for UI
    
    // Sets the display name for the goal type
    public GoalType(String displayName) {
        this.displayName = displayName;
    }
    
    // Returns the display name for this goal type
    public String getDisplayName() {
        return displayName;
    }
    
    // Returns the display name as the string representation
    @Override
    public String toString() {
        return displayName;
    }
} 