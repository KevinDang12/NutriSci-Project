package com.nutrisci.model;

import com.nutrisci.calculator.NutritionalData;

/**
 * Abstract base class for all nutritional goals in the NutriSci application.
 * Provides common functionality and enforces implementation of goal-specific logic.
 * 
 * @author NutriSci Team
 * @version 1.0
 */
public abstract class Goal {
    protected GoalType goalType;
    protected String description;
    
    /**
     * Constructor for Goal with type and description
     * 
     * @param goalType The type of nutritional goal
     * @param description Human-readable description of the goal
     */
    public Goal(GoalType goalType, String description) {
        this.goalType = goalType;
        this.description = description;
    }
    
    /**
     * Abstract method to determine if the goal has been achieved based on current nutritional intake.
     * 
     * @param currentIntake The current daily nutritional intake
     * @return true if the goal is achieved, false otherwise
     */
    public abstract boolean isAchieved(NutritionalData currentIntake);
    
    /**
     * Abstract method to generate a user-friendly progress message.
     * 
     * @param currentIntake The current daily nutritional intake
     * @return Human-readable progress message
     */
    public abstract String getProgressMessage(NutritionalData currentIntake);
    
    /**
     * Returns the type of this goal.
     * 
     * @return GoalType enum value
     */
    public GoalType getGoalType() {
        return goalType;
    }
    
    /**
     * Returns a human-readable description of the goal.
     * 
     * @return Goal description string
     */
    public String getGoalDescription() {
        return description;
    }
    
    /**
     * Sets the goal description.
     * 
     * @param description New description for the goal
     */
    public void setGoalDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "Goal{" +
                "type=" + goalType +
                ", description='" + description + '\'' +
                '}';
    }
} 