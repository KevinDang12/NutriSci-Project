package com.nutrisci.meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nutrisci.calculator.NutritionalData;

// Abstract class for a Meal (breakfast, lunch, dinner, snack)
public abstract class Meal {
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
    public void addFoodItem(FoodItem item) {
        // item.adjustForQuantity(quantity);
        foodItems.add(item);
    }

    // Removes a food item from the meal
    public void removeFoodItem(FoodItem item) {
        for (FoodItem foodItem : foodItems) {
            if (foodItem.description.equals(item.description)) {
                foodItems.remove(foodItem);
                break;
            }
        }
    }

    // Returns the list of food items in the meal
    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItem> newFoodItems) {
        this.foodItems = newFoodItems;
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
     * Set the ID of the meal
     * @param id The ID of the meal using UNIX time
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retrieve the ID of the meal
     * @return The Meal ID
     */
    public long getId() {
        return id;
    }

    /**
     * Get the meal creation date
     * @return The creation date of the meal
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Creates deep copy of meal with all food items
     * helped by AI
     * @return the cloned meal
     */
    public Meal copyMeal() {
        MealBuilder clonedMeal = new MealBuilder().setMealType(getMealType());

        for (FoodItem item : foodItems) {
            clonedMeal.addFoodItem(item);
        }

        clonedMeal.addNotes(notes);
        clonedMeal.mealBeingBuilt.setId(id);

        return clonedMeal.build();
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
