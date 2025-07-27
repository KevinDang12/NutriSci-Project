package com.nutrisci.meal;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.nutrisci.calculator.NutritionalCalculator;
import com.nutrisci.calculator.NutritionalData;
import com.nutrisci.database.DatabaseManager;
import com.nutrisci.model.Goal;
import com.nutrisci.model.GoalType;
import com.nutrisci.service.FoodSwapService;
import com.nutrisci.service.FoodSwapService.FoodSwapSuggestion;

public class MealLoggingTest {
    /**
     * Log a Snack meal to the database
     */
    @Test
    public void logMeal() {
        DatabaseManager db = DatabaseManager.getInstance();
        FoodItem item1 = db.loadFoodItem(2L);
        FoodItem item2 = db.loadFoodItem(5L);

        assertNotNull(item1);
        assertNotNull(item2);

        MealBuilder mealBuilder = new MealBuilder().setMealType(MealType.SNACK);

        mealBuilder.addFoodItem(item1);
        mealBuilder.addFoodItem(item2);

        // Add meal to the database
        assertTrue(db.saveMeal(mealBuilder.build(), 1));

        db.deleteMeal(mealBuilder.mealBeingBuilt.id);
    }

    /**
     * Log a meal for each meal category and check if it
     * exists in the database to ensure that Breakfast, Lunch, and Dinner
     * can be added only once
     */
    @Test
    public void canAddMealType() {
        DatabaseManager db = DatabaseManager.getInstance();

        FoodItem item1 = db.loadFoodItem(2L);
        assertNotNull(item1);

        // Add Snack
        MealBuilder mealBuilder = new MealBuilder().setMealType(MealType.SNACK);
        mealBuilder.addFoodItem(item1);

        assertTrue(db.saveMeal(mealBuilder.build(), 1));
        boolean canAddSnack = db.canAddMealType(1, MealType.SNACK, LocalDate.now());
        assertTrue(canAddSnack);
        db.deleteMeal(mealBuilder.mealBeingBuilt.id);

        // Add Breakfast
        mealBuilder = new MealBuilder().setMealType(MealType.BREAKFAST);
        mealBuilder.addFoodItem(item1);

        assertTrue(db.saveMeal(mealBuilder.build(), 1));
        boolean canAddBreakfast = db.canAddMealType(1, MealType.BREAKFAST, LocalDate.now());
        assertFalse(canAddBreakfast);
        db.deleteMeal(mealBuilder.mealBeingBuilt.id);

        // Add Lunch
        mealBuilder = new MealBuilder().setMealType(MealType.LUNCH);
        mealBuilder.addFoodItem(item1);

        assertTrue(db.saveMeal(mealBuilder.build(), 1));
        boolean canAddLunch = db.canAddMealType(1, MealType.LUNCH, LocalDate.now());
        assertFalse(canAddLunch);
        db.deleteMeal(mealBuilder.mealBeingBuilt.id);

        // Add Dinner
        mealBuilder = new MealBuilder().setMealType(MealType.DINNER);
        mealBuilder.addFoodItem(item1);

        assertTrue(db.saveMeal(mealBuilder.build(), 1));
        boolean canAddDinner = db.canAddMealType(1, MealType.DINNER, LocalDate.now());
        assertFalse(canAddDinner);
        db.deleteMeal(mealBuilder.mealBeingBuilt.id);
    }

    /**
     * Check whether you can import a meal from a previous date
     * and add it to the database
     */
    @Test
    public void importMeal() {
        DatabaseManager db = DatabaseManager.getInstance();

        Map<Long, String> meals = db.importMeals(1);

        Object[] mealIds = meals.keySet().toArray();

        int index = (int) (Math.random() * mealIds.length);

        List<Long> foodIds = db.importMeal((Long) mealIds[index]);

        MealBuilder mealBuilder = new MealBuilder().setMealType(MealType.SNACK);

        for (Long id : foodIds) {
            mealBuilder.addFoodItem(db.loadFoodItem(id));
        }

        assertTrue(db.saveMeal(mealBuilder.build(), 1));

        db.deleteMeal(mealBuilder.mealBeingBuilt.id);
    }

    /**
     * Perform calculations on two meals and their nutritional data
     * are different from each other
     */
    @Test
    public void compareMeal() {
        DatabaseManager db = DatabaseManager.getInstance();
        NutritionalCalculator nutritionalCalculator = new NutritionalCalculator();

        Map<Long, String> meals = db.importMeals(1);

        Object[] mealIds = meals.keySet().toArray();

        int index1 = (int) (Math.random() * mealIds.length);
        int index2 = 0;
        do {
            index2 = (int) (Math.random() * mealIds.length);
        } while (index1 != index2);

        List<Long> foodIds1 = db.importMeal((Long) mealIds[index1]);

        MealBuilder mealBuilder1 = new MealBuilder().setMealType(MealType.SNACK);

        for (Long id : foodIds1) {
            mealBuilder1.addFoodItem(db.loadFoodItem(id));
        }

        List<Long> foodIds2 = db.importMeal((Long) mealIds[index2]);

        MealBuilder mealBuilder2 = new MealBuilder().setMealType(MealType.SNACK);

        for (Long id : foodIds2) {
            mealBuilder2.addFoodItem(db.loadFoodItem(id));
        }

        NutritionalData data1 = nutritionalCalculator.calculateMealNutrition(mealBuilder1.mealBeingBuilt.foodItems);
        NutritionalData data2 = nutritionalCalculator.calculateMealNutrition(mealBuilder2.mealBeingBuilt.foodItems);

        assertNotEquals(data1, data2);
        assertNotEquals(data1.getCalories(), data2.getCalories());
        assertNotEquals(data1.getCarbs(), data2.getCarbs());
        assertNotEquals(data1.getFat(), data2.getFat());
        assertNotEquals(data1.getFiber(), data2.getFiber());
        assertNotEquals(data1.getProtein(), data2.getProtein());
    }

    /**
     * Perform a food swap for increasing calories
     */
    @Test
    public void foodSwapCalories() {
        FoodSwapService foodSwapService = new FoodSwapService();

        DatabaseManager db = DatabaseManager.getInstance();

        Map<Long, String> meals = db.importMeals(1);

        Object[] mealIds = meals.keySet().toArray();

        int index = (int) (Math.random() * mealIds.length);

        List<Long> foodIds = db.importMeal((Long) mealIds[index]);

        MealBuilder mealBuilder = new MealBuilder().setMealType(MealType.SNACK);

        for (Long id : foodIds) {
            mealBuilder.addFoodItem(db.loadFoodItem(id));
        }

        Goal calorieGoal = new Goal(GoalType.CALORIES, true, 5);
        FoodSwapSuggestion calorieFoodSwap = foodSwapService.suggestSwap(mealBuilder.mealBeingBuilt.foodItems, calorieGoal);
        assertNotEquals(calorieFoodSwap.getOriginalItem(), calorieFoodSwap.getReplacementItem());
        assertTrue(calorieFoodSwap.getReplacementItem().calculateCaloriesFromMacros() > calorieFoodSwap.getOriginalItem().calculateCaloriesFromMacros());
    }

    /**
     * Perform a food swap for increasing protein
     */
    @Test
    public void foodSwapProtein() {
        FoodSwapService foodSwapService = new FoodSwapService();

        DatabaseManager db = DatabaseManager.getInstance();

        Map<Long, String> meals = db.importMeals(1);

        Object[] mealIds = meals.keySet().toArray();

        int index = (int) (Math.random() * mealIds.length);

        List<Long> foodIds = db.importMeal((Long) mealIds[index]);

        MealBuilder mealBuilder = new MealBuilder().setMealType(MealType.SNACK);

        for (Long id : foodIds) {
            mealBuilder.addFoodItem(db.loadFoodItem(id));
        }

        Goal proteinGoal = new Goal(GoalType.PROTEIN, true, 5);
        FoodSwapSuggestion proteinFoodSwap = foodSwapService.suggestSwap(mealBuilder.mealBeingBuilt.foodItems, proteinGoal);
        assertNotEquals(proteinFoodSwap.getOriginalItem(), proteinFoodSwap.getReplacementItem());
        assertTrue(proteinFoodSwap.getReplacementItem().getNutrientValue("PROTEIN") > proteinFoodSwap.getOriginalItem().getNutrientValue("PROTEIN"));
    }

    /**
     * Perform a food swap for increasing fibre
     */
    @Test
    public void foodSwapFibre() {
        FoodSwapService foodSwapService = new FoodSwapService();

        DatabaseManager db = DatabaseManager.getInstance();

        Map<Long, String> meals = db.importMeals(1);

        Object[] mealIds = meals.keySet().toArray();

        int index = (int) (Math.random() * mealIds.length);

        List<Long> foodIds = db.importMeal((Long) mealIds[index]);

        MealBuilder mealBuilder = new MealBuilder().setMealType(MealType.SNACK);

        for (Long id : foodIds) {
            mealBuilder.addFoodItem(db.loadFoodItem(id));
        }

        Goal fibreGoal = new Goal(GoalType.FIBRE, true, 5);
        FoodSwapSuggestion fibreFoodSwap = foodSwapService.suggestSwap(mealBuilder.mealBeingBuilt.foodItems, fibreGoal);
        assertNotEquals(fibreFoodSwap.getOriginalItem(), fibreFoodSwap.getReplacementItem());
        assertTrue(fibreFoodSwap.getReplacementItem().getNutrientValue("FIBRE") >= fibreFoodSwap.getOriginalItem().getNutrientValue("FIBRE"));
    }
}
