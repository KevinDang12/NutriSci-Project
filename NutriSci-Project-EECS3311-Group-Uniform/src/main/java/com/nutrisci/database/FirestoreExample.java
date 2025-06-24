package com.nutrisci.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example class demonstrating how to use the FirestoreSingleton
 * for various database operations.
 */
public class FirestoreExample {
    
    public static void main(String[] args) {
        // Get the singleton instance
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        
        try {
            System.out.println("=== Example 1: Retrieving a document ===");

            // Map<String, Object> nutrientSources = getNutrientSources("0");
            // System.out.println(nutrientSources);

            // Map<String, Object> userData = new HashMap<>();
            // userData.put("name", "John Doe");
            // userData.put("email", "john.doe@example.com");
            // userData.put("age", 30);
            // userData.put("city", "Toronto");
            
            // String documentId = firestore.addDocument("users", userData);
            // System.out.println("Document added with ID: " + documentId);

            List<Map<String, Object>> yieldAmounts = findYieldAmounts();
            System.out.println(yieldAmounts);
            
        } catch (Exception e) {
            System.err.println("Error during Firestore operations: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the connection when done
            firestore.close();
        }
    }
    
    /**
     * Example method for adding a user to the database
     */
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
    
    /**
     * Example method for retrieving a user by ID
     */
    public static Map<String, Object> getUser(String userId) {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        return firestore.getDocument("users", userId);
    }
    
    /**
     * Example method for updating user information
     */
    public static void updateUser(String userId, String newName, String newEmail) {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", newName);
        updateData.put("email", newEmail);
        updateData.put("updatedAt", java.time.LocalDateTime.now().toString());
        
        firestore.updateDocument("users", userId, updateData);
    }
    
    /**
     * Example method for deleting a user
     */
    public static void deleteUser(String userId) {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        firestore.deleteDocument("users", userId);
    }
    
    /**
     * Example method for finding users by city
     */
    public static List<Map<String, Object>> findUsersByCity(String city) {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        return firestore.queryDocuments("users", "city", city);
    }

    /**
     * Retrieve a nutrient source by ID
     * @param userId ID of the nutrient source
     * @return Map of the nutrient source
     */
    public static Map<String, Object> getNutrientSources(String userId) {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        return firestore.getDocument("nutrientSources", userId);
    }

    public static List<Map<String,Object>> findYieldAmounts() {
        FirestoreSingleton firestore = FirestoreSingleton.getInstance();
        return firestore.queryDocuments("yieldAmounts", "FoodID", 735);
    }
} 