package com.nutrisci.calculator;

// Holds nutritional data for a meal or user
public class NutritionalData {
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;

    // Constructor for nutritional data
    public NutritionalData(double calories, double protein, double carbs, double fat, double fiber){
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
    }

    // Adds another nutritional data object to this one
    public NutritionalData add(NutritionalData other){
        if (other == null) 
            return this;

        return new NutritionalData(
            this.calories + other.calories, 
            this.protein + other.protein, 
            this.carbs + other.carbs, 
            this.fat + other.fat, 
            this.fiber + other.fiber
        );
    }

    // Getter for calories
    public double getCalories() {
        return calories;
    }
    // Getter for protein
    public double getProtein() {
        return protein;
    }
    // Getter for carbs
    public double getCarbs() {
        return carbs;
    }
    // Getter for fat
    public double getFat() {
        return fat;
    }
    // Getter for fiber
    public double getFiber() {
        return fiber;
    }
}
