package com.nutrisci.calculator;

// Holds nutritional data for a meal or user
public class NutritionalData {
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;

    // Constructor for nutritional data
    public NutritionalData(){
        this.calories = 0;
        this.protein = 0;
        this.carbs = 0;
        this.fat = 0;
        this.fiber = 0;
    }

    // Adds another nutritional data object to this one
    public void add(NutritionalData other){
        if (other == null) 
            return;

        this.calories += other.calories;
        this.protein += other.protein;
        this.carbs += other.carbs;
        this.fat += other.fat;
        this.fiber += other.fiber;
    }

    // Getter for calories
    public double getCalories() {
        return this.calories;
    }

    // Setter for calories
    public void setCalories(double calories) {
        this.calories = calories;
    }

    // Getter for protein
    public double getProtein() {
        return this.protein;
    }

    // Setter for protein
    public void setProtein(double protein) {
        this.protein = protein;
    }

    // Getter for carbs
    public double getCarbs() {
        return this.carbs;
    }

    // Setter for carbs
    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    // Getter for fat
    public double getFat() {
        return this.fat;
    }

    // Setter for fat
    public void setFat(double fat) {
        this.fat = fat;
    }

    // Getter for fiber
    public double getFiber() {
        return this.fiber;
    }

    // Setter for fiber
    public void setFiber(double fiber) {
        this.fiber = fiber;
    }
}
