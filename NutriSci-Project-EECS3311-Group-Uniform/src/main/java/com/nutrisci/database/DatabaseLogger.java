package com.nutrisci.database;

import java.util.HashMap;
import java.util.List;

import com.nutrisci.meal.FoodItem;

public class FirestoreExample {

    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();

        HashMap<Long, String> foodNames = db.getFoodItemsByGroup();

        for (Long key : foodNames.keySet()) {
            System.out.println(foodNames.get(key));
        }

        // List<FoodItem> res1 = db.searchFoodItems("Cheese");

        // for (FoodItem item : res1) {
        //     System.out.println(item.getDisplayName());
        //     System.out.println("Proteins: " + item.getNutrientValue("PROTEIN"));
        //     System.out.println("Carbs: " + item.getNutrientValue("CARBOHYDRATE, TOTAL (BY DIFFERENCE)"));
        //     System.out.println("Fat: " + item.getNutrientValue("FAT (TOTAL LIPIDS)"));
        //     System.out.println("Calories: " + item.calculateCaloriesFromMacros());
        //     System.out.println();
        // }

        // List<FoodItem> res2 = db.getFoodItemsByGroup("Snacks");

        // for (FoodItem item : res2) {
        //     System.out.println(item.getDisplayName());
        //     System.out.println("Proteins: " + item.getNutrientValue("PROTEIN"));
        //     System.out.println("Carbs: " + item.getNutrientValue("CARBOHYDRATE, TOTAL (BY DIFFERENCE)"));
        //     System.out.println("Fat: " + item.getNutrientValue("FAT (TOTAL LIPIDS)"));
        //     System.out.println("Calories: " + item.calculateCaloriesFromMacros());
        //     System.out.println();
        // }

        // FoodItem item = db.loadFoodItem(2l);
        // System.out.println(item.getDisplayName());
        // System.out.println("Proteins: " + item.getNutrientValue("PROTEIN"));
        // System.out.println("Carbs: " + item.getNutrientValue("CARBOHYDRATE, TOTAL (BY DIFFERENCE)"));
        // System.out.println("Fat: " + item.getNutrientValue("FAT (TOTAL LIPIDS)"));
        // System.out.println("Calories: " + item.calculateCaloriesFromMacros());
    }
} 