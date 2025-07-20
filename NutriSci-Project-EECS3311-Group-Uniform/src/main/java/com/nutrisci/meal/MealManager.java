package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.*;

import com.nutrisci.calculator.NutritionalCalculator;
import com.nutrisci.calculator.NutritionalData;
import com.nutrisci.database.DatabaseManager;
import com.nutrisci.util.UserSessionManager;
import com.nutrisci.model.User;

public class MealManager {
    private List<MealObserver> observers = new ArrayList<>();
    private UserSessionManager userSessionManager = UserSessionManager.getInstance();
    private NutritionalCalculator nutritionalCalculator = new NutritionalCalculator();
    private Map<LocalDate, List<Meal>> mealCache = new HashMap<>();
    private DatabaseManager db;

    public boolean addMeal(Meal meal) {
        // Validate meal (assume isValid method exists or use basic checks)
        if (meal == null || meal.getFoodItems().isEmpty()) return false;
        User user = userSessionManager.getCurrentUser();
        if (user == null) return false;
        // Set user ID (assume setUserId or setEmail method exists)
        // meal.setUserId(user.getEmail()); // Uncomment if method exists
        // Save to Firestore
        try {
            db = DatabaseManager.getInstance();
            db.saveMeal(meal, 0);
            notifyObservers(MealEvent.MEAL_ADDED, meal);
            // TODO: Trigger goal progress update
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMeal(Meal meal) {
        if (meal == null) return false;
        User user = userSessionManager.getCurrentUser();
        if (user == null) return false;
        // Security: Only allow update if meal belongs to user (assume meal has userId/email)
        // if (!user.getEmail().equals(meal.getUserId())) return false;
        try {
            db = DatabaseManager.getInstance();
            db.updateMeal(meal);
            notifyObservers(MealEvent.MEAL_UPDATED, meal);
            // TODO: Recalculate daily totals and goal progress
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMeal(Long mealId) {
        // Retrieve meal for date and details
        try {
            // firestore.deleteDocument("meals", String.valueOf(mealId));
            // Remove from cache
            db = DatabaseManager.getInstance();
            db.deleteMeal(mealId);
            notifyObservers(MealEvent.MEAL_DELETED, null);
            // TODO: Recalculate daily totals and goal progress
            // TODO: Log meal deletion for user history
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Meal> getMealsForDate(LocalDate date) {
        db = DatabaseManager.getInstance();
        // Get ID
        List<Meal> meals = db.getMealsForUser(null, date, date);
        return meals;
    }

    public List<Meal> getMealsForDateRange(LocalDate start, LocalDate end) {
        db = DatabaseManager.getInstance();
        // Get ID
        List<Meal> meals = db.getMealsForUser(null, start, end);
        return meals;
    }

    public boolean importMeal(Meal sourceMeal, LocalDate targetDate) {
        // Validate target date allows this meal type
        if (!sourceMeal.canAddToDate(targetDate)) return false;
        Meal newMeal = (Meal) sourceMeal.copyMeal();
        newMeal.date = targetDate;
        // Optionally link to original meal (add field if needed)
        return addMeal(newMeal);
    }

    public boolean swapFoodInMeal(Long mealId, FoodItem original, FoodItem replacement) {
        // Retrieve meal by ID
        db = DatabaseManager.getInstance();
        return db.swapFoodInMeal(mealId, original, replacement);
    }

    public void addObserver(MealObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(MealObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(MealEvent event, Meal meal) {
        for (MealObserver observer : observers) {
            try {
                observer.onMealChanged(event, meal);
            } catch (Exception e) {
                e.printStackTrace();
                // Continue notifying others
            }
        }
    }

    /**
     * Need to uncomment food items
     */
    // public NutritionalData calculateDailyTotals(LocalDate date) {
    //     List<Meal> meals = getMealsForDate(date);
    //     NutritionalData total = nutritionalCalculator.calculateMealNutrition(
    //         meals.stream().flatMap(m -> m.getFoodItems().stream()).toList()
    //     );
    //     // Optionally cache result
    //     return total;
    // }

    public Map<LocalDate, List<Meal>> getMealHistory(int dayCount) {
        User user = userSessionManager.getCurrentUser();
        if (user == null) return Collections.emptyMap();
        
        Map<LocalDate, List<Meal>> history = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate lastDays = today.minusDays(dayCount);
        db = DatabaseManager.getInstance();
        List<Meal> meals = db.getMealsForUser(null, lastDays, today);

        for (Meal meal : meals) {
            LocalDate dateKey = meal.createdAt.toLocalDate();
            if (!history.containsKey(dateKey)) {
                history.put(dateKey, new ArrayList<Meal>());
            }
            history.get(dateKey).add(meal);
        }

        return history;
    }
}
