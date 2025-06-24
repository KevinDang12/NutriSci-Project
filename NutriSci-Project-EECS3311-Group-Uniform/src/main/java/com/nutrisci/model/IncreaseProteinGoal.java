package com.nutrisci.model;

import com.nutrisci.calculator.NutritionalData;

// Goal for increasing daily protein intake
public class IncreaseProteinGoal extends Goal {
    private int targetIncreaseGrams; // Amount to increase protein by
    private double baselineProtein; // Default baseline for Deliverable 1
    
    // Creates a protein goal with a target increase
    // helped by AI
    public IncreaseProteinGoal(int targetIncreaseGrams) {
        super(GoalType.INCREASE_PROTEIN, 
              "Increase protein intake by " + targetIncreaseGrams + " grams per day");
        this.targetIncreaseGrams = targetIncreaseGrams;
        this.baselineProtein = 50.0; // Default baseline for Deliverable 1
    }
    
    // Creates a protein goal with a custom baseline
    // helped by AI
    public IncreaseProteinGoal(int targetIncreaseGrams, double baselineProtein) {
        super(GoalType.INCREASE_PROTEIN, 
              "Increase protein intake by " + targetIncreaseGrams + " grams per day");
        this.targetIncreaseGrams = targetIncreaseGrams;
        this.baselineProtein = baselineProtein;
    }
    
    // Checks if the protein goal is achieved
    @Override
    public boolean isAchieved(NutritionalData currentIntake) {
        if (currentIntake == null) {
            return false;
        }
        double currentProtein = currentIntake.getProtein();
        double targetProtein = baselineProtein + targetIncreaseGrams;
        return currentProtein >= targetProtein;
    }
    
    // Returns a progress message for the protein goal
    // helped by AI
    @Override
    public String getProgressMessage(NutritionalData currentIntake) {
        if (currentIntake == null) {
            return "No nutritional data available";
        }
        double currentProtein = currentIntake.getProtein();
        double targetProtein = baselineProtein + targetIncreaseGrams;
        double remaining = targetProtein - currentProtein;
        if (remaining <= 0) {
            return String.format("Goal achieved! You've exceeded your protein target by %.1f grams.", 
                               Math.abs(remaining));
        } else {
            return String.format("You need %.1f more grams of protein to reach your daily goal.", remaining);
        }
    }
    
    // Returns the goal type as INCREASE_PROTEIN
    public GoalType getMealType() {
        return GoalType.INCREASE_PROTEIN;
    }
    
    // Gets the target increase in grams
    public int getTargetIncreaseGrams() {
        return targetIncreaseGrams;
    }
    // Sets the target increase in grams
    public void setTargetIncreaseGrams(int targetIncreaseGrams) {
        this.targetIncreaseGrams = targetIncreaseGrams;
        this.description = "Increase protein intake by " + targetIncreaseGrams + " grams per day";
    }
    // Gets the baseline protein
    public double getBaselineProtein() {
        return baselineProtein;
    }
    // Sets the baseline protein
    public void setBaselineProtein(double baselineProtein) {
        this.baselineProtein = baselineProtein;
    }
    // Gets the total target protein (baseline + increase)
    public double getTotalTargetProtein() {
        return baselineProtein + targetIncreaseGrams;
    }
    // Returns a string representation of the goal
    @Override
    public String toString() {
        return "IncreaseProteinGoal{" +
                "targetIncrease=" + targetIncreaseGrams + "g" +
                ", baseline=" + baselineProtein + "g" +
                ", totalTarget=" + getTotalTargetProtein() + "g" +
                '}';
    }
} 