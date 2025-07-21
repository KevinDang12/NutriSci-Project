package com.nutrisci.database;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.nutrisci.meal.FoodItem;
import com.nutrisci.meal.Meal;
import com.nutrisci.meal.MealType;

public class DatabaseLogger {

    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();

        // System.out.println(db.canAddMealType(0, MealType.DINNER, LocalDate.now().minusDays(1)));
        // System.out.println(db.getMealCountForType(0, MealType.SNACK, LocalDate.now()));

        // db.deleteMeal(1753052033L);

        // System.out.println(db.getMealCountForType(0, MealType.SNACK, LocalDate.now()));
        List<Meal> meals = db.getMealsForUser(0, LocalDate.now().minusDays(1), LocalDate.now().minusDays(1));
        for (Meal meal : meals) {
            System.out.println(meal.getId());
            System.out.println(meal.getFoodItems());
        }

        // HashMap<Long, String> foodNames = db.getFoodItemsByGroup("");

        // for (Long key : foodNames.keySet()) {
        //     System.out.println(foodNames.get(key));
        // }

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