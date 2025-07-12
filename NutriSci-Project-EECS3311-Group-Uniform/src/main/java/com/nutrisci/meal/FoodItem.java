package com.nutrisci.meal;

import java.util.Map;

// Represents a food item with nutrients, vitamins, minerals, and serving info
public class FoodItem {
    long id;
    String description;
    Map<String, Double> nutrients;
    String foodGroup;

    // Constructor for FoodItem
    public FoodItem(String description, Map<String, Double> nutrients, String foodGroup) {
        this.description = description;
        this.nutrients = nutrients;
        this.foodGroup = foodGroup;
    }

    // Returns a map of macronutrients with their values
    // private Map<String, Double> getMacronutrients() {
    //     Map<String, Double> macros = new HashMap<>();
    //     macros.put("calories", 0.0);
    //     macros.put("protein", 0.0);
    //     macros.put("carbs", 0.0);
    //     macros.put("fat", 0.0);
    //     macros.put("fiber", 0.0);
    //     return macros;
    // }

    // Returns the value of a nutrient (macro, vitamin, or mineral)
    public double getNutrientValue(String nutrientName) {        
        if (nutrients.containsKey(nutrientName)) {
            return nutrients.get(nutrientName);
        }
        // Return -1 if nutrient not found
        return -1;
    }

    // Returns a new FoodItem with the same properties but with the quantity adjusted
    public FoodItem adjustForQuantity(double quantity) {
        FoodItem newFoodItem = new FoodItem(this.description, this.nutrients, this.foodGroup);
        return newFoodItem;
    }

    // Checks if the food item is in the same food group as another food item
    public boolean isSameFoodGroup(FoodItem other) {
        if (this.foodGroup == other.foodGroup) {
            return true;
        }
        return false;
    }

    // Calculates the calories from the macronutrients
    public double calculateCaloriesFromMacros() {
        double protein = getNutrientValue("PROTEIN");
        double carbs = getNutrientValue("CARBOHYDRATE, TOTAL (BY DIFFERENCE)");
        double fat = getNutrientValue("FAT (TOTAL LIPIDS)");
        return Math.round(((protein * 4) + (carbs * 4) + (fat * 9)) * 100.0) / 100.0;
    }

    // Returns the name, description, and serving size of the food item
    public String getDisplayName() {
        return this.description + "\n" + this.foodGroup + "\n";
    }

    /**
     * Calculates the nutrition density of a food item
     * @param nutrientName the nutrient to calculate the density for
     * @return the nutrition density of the food item
     */
    public double getNutritionDensity(String nutrientName) {
        double nutrientValue = getNutrientValue(nutrientName);
        double calories = getNutrientValue("calories");
        return nutrientValue / calories * 100;
    }
}
