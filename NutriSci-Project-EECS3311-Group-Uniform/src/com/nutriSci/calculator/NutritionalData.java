package com.nutriSci.calculator;

public class NutritionalData {
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;

    public NutritionalData(double calories, double protein, double carbs, double fat, double fiber){
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
    }

    // adds another nutritional data
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

    //getter methods
    public double getCalories() {
        return calories;
    }
    public double getProtein() {
        return protein;
    }
    public double getCarbs() {
        return carbs;
    }
    public double getFat() {
        return fat;
    }
    public double getFiber() {
        return fiber;
    }
}
