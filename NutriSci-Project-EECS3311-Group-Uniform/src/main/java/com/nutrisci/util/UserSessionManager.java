package com.nutrisci.util;

import com.nutrisci.model.User;
import com.nutrisci.model.Gender;
import com.nutrisci.model.Units;
import com.nutrisci.database.FirestoreSingleton;

import java.util.Map;
import java.util.HashMap;

/**
 * Singleton class for managing user sessions and authentication in the NutriSci application.
 * Provides centralized access to current user information and authentication state.
 * 
 * @author NutriSci Team
 * @version 1.0
 */
public class UserSessionManager {
    // Singleton instance
    private static volatile UserSessionManager instance;
    
    // Current user session data
    private User currentUser;
    private boolean isLoggedIn;
    
    // Private constructor to prevent instantiation
    private UserSessionManager() {
        this.currentUser = null;
        this.isLoggedIn = false;
    }
    
    /**
     * Gets the singleton instance of UserSessionManager (thread-safe).
     * 
     * @return UserSessionManager instance
     */
    public static UserSessionManager getInstance() {
        if (instance == null) {
            synchronized (UserSessionManager.class) {
                if (instance == null) {
                    instance = new UserSessionManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Attempts to authenticate a user with the provided email and password.
     * This method integrates with Firebase for user verification.
     * 
     * @param email User's email address
     * @param password User's password
     * @return true if authentication is successful, false otherwise
     */
    // helped by AI - Firebase integration and authentication logic
    public boolean login(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Get Firebase instance
            FirestoreSingleton firestore = FirestoreSingleton.getInstance();
            
            // Query for user with matching email
            // Note: In a real application, you would hash the password and compare hashes
            // For Deliverable 1, we'll do a simple string comparison
            Map<String, Object> userData = findUserByEmail(email);
            
            if (userData != null) {
                String storedPassword = (String) userData.get("password");
                if (password.equals(storedPassword)) {
                    // Authentication successful - create User object
                    currentUser = createUserFromData(userData);
                    isLoggedIn = true;
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Logs out the current user and clears the session.
     */
    public void logout() {
        currentUser = null;
        isLoggedIn = false;
    }
    
    /**
     * Returns the currently logged-in user.
     * 
     * @return Current User object, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return isLoggedIn && currentUser != null;
    }
    
    /**
     * Updates the current user's profile information.
     * 
     * @param updatedUser The updated User object
     */
    public void updateUserProfile(User updatedUser) {
        if (isLoggedIn && updatedUser != null) {
            currentUser = updatedUser;
            
            // Update user data in Firebase
            try {
                FirestoreSingleton firestore = FirestoreSingleton.getInstance();
                Map<String, Object> userData = convertUserToMap(updatedUser);
                firestore.updateDocument("users", currentUser.getEmail(), userData);
            } catch (Exception e) {
                System.err.println("Failed to update user profile: " + e.getMessage());
            }
        }
    }
    
    /**
     * Registers a new user in the system.
     * 
     * @param user The User object to register
     * @return true if registration is successful, false otherwise
     */
    public boolean registerUser(User user) {
        if (user == null || !user.validateProfile()) {
            return false;
        }
        
        try {
            // Check if user already exists
            Map<String, Object> existingUser = findUserByEmail(user.getEmail());
            if (existingUser != null) {
                return false; // User already exists
            }
            
            // Add user to Firebase
            FirestoreSingleton firestore = FirestoreSingleton.getInstance();
            Map<String, Object> userData = convertUserToMap(user);
            firestore.addDocument("users", userData);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Finds a user by email address in the database.
     * 
     * @param email The email address to search for
     * @return User data as Map, or null if not found
     */
    private Map<String, Object> findUserByEmail(String email) {
        try {
            FirestoreSingleton firestore = FirestoreSingleton.getInstance();
            // Query for user with matching email
            // For simplicity in Deliverable 1, we'll use the email as document ID
            return firestore.getDocument("users", email);
        } catch (Exception e) {
            System.err.println("Error finding user: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Converts a User object to a Map for Firebase storage.
     * 
     * @param user The User object to convert
     * @return Map representation of the user
     */
    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("password", user.getPassword()); // In production, this should be hashed
        userData.put("gender", user.getGender() != null ? user.getGender().name() : null);
        userData.put("dateOfBirth", user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
        userData.put("height", user.getHeight());
        userData.put("weight", user.getWeight());
        userData.put("units", user.getUnits() != null ? user.getUnits().name() : null);
        
        // Store goal information if present
        if (user.getUserGoal() != null) {
            Map<String, Object> goalData = new HashMap<>();
            goalData.put("type", user.getUserGoal().getGoalType().name());
            goalData.put("description", user.getUserGoal().getGoalDescription());
            userData.put("goal", goalData);
        }
        
        return userData;
    }
    
    /**
     * Creates a User object from Firebase data.
     * 
     * @param userData The user data from Firebase
     * @return User object, or null if creation fails
     */
    private User createUserFromData(Map<String, Object> userData) {
        try {
            User user = new User();
            user.setName((String) userData.get("name"));
            user.setEmail((String) userData.get("email"));
            user.setPassword((String) userData.get("password"));
            
            // Set other fields if they exist
            if (userData.get("gender") != null) {
                user.setGender(Gender.valueOf((String) userData.get("gender")));
            }
            if (userData.get("dateOfBirth") != null) {
                user.setDateOfBirth(java.time.LocalDate.parse((String) userData.get("dateOfBirth")));
            }
            if (userData.get("height") != null) {
                user.setHeight(((Number) userData.get("height")).doubleValue());
            }
            if (userData.get("weight") != null) {
                user.setWeight(((Number) userData.get("weight")).doubleValue());
            }
            if (userData.get("units") != null) {
                user.setUnits(Units.valueOf((String) userData.get("units")));
            }
            
            return user;
            
        } catch (Exception e) {
            System.err.println("Error creating user from data: " + e.getMessage());
            return null;
        }
    }
} 