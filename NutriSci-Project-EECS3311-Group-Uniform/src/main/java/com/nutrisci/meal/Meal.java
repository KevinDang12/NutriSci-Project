package com.nutrisci.meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nutrisci.calculator.NutritionalData;

// Abstract class for a Meal (breakfast, lunch, dinner, snack)
public abstract class Meal implements Cloneable {
    long id;
    LocalDate date;
    List<FoodItem> foodItems = new ArrayList<>();
    NutritionalData nutritionalData;
    String notes;
    LocalDateTime createdAt, updatedAt;
    
    // Checks if this meal can be added to a given date (business rules)
    public abstract boolean canAddToDate(LocalDate date);

    // Returns the meal type (breakfast, lunch, etc.)
    public abstract MealType getMealType();

    // Returns the max allowed of this meal type per day
    public abstract int getMaxAllowedPerDay();

    // Adds a food item to the meal
    public void addFoodItem(FoodItem item, double quantity) {
        item.adjustForQuantity(quantity);
        foodItems.add(item);
    }

    // Removes a food item from the meal
    public void removeFoodItem(FoodItem item) {
        for (FoodItem foodItem : foodItems) {
            if (foodItem.equals(item)) {
                foodItems.remove(foodItem);
                break;
            }
        }
    }

    // Returns the list of food items in the meal
    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    // Updates the quantity of a food item in the meal
    // helped by AI
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
     * Calculates the total nutrition of the meal
     * @return the total nutrition of the meal
     */
    public NutritionalData calculateTotalNutrition() {
        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        double totalFiber = 0;

        for (FoodItem foodItem : foodItems) {
            totalCalories += foodItem.getNutrientValue("calories");
            totalProtein += foodItem.getNutrientValue("protein");
            totalCarbs += foodItem.getNutrientValue("carbs");
            totalFat += foodItem.getNutrientValue("fat");
            totalFiber += foodItem.getNutrientValue("fiber");
        }
        return new NutritionalData(totalCalories, totalProtein, totalCarbs, totalFat, totalFiber);
    }

    // Returns the total calories of the meal
    public double getTotalCalories() {
        double totalCalories = 0;
        for (FoodItem foodItem : foodItems) {
            totalCalories += foodItem.getNutrientValue("calories");
        }
        return totalCalories;
    }

    // Returns the number of food items in the meal
    public int getFoodItemCount() {
        return foodItems.size();
    }

    /**
     * Creates deep copy of meal with all food items
     * helped by AI
     * @return the cloned meal
     * @throws CloneNotSupportedException if the meal cannot be cloned
     */
    public Object clone() throws CloneNotSupportedException {
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
     * @param obj the object to compare to
     * @return true if the meals are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Meal meal = (Meal) obj;
        return foodItems.equals(meal.foodItems) &&
               notes.equals(meal.notes) &&
               createdAt.equals(meal.createdAt) &&
               updatedAt.equals(meal.updatedAt) &&
               id == meal.id &&
               date.equals(meal.date);
    }
}
