package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.List;

import com.nutrisci.calculator.NutritionalCalculator;

// Builder for constructing Meal objects step by step
public class MealBuilder {
    Meal mealBeingBuilt;
    boolean mealTypeSet, dataSet;
    List<String> buildErrors;
    
    // Sets the meal type and initializes the meal
    // helped by AI
    public MealBuilder setMealType(MealType type) {
        LocalDate date = LocalDate.now();
        long mealID = java.time.Instant.now().getEpochSecond();
        mealBeingBuilt = MealFactory.createMeal(type, date, mealID);
        mealTypeSet = true;
        System.out.println(mealBeingBuilt.foodItems);
        return this;
    }

    // Adds a food item to the meal
    public MealBuilder addFoodItem(FoodItem item) {
        mealBeingBuilt.addFoodItem(item);
        return this;
    }

    // Removes a food item from the meal
    public MealBuilder removeFoodItem(FoodItem item) {
        mealBeingBuilt.removeFoodItem(item);
        return this;
    }

    public MealBuilder clearFoodItems() {
        mealBeingBuilt.foodItems.clear();
        return this;
    }

    // public MealBuilder calculateNutrition() {
    //     NutritionalCalculator.calculateMealNutrition(mealBeingBuilt.getFoodItems());
    //     return this;
    // }

    public MealBuilder addNotes(String notes) {
        this.mealBeingBuilt.notes = notes;
        return this;
    }

    public Meal build() {
        if (!mealTypeSet) {
            System.out.println("Meal Type not set");
            return null;
        }

        if (mealBeingBuilt.foodItems.isEmpty()) {
            System.out.println("Meal must contain at least one food item.");
            return null;
        }

        return mealBeingBuilt;
    }

    // Returns a preview of the meal being built
    // helped by AI
    public Meal buildPreview() {
        return MealFactory.duplicateMeal(mealBeingBuilt, LocalDate.now());
    }
}
