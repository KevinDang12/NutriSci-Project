package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.*;

import com.nutrisci.calculator.NutritionalCalculator;
import com.nutrisci.calculator.NutritionalData;
import com.nutrisci.database.DatabaseManager;
import com.nutrisci.util.UserSessionManager;
import com.nutrisci.model.User;

/**
 * Coordinate Meal Logging operations
 */
public class MealManager {
    private List<MealObserver> observers = new ArrayList<>();
    private UserSessionManager userSessionManager = UserSessionManager.getInstance();
    private NutritionalCalculator nutritionalCalculator = new NutritionalCalculator();
    private DatabaseManager db = DatabaseManager.getInstance();

    /**
     * Get the current user's ID from the session
     * @return The current user's ID, or 0 if no user is logged in
     */
    private long getCurrentUserId() {
        User currentUser = userSessionManager.getCurrentUser();
        return currentUser != null ? currentUser.getId() : 0;
    }

    /**
     * Add meal to the database
     * @param meal The meal to add
     * @return true if successful, otherwise false
     */
    public boolean addMeal(Meal meal) {
        try {
            long userId = getCurrentUserId();
            if (userId == 0) {
                System.err.println("No user logged in");
                return false;
            }
            db.saveMeal(meal, userId);
            notifyObservers(MealEvent.MEAL_ADDED, meal);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update the meal for the user
     * @param meal The new meal to update in the database
     * @return true if sucessful, otherwise false
     */
    public boolean updateMeal(Meal meal) {
        if (meal == null) return false;
        User user = userSessionManager.getCurrentUser();
        if (user == null) return false;
        
        try {
            db.updateMeal(meal);
            notifyObservers(MealEvent.MEAL_UPDATED, meal);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete the meal from the database
     * @param mealId The meal to delete
     * @return true if successful, otherwise false
     */
    public boolean deleteMeal(Long mealId) {
        try {
            db.deleteMeal(mealId);
            notifyObservers(MealEvent.MEAL_DELETED, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Import a list of meals from the user
     * @return Map of meals and their ids
     */
    public Map<Long, String> importMeals() {
        return db.importMeals(getCurrentUserId());
    }

    /**
     * Get a list for available meal types for a certain date
     * @param date The date to retreive the meal types
     * @return List of meal types logged for the date
     */
    public List<MealType> getAvailableMealTypes(LocalDate date) {
        return db.getAvailableMealTypes(getCurrentUserId(), date);
    }

    /**
     * Get a list of meals for a certain date
     * @param date The date to retrieve the meal
     * @return The list of meals for the date
     */
    public List<Meal> getMealsForDate(LocalDate date) {
        List<Meal> meals = db.getMealsForUser(getCurrentUserId(), date, date);
        return meals;
    }

    /**
     * Get a list of meals between the two dates
     * @param start The start date
     * @param end The end date
     * @return The list of meals between the two dates
     */
    public List<Meal> getMealsForDateRange(LocalDate start, LocalDate end) {
        List<Meal> meals = db.getMealsForUser(getCurrentUserId(), start, end);
        return meals;
    }

    /**
     * Import the list of food items for the meal logging page
     * @param mealId The meal ID to retrieve the food items
     * @return A list of Food IDs
     */
    public List<Long> importMeal(long mealId) {
        return db.importMeal(mealId);
    }

    /**
     * Perform a food swap with the food items in the meals
     * @param mealId The meal id to perform the food swap
     * @param original The original food item
     * @param replacement The replacement food item
     * @return true if successful, otherwise false
     */
    public boolean swapFoodInMeal(Long mealId, FoodItem original, FoodItem replacement) {
        return db.swapFoodInMeal(mealId, original, replacement);
    }

    /**
     * Add a meal observer
     * @param observer The meal observer to add
     */
    public void addObserver(MealObserver observer) {
        observers.add(observer);
    }

    /**
     * Remove a meal observer
     * @param observer The meal observer to remove
     */
    public void removeObserver(MealObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all registered observers of meal events using the Observer pattern.
     * helped by AI
     * @param event The type of meal event
     * @param meal The meal involved in the event
     */
    public void notifyObservers(MealEvent event, Meal meal) {
        for (MealObserver observer : observers) {
            try {
                observer.onMealChanged(event, meal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Check if a meal type can be added to a certain date
     * @param type The type of meal
     * @param date The given date
     * @return true if it is possible to add, otherwise false
     */
    public boolean canAddMealType(MealType type, LocalDate date) {
        return db.canAddMealType(getCurrentUserId(), type, date);
    }

    /**
     * Get the number of meal type for a certain date
     * @param type The type of meal
     * @param date The given date
     * @return The number of meals of a given type for a certain date
     */
    public int getMealCountForType(MealType type, LocalDate date) {
        return db.getMealCountForType(getCurrentUserId(), type, date);
    }

    /**
     * Get a list of food items from the database
     * @return HashMap of the food description and its id
     */
    public Map<Long, String> getFoodItems() {
        return db.getFoodItems();
    }

    /**
     * Load the information for a given food item
     * @param foodId The food id
     * @return The Food Item with the provided information
     */
    public FoodItem loadFoodItem(Long foodId) {
        return db.loadFoodItem(foodId);
    }

    /**
     * Calculates daily nutritional totals for a given date.
     * helped by AI
     * @param date The date to calculate totals for
     * @return NutritionalData object with daily totals
     */
    public NutritionalData calculateDailyTotals(LocalDate date) {
        List<Meal> meals = getMealsForDate(date);
        NutritionalData nutritionalData = new NutritionalData(0, 0, 0, 0, 0);

        for (Meal meal : meals) {
            nutritionalData.add(nutritionalCalculator.calculateMealNutrition(meal.getFoodItems()));
        }

        return nutritionalData;
    }

    /**
     * Retrieves meal history for a specified number of days.
     * helped by AI
     * @param dayCount Number of days to retrieve
     * @return Map of dates to lists of meals
     */
    public Map<LocalDate, List<Meal>> getMealHistory(int dayCount) {
        User user = userSessionManager.getCurrentUser();
        if (user == null) return Collections.emptyMap();
        
        Map<LocalDate, List<Meal>> history = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate lastDays = today.minusDays(dayCount);
        List<Meal> meals = db.getMealsForUser(getCurrentUserId(), lastDays, today);

        for (Meal meal : meals) {
            LocalDate dateKey = meal.getCreatedAt().toLocalDate();
            if (!history.containsKey(dateKey)) {
                history.put(dateKey, new ArrayList<Meal>());
            }
            history.get(dateKey).add(meal);
        }

        return history;
    }
}
