package com.nutrisci.model;

import com.nutrisci.calculator.NutritionalData;
import com.nutrisci.util.UserSessionManager;

import java.time.LocalDate;

// Test class to demonstrate the user component functionality
public class UserComponentTest {
    public static void main(String[] args) {
        System.out.println("=== NutriSci User Component Test ===\n");
        // helped by AI - User profile creation and validation
        // Test 1: Create a user using UserProfileBuilder
        System.out.println("1. Creating a user profile...");
        UserProfileBuilder builder = new UserProfileBuilder();
        User user = builder
            .setName("John Doe")
            .setEmail("john.doe@example.com")
            .setPassword("securepassword123")
            .setBasicInfo(Gender.MALE, LocalDate.of(1995, 5, 15), 175.0, 70.0)
            .setGoal(GoalType.INCREASE_PROTEIN, 5)
            .setUnits(Units.METRIC)
            .build();
        if (user != null) {
            System.out.println("✓ User created successfully: " + user.getName());
            System.out.println("  Age: " + user.getAge() + " years");
            System.out.println("  BMI: " + String.format("%.1f", user.getBMI()));
            System.out.println("  Height: " + user.getDisplayHeight());
            System.out.println("  Weight: " + user.getDisplayWeight());
            System.out.println("  Goal: " + user.getUserGoal().getGoalDescription());
        } else {
            System.out.println("✗ User creation failed. Errors:");
            for (String error : builder.getValidationErrors()) {
                System.out.println("  - " + error);
            }
        }
        // Test 2: Test goal functionality
        System.out.println("\n2. Testing goal functionality...");
        if (user != null && user.getUserGoal() != null) {
            Goal goal = user.getUserGoal();
            // helped by AI - Goal progress and achievement
            // Test with insufficient protein
            NutritionalData lowProtein = new NutritionalData(2000, 45, 250, 80, 25);
            System.out.println("  Current protein: 45g, Target: " + 
                             ((IncreaseProteinGoal) goal).getTotalTargetProtein() + "g");
            System.out.println("  Goal achieved: " + goal.isAchieved(lowProtein));
            System.out.println("  Progress: " + goal.getProgressMessage(lowProtein));
            // Test with sufficient protein
            NutritionalData highProtein = new NutritionalData(2000, 60, 250, 80, 25);
            System.out.println("  Current protein: 60g, Target: " + 
                             ((IncreaseProteinGoal) goal).getTotalTargetProtein() + "g");
            System.out.println("  Goal achieved: " + goal.isAchieved(highProtein));
            System.out.println("  Progress: " + goal.getProgressMessage(highProtein));
        }
        // Test 3: Test GoalFactory
        System.out.println("\n3. Testing GoalFactory...");
        System.out.println("Available goals:");
        for (GoalFactory.GoalOption option : GoalFactory.getAvailableGoals()) {
            System.out.println("  - " + option.getDisplayText());
        }
        // Test 4: Test UserSessionManager (without Firebase for demo)
        System.out.println("\n4. Testing UserSessionManager...");
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        System.out.println("  Session manager instance created: " + (sessionManager != null));
        System.out.println("  User logged in: " + sessionManager.isUserLoggedIn());
        // Test 5: Profile validation
        System.out.println("\n5. Testing profile validation...");
        if (user != null) {
            System.out.println("  Profile valid: " + user.validateProfile());
            // Test invalid profile
            User invalidUser = new User("", "", "123");
            System.out.println("  Invalid profile valid: " + invalidUser.validateProfile());
        }
        System.out.println("\n=== Test Complete ===");
    }
} 