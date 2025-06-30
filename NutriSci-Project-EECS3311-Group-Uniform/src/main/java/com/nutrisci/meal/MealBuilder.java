package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.nutrisci.calculator.NutritionalData;
import com.nutrisci.util.UserSessionManager;

// Builder for constructing Meal objects step by step
public class MealBuilder {
    Meal mealBeingBuilt;
    boolean mealTypeSet, dateSet;
    List<String> buildErrors;
    UserSessionManager userSessionManager;
    
    // Sets the meal type and initializes the meal
    // helped by AI
    public MealBuilder setMealType(MealType type) {
        LocalDate date = LocalDate.now();
        mealBeingBuilt = MealFactory.createMeal(type, date);
        mealTypeSet = true;
        return this;
    }

    public MealBuilder setDate(LocalDate date) {
        mealBeingBuilt.date = date;
        return this;
    }

    // Adds a food item to the meal
    public MealBuilder addFoodItem(String foodName, double quantity) {
        // Make call to backend to get food item
        // mealBeingBuilt.addFoodItem(foodItem, quantity);
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

    public MealBuilder updateFoodQuantity(FoodItem item, double quantity) {
        for (FoodItem foodItem : mealBeingBuilt.foodItems) {
            if (foodItem.name.equals(item.name)) {
                foodItem.adjustForQuantity(quantity);
                break;
            }
        }
        return this;
    }

    public MealBuilder calculateNutrition() {
        // mealBeingBuilt.calculateTotalNutrition();
        // Calls NutritionalCalculator.calculateMealNutrition(foodItems) and assigns to mealBeingBuilt.nutritionalData
        return this;
    }

    public MealBuilder addNotes(String notes) {
        mealBeingBuilt.notes = notes;
        return this;
    }
    
    public Meal build() {
        if (!dateSet) {
            mealBeingBuilt.date = LocalDate.now();
        }

        // Check if meal type is set, has at least one food item, and total nutrition is reasonable ( not negative and not too high )
        // if (mealBeingBuilt.getFoodItemCount() >= 1 && mealBeingBuilt.nutritionalData == null) {
        //     mealBeingBuilt.calculateTotalNutrition();
        // }

        // Check for any build errors
        
        return mealBeingBuilt;
    }

    // Returns a preview of the meal being built
    // helped by AI
    public Meal buildPreview() {
        try {
            return MealFactory.duplicateMeal(mealBeingBuilt, LocalDate.now());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public MealBuilder reset() {
        mealBeingBuilt = null;
        mealTypeSet = false;
        dateSet = false;
        buildErrors = new ArrayList<>();
        return this;
    }

    public List<String> getErrors() {
        return buildErrors;
    }

    public NutritionalData getCurrentNutrition() {
        return mealBeingBuilt.nutritionalData;
    }
}
