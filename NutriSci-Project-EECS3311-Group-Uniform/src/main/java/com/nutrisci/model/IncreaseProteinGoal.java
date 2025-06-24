package com.nutrisci.model;

import com.nutrisci.calculator.NutritionalData;

/**
 * Concrete implementation of a protein increase goal.
 * Allows users to set targets for increasing their daily protein intake.
 * 
 * @author NutriSci Team
 * @version 1.0
 */
public class IncreaseProteinGoal extends Goal {
    private int targetIncreaseGrams;
    private double baselineProtein; // Default baseline for Deliverable 1
    
    /**
     * Constructor for IncreaseProteinGoal with target increase amount.
     * 
     * @param targetIncreaseGrams The target increase in grams (3, 5, or 10)
     */
    public IncreaseProteinGoal(int targetIncreaseGrams) {
        super(GoalType.INCREASE_PROTEIN, 
              "Increase protein intake by " + targetIncreaseGrams + " grams per day");
        this.targetIncreaseGrams = targetIncreaseGrams;
        this.baselineProtein = 50.0; // Default baseline for Deliverable 1
    }
    
    /**
     * Constructor with custom baseline protein amount.
     * 
     * @param targetIncreaseGrams The target increase in grams
     * @param baselineProtein The baseline protein amount in grams
     */
    public IncreaseProteinGoal(int targetIncreaseGrams, double baselineProtein) {
        super(GoalType.INCREASE_PROTEIN, 
              "Increase protein intake by " + targetIncreaseGrams + " grams per day");
        this.targetIncreaseGrams = targetIncreaseGrams;
        this.baselineProtein = baselineProtein;
    }
    
    /**
     * Determines if the protein increase goal has been achieved.
     * Goal is achieved when current protein intake meets or exceeds the target.
     * 
     * @param currentIntake The current daily nutritional intake
     * @return true if protein target is met, false otherwise
     */
    @Override
    public boolean isAchieved(NutritionalData currentIntake) {
        if (currentIntake == null) {
            return false;
        }
        
        double currentProtein = currentIntake.getProtein();
        double targetProtein = baselineProtein + targetIncreaseGrams;
        
        return currentProtein >= targetProtein;
    }
    
    /**
     * Generates a user-friendly progress message for the protein goal.
     * 
     * @param currentIntake The current daily nutritional intake
     * @return Human-readable progress message
     */
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
    
    /**
     * Returns the goal type as INCREASE_PROTEIN.
     * 
     * @return GoalType.INCREASE_PROTEIN
     */
    public GoalType getMealType() {
        return GoalType.INCREASE_PROTEIN;
    }
    
    // Getters and Setters
    public int getTargetIncreaseGrams() {
        return targetIncreaseGrams;
    }
    
    public void setTargetIncreaseGrams(int targetIncreaseGrams) {
        this.targetIncreaseGrams = targetIncreaseGrams;
        // Update description when target changes
        this.description = "Increase protein intake by " + targetIncreaseGrams + " grams per day";
    }
    
    public double getBaselineProtein() {
        return baselineProtein;
    }
    
    public void setBaselineProtein(double baselineProtein) {
        this.baselineProtein = baselineProtein;
    }
    
    /**
     * Gets the total target protein amount (baseline + increase).
     * 
     * @return Total target protein in grams
     */
    public double getTotalTargetProtein() {
        return baselineProtein + targetIncreaseGrams;
    }
    
    @Override
    public String toString() {
        return "IncreaseProteinGoal{" +
                "targetIncrease=" + targetIncreaseGrams + "g" +
                ", baseline=" + baselineProtein + "g" +
                ", totalTarget=" + getTotalTargetProtein() + "g" +
                '}';
    }
} 