package com.nutrisci.model;

import com.nutrisci.calculator.NutritionalData;

// Abstract base class for all nutritional goals
public abstract class Goal {
    protected GoalType goalType; // Type of the goal
    protected String description; // Description of the goal
    
    // Sets the goal type and description
    public Goal(GoalType goalType, String description) {
        this.goalType = goalType;
        this.description = description;
    }
    
    // Checks if the goal is achieved based on current intake
    public abstract boolean isAchieved(NutritionalData currentIntake);
    
    // Returns a progress message for the goal
    public abstract String getProgressMessage(NutritionalData currentIntake);
    
    // Returns the type of this goal
    public GoalType getGoalType() {
        return goalType;
    }
    
    // Returns a human-readable description of the goal
    public String getGoalDescription() {
        return description;
    }
    
    // Sets the goal description
    public void setGoalDescription(String description) {
        this.description = description;
    }
    
    // Returns a string representation of the goal
    @Override
    public String toString() {
        return "Goal{" +
                "type=" + goalType +
                ", description='" + description + '\'' +
                '}';
    }
} 