package com.nutrisci.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// FirestoreSingleton for database operations
public class FirestoreExample {
    public static void main(String[] args) {
        // helped by AI - Firestore usage
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        try {
            System.out.println("=== Example 1: Retrieving a document ===");
            // Example: find yield amounts
            List<Map<String, Object>> yieldAmounts = findYieldAmounts();
            System.out.println(yieldAmounts);
        } catch (Exception e) {
            System.err.println("Error during Firestore operations: " + e.getMessage());
            e.printStackTrace();
        } finally {
            firestore.close();
        }
    }

    // Adds a user to the database
    public static String addUser(String name, String email, int age, String city) {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("age", age);
        userData.put("city", city);
        userData.put("createdAt", java.time.LocalDateTime.now().toString());
        return firestore.addDocument("users", userData);
    }

    // Gets a user by ID
    public static Map<String, Object> getUser(String userId) {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        return firestore.getDocument("users", userId);
    }

    // Updates user information
    public static void updateUser(String userId, String newName, String newEmail) {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", newName);
        updateData.put("email", newEmail);
        updateData.put("updatedAt", java.time.LocalDateTime.now().toString());
        firestore.updateDocument("users", userId, updateData);
    }

    // Deletes a user
    public static void deleteUser(String userId) {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        firestore.deleteDocument("users", userId);
    }

    // Finds users by city
    public static List<Map<String, Object>> findUsersByCity(String city) {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        return firestore.queryDocuments("users", "city", city);
    }

    // Gets a nutrient source by ID
    public static Map<String, Object> getNutrientSources(String userId) {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        return firestore.getDocument("nutrientSources", userId);
    }

    // Finds yield amounts for a specific food (example query)
    // helped by AI
    public static List<Map<String,Object>> findYieldAmounts() {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        return firestore.queryDocuments("yieldAmounts", "FoodID", 735);
    }
} 