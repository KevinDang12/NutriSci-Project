package com.nutrisci.model;

/**
 * Enum representing different types of nutritional goals available in NutriSci.
 * 
 * @author NutriSci Team
 * @version 1.0
 */
public enum GoalType {
    INCREASE_PROTEIN("Increase Protein Intake"),
    INCREASE_CALORIES("Increase Calorie Intake"),
    DECREASE_CALORIES("Decrease Calorie Intake");
    
    private final String displayName;
    
    /**
     * Constructor for GoalType enum
     * 
     * @param displayName Human-readable name for the goal type
     */
    GoalType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Returns the human-readable display name for this goal type.
     * 
     * @return Display name string
     */
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
} 