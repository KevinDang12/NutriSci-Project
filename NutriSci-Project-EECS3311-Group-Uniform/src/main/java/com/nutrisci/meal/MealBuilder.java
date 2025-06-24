package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.List;

// Builder for constructing Meal objects step by step
public class MealBuilder {
    Meal mealBeingBuilt;
    boolean mealTypeSet, dataSet;
    List<String> buildErrors;
    
    // Sets the meal type and initializes the meal
    // helped by AI
    public MealBuilder setMealType(MealType type) {
        LocalDate date = LocalDate.now();
        mealBeingBuilt = MealFactory.createMeal(type, date);
        mealTypeSet = true;
        return this;
    }

    // Adds a food item to the meal
    public MealBuilder addFoodItem(FoodItem item, double quantity) {
        mealBeingBuilt.addFoodItem(item, quantity);
        return this;
    }

    // Removes a food item from the meal
    public MealBuilder removeFoodItem(FoodItem item) {
        mealBeingBuilt.removeFoodItem(item);
        return this;
    }

    // Returns a preview of the meal being built
    // helped by AI
    public Meal buildPreview() {
        return MealFactory.duplicateMeal(mealBeingBuilt, LocalDate.now());
    }
}
