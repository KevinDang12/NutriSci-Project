package com.nutrisci.model;

// Enum for different types of nutritional goals
public enum GoalType {
    CALORIES("Calories"),
    PROTEIN("Protein"),
    FIBRE("Fibre");

    private final String displayName;

    GoalType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 