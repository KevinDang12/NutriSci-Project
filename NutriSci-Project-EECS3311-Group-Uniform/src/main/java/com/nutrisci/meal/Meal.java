package com.nutrisci.meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Meal {
    long id;
    LocalDate date;
    List<FoodItem> foodItems = new ArrayList<>();
    String notes;
    LocalDateTime createdAt, updatedAt;
    
    public abstract boolean canAddToDate(LocalDate date);

    public abstract MealType getMealType();

    public abstract int getMaxAllowedPerDay();

    /**
     * Add a food item to the foodItems list
     * @param item The food item to add
     * @param quantity The quantity of the food item to add
     */
    public void addFoodItem(FoodItem item, double quantity) {
        item.adjustForQuantity(quantity);
        foodItems.add(item);
    }

    /**
     * Remove a food item from the foodItems list
     * @param item The food item to remove
     */
    public void removeFoodItem(FoodItem item) {
        for (FoodItem foodItem : foodItems) {
            if (foodItem.equals(item)) {
                foodItems.remove(foodItem);
                break;
            }
        }
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    /**
     * Update the quantity of a food item in the foodItems list
     * @param item The food item to update
     * @param newQuantity The new quantity of the food item
     * @return True if the quantity is > 0, false otherwise
     */
    public boolean updateFoodItemQuantity(FoodItem item, double newQuantity) {
        if (newQuantity <= 0) {
            return false;
        }
        // Check if the food item is in the meal
        for (FoodItem foodItem : foodItems) {
            if (foodItem.equals(item)) {
                foodItem.adjustForQuantity(newQuantity);
                return true;
            }
        }
        return false;
    }

    /**
     * Get the total calories of the meal without the full nutrition calculation
     * @return The total calories of the meal
     */
    public double getTotalCalories() {
        double totalCalories = 0;
        for (FoodItem foodItem : foodItems) {
            totalCalories += foodItem.getNutrientValue("calories");
        }
        return totalCalories;
    }

    /**
     * Get the number of distinct food items in the meal
     * @return The number of food items in the meal
     */
    public int getFoodItemCount() {
        return foodItems.size();
    }

    @Override
    public Meal clone() throws CloneNotSupportedException {
        Meal clonedMeal = (Meal) super.clone();
        clonedMeal.foodItems = new ArrayList<>(foodItems);
        clonedMeal.notes = notes;
        clonedMeal.createdAt = createdAt;
        clonedMeal.updatedAt = updatedAt;
        clonedMeal.id = id;
        clonedMeal.date = date;
        return clonedMeal;
    }

    /**
     * Compares two meals for identical content (same foods, quantities, date)
     * @param obj The object to compare to
     * @return True if the meals are identical, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        // TODO: Implement
        return false;
    }
}
