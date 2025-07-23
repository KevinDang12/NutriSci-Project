package com.nutrisci.util;

import com.nutrisci.model.User;
import com.nutrisci.model.Gender;
import com.nutrisci.model.Units;
import com.nutrisci.database.DatabaseManager;

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
    
    // Database manager for user operations
    private DatabaseManager databaseManager;
    
    // Private constructor to prevent instantiation
    private UserSessionManager() {
        this.currentUser = null;
        this.isLoggedIn = false;
        this.databaseManager = DatabaseManager.getInstance();
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
     * This method integrates with Azure MySQL for user verification.
     * 
     * @param email User's email address
     * @param password User's password
     * @return true if authentication is successful, false otherwise
     */
    public boolean login(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        
        try {
            // For now, we'll use a simple approach with email as userId
            // In a real application, you would query by email first to get the userId
            String userId = email; // Simplified for demo - should query by email
            
            User authenticatedUser = databaseManager.authenticateUser(userId, password);
            
            if (authenticatedUser != null) {
                currentUser = authenticatedUser;
                isLoggedIn = true;
                return true;
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
            
            // Update user data in MySQL database
            try {
                boolean success = databaseManager.updateUserProfile(updatedUser);
                if (!success) {
                    System.err.println("Failed to update user profile in database");
                }
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
            // Add user to MySQL database
            boolean success = databaseManager.saveUser(user);
            return success;
            
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }
} 