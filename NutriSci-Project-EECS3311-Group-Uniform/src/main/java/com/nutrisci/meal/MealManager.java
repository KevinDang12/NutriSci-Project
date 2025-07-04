package com.nutrisci.meal;

import java.time.LocalDate;
import java.util.*;

import com.nutrisci.calculator.NutritionalCalculator;
import com.nutrisci.database.FirestoreSingleton;
import com.nutrisci.util.UserSessionManager;
import com.nutrisci.model.User;

public class MealManager {
    private List<MealObserver> observers = new ArrayList<>();
    private FirestoreSingleton firestore = FirestoreSingleton.getInstance();
    private UserSessionManager userSessionManager = UserSessionManager.getInstance();
    private NutritionalCalculator nutritionalCalculator = new NutritionalCalculator();
    private Map<LocalDate, List<Meal>> mealCache = new HashMap<>();

    public boolean addMeal(Meal meal) {
        // Validate meal (assume isValid method exists or use basic checks)
        if (meal == null || meal.getFoodItems().isEmpty()) return false;
        User user = userSessionManager.getCurrentUser();
        if (user == null) return false;
        // Set user ID (assume setUserId or setEmail method exists)
        // meal.setUserId(user.getEmail()); // Uncomment if method exists
        // Save to Firestore
        try {
            Map<String, Object> mealData = mealToMap(meal, user.getEmail());
            String mealId = firestore.addDocument("meals", mealData);
            // Optionally set meal.id = mealId;
            // Update cache
            LocalDate date = meal.date;
            mealCache.computeIfAbsent(date, k -> new ArrayList<>()).add(meal);
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
            Map<String, Object> mealData = mealToMap(meal, user.getEmail());
            // Assume meal.id is set and used as documentId
            firestore.updateDocument("meals", String.valueOf(meal.id), mealData);
            // Update cache
            LocalDate date = meal.date;
            List<Meal> meals = mealCache.get(date);
            if (meals != null) {
                meals.removeIf(m -> m.id == meal.id);
                meals.add(meal);
            }
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
        Meal mealToDelete = null;
        for (List<Meal> meals : mealCache.values()) {
            for (Meal m : meals) {
                if (m.id == mealId) {
                    mealToDelete = m;
                    break;
                }
            }
        }
        if (mealToDelete == null) return false;
        try {
            firestore.deleteDocument("meals", String.valueOf(mealId));
            // Remove from cache
            List<Meal> meals = mealCache.get(mealToDelete.date);
            if (meals != null) {
                meals.removeIf(m -> m.id == mealId);
            }
            notifyObservers(MealEvent.MEAL_DELETED, mealToDelete);
            // TODO: Recalculate daily totals and goal progress
            // TODO: Log meal deletion for user history
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Meal> getMealsForDate(LocalDate date) {
        if (mealCache.containsKey(date)) {
            return mealCache.get(date);
        }
        // Query Firestore for meals for this date and user
        User user = userSessionManager.getCurrentUser();
        if (user == null) return Collections.emptyList();
        List<Map<String, Object>> docs = firestore.queryDocuments("meals", "date", date.toString());
        List<Meal> meals = new ArrayList<>();
        for (Map<String, Object> doc : docs) {
            if (user.getEmail().equals(doc.get("userId"))) {
                meals.add(mapToMeal(doc));
            }
        }
        // Sort by meal type and creation time if available
        meals.sort(Comparator.comparing((Meal m) -> m.getMealType().ordinal()));
        mealCache.put(date, meals);
        return meals;
    }

    public List<Meal> getMealsForDateRange(LocalDate start, LocalDate end) {
        User user = userSessionManager.getCurrentUser();
        if (user == null) return Collections.emptyList();
        // Query Firestore for meals in date range
        // (Assume queryDocuments can be extended for range, or filter after fetch)
        List<Map<String, Object>> docs = firestore.getAllDocuments("meals");
        List<Meal> meals = new ArrayList<>();
        for (Map<String, Object> doc : docs) {
            LocalDate mealDate = LocalDate.parse((String) doc.get("date"));
            if (!mealDate.isBefore(start) && !mealDate.isAfter(end) && user.getEmail().equals(doc.get("userId"))) {
                meals.add(mapToMeal(doc));
            }
        }
        // No caching for range queries
        meals.sort(Comparator.comparing((Meal m) -> m.date));
        return meals;
    }

    public boolean importMeal(Meal sourceMeal, LocalDate targetDate) {
        // Validate target date allows this meal type
        if (!sourceMeal.canAddToDate(targetDate)) return false;
        try {
            Meal newMeal = (Meal) sourceMeal.clone();
            newMeal.date = targetDate;
            // Optionally link to original meal (add field if needed)
            return addMeal(newMeal);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean swapFoodInMeal(Long mealId, FoodItem original, FoodItem replacement) {
        // Retrieve meal by ID
        Meal meal = null;
        for (List<Meal> meals : mealCache.values()) {
            for (Meal m : meals) {
                if (m.id == mealId) {
                    meal = m;
                    break;
                }
            }
        }
        if (meal == null) return false;
        meal.removeFoodItem(original);
        meal.addFoodItem(replacement, 1); // TODO: Adjust quantity as needed
        // Recalculate nutrition if needed
        return updateMeal(meal);
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
        List<Map<String, Object>> docs = firestore.getAllDocuments("meals");
        Map<LocalDate, List<Meal>> history = new HashMap<>();
        LocalDate today = LocalDate.now();
        for (Map<String, Object> doc : docs) {
            LocalDate mealDate = LocalDate.parse((String) doc.get("date"));
            if (!mealDate.isBefore(today.minusDays(dayCount)) && user.getEmail().equals(doc.get("userId"))) {
                history.computeIfAbsent(mealDate, k -> new ArrayList<>()).add(mapToMeal(doc));
            }
        }
        return history;
    }

    // Helper: Convert Meal to Map for Firestore
    private Map<String, Object> mealToMap(Meal meal, String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", meal.id);
        map.put("date", meal.date.toString());
        map.put("mealType", meal.getMealType().name());
        map.put("userId", userId);
        map.put("foodItems", meal.getFoodItems()); // You may need to serialize food items
        map.put("notes", meal.notes);
        map.put("createdAt", meal.createdAt != null ? meal.createdAt.toString() : null);
        map.put("updatedAt", meal.updatedAt != null ? meal.updatedAt.toString() : null);
        return map;
    }

    // Helper: Convert Map to Meal (stub, needs real implementation)
    private Meal mapToMeal(Map<String, Object> map) {
        // TODO: Implement conversion from map to Meal object
        return null;
    }
}
