package com.nutrisci.meal;

import java.util.Map;
import java.util.HashMap;

// Represents a food item with nutrients, vitamins, minerals, and serving info
public class FoodItem {
    long id;
    String name;
    String description;
    Map<String, Double> macroNutrients = getMacronutrients();
    Map<String, Double> vitamins;
    Map<String, Double> minerals;
    String foodGroup;
    double servingSize;
    String unit;

    // Constructor for FoodItem
    public FoodItem(String name, String description, Map<String, Double> macroNutrients, Map<String, Double> vitamins, Map<String, Double> minerals, String foodGroup, double servingSize, String unit) {
        this.name = name;
        this.description = description;
        this.macroNutrients = macroNutrients;
        this.vitamins = vitamins;
        this.minerals = minerals;
        this.foodGroup = foodGroup;
        this.servingSize = servingSize;
        this.unit = unit;
    }
    // Returns a map of macronutrients with their values
    private Map<String, Double> getMacronutrients() {
        Map<String, Double> macros = new HashMap<>();
        macros.put("calories", 0.0);
        macros.put("protein", 0.0);
        macros.put("carbs", 0.0);
        macros.put("fat", 0.0);
        macros.put("fiber", 0.0);
        return macros;
    }

    // Returns the value of a nutrient (macro, vitamin, or mineral)
    public double getNutrientValue(String nutrientName) {        
        // Check macros first
        if (macroNutrients.containsKey(nutrientName)) {
            return macroNutrients.get(nutrientName);
        }
        // Then check vitamins
        if (vitamins != null && vitamins.containsKey(nutrientName)) {
            return vitamins.get(nutrientName);
        }
        // Finally check minerals
        if (minerals != null && minerals.containsKey(nutrientName)) {
            return minerals.get(nutrientName);
        }
        // Return 0 if nutrient not found
        return 0;
    }

    // Returns a new FoodItem with the same properties but with the quantity adjusted
    public FoodItem adjustForQuantity(double quantity) {
        FoodItem newFoodItem = new FoodItem(this.name, this.description, this.macroNutrients, this.vitamins, this.minerals, this.foodGroup, this.servingSize * quantity, this.unit);
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
        double protein = getNutrientValue("protein");
        double carbs = getNutrientValue("carbs");
        double fat = getNutrientValue("fat");
        return (protein * 4) + (carbs * 4) + (fat * 9);
    }

    // Returns the name, description, and serving size of the food item
    public String getDisplayName() {
        return this.name + "\n" + this.description + "\n" + this.servingSize + " " + this.unit;
    }
}
