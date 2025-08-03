package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;

// Builder for constructing Meal objects step by step
public class MealBuilder {
    private Meal mealBeingBuilt;
    private boolean mealTypeSet;
    List<String> buildErrors;

    MealFactory mealFactory;
    
    // Sets the meal type and initializes the meal
    // helped by AI
    public MealBuilder setMealType(MealType type) {
        mealFactory = new MealFactory();
        LocalDate date = LocalDate.now();
        long mealID = Instant.now().getEpochSecond();
        mealBeingBuilt = mealFactory.createMeal(type, date, mealID);
        mealTypeSet = true;
        return this;
    }

    public MealBuilder setId(Long id) {
        this.mealBeingBuilt.id = id;
        return this;
    }

    // Return a list of food items
    public List<FoodItem> getFoodItems() {
        return this.mealBeingBuilt.foodItems;
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

    // Clear food items from list
    public MealBuilder clearFoodItems() {
        mealBeingBuilt.foodItems.clear();
        return this;
    }

    // Add a new list of food items for the meal
    public MealBuilder setFoodItems(List<FoodItem> foodItems) {
        this.mealBeingBuilt.foodItems = new ArrayList<>(foodItems);
        return this;
    }

    // Add notes for the meal
    public MealBuilder addNotes(String notes) {
        this.mealBeingBuilt.notes = notes;
        return this;
    }

    /**
     * Build the Meal object while checking whether the meal is set
     * ond at least one food item is selected.
     * @return
     */
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
        return mealBeingBuilt;
    }
}
