package com.nutrisci.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// Builder for creating User objects step-by-step with validation
public class UserProfileBuilder {
    private String name;
    private String email;
    private String password;
    private Gender gender;
    private LocalDate dateOfBirth;
    private double height;
    private double weight;
    private Units units;
    private Goal userGoal;
    private List<String> validationErrors;
    
    // Initializes the builder with default values
    public UserProfileBuilder() {
        this.units = Units.METRIC;
        this.validationErrors = new ArrayList<>();
    }
    // Sets and validates the user's name
    public UserProfileBuilder setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            validationErrors.add("Name cannot be empty");
        } else if (name.trim().length() < 2) {
            validationErrors.add("Name must be at least 2 characters long");
        } else {
            this.name = name.trim();
        }
        return this;
    }
    // Sets and validates the user's email address
    public UserProfileBuilder setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            validationErrors.add("Email cannot be empty");
        } else if (!isValidEmail(email)) {
            validationErrors.add("Email format is invalid");
        } else {
            this.email = email.trim().toLowerCase();
        }
        return this;
    }
    // Sets and validates the user's password
    public UserProfileBuilder setPassword(String password) {
        if (password == null || password.length() < 8) {
            validationErrors.add("Password must be at least 8 characters long");
        } else {
            this.password = password;
        }
        return this;
    }
    // Sets basic user info (gender, dob, height, weight)
    public UserProfileBuilder setBasicInfo(Gender gender, LocalDate dateOfBirth, double height, double weight) {
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weight = weight;
        if (dateOfBirth != null) {
            int age = java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
            if (age < 13 || age > 120) {
                validationErrors.add("Age must be between 13 and 120 years");
            }
        }
        if (height <= 0) {
            validationErrors.add("Height must be positive");
        }
        if (weight <= 0) {
            validationErrors.add("Weight must be positive");
        }
        return this;
    }
    // Sets the user's nutritional goal using the GoalFactory
    public UserProfileBuilder setGoal(GoalType type, int value) {
        Goal goal = GoalFactory.createGoal(type, value);
        if (goal == null) {
            validationErrors.add("Invalid goal type or value");
        } else {
            this.userGoal = goal;
        }
        return this;
    }
    // Sets the user's preferred measurement units
    public UserProfileBuilder setUnits(Units units) {
        if (units == null) {
            validationErrors.add("Units cannot be null");
        } else {
            this.units = units;
        }
        return this;
    }
    // Builds and returns a User object if all validation passes
    public User build() {
        if (!validationErrors.isEmpty()) {
            return null;
        }
        User user = new User(name, email, password);
        user.setGender(gender);
        user.setDateOfBirth(dateOfBirth);
        user.setHeight(height);
        user.setWeight(weight);
        user.setUnits(units);
        user.setGoal(userGoal);
        return user;
    }
    // Returns a list of validation errors
    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }
    // Checks if the builder has any validation errors
    public boolean hasValidationErrors() {
        return !validationErrors.isEmpty();
    }
    // Clears all validation errors
    public void clearValidationErrors() {
        validationErrors.clear();
    }
    // Validates email format using a regex pattern
    // helped by AI
    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(emailPattern, email);
    }
    // Resets the builder to its initial state
    public void reset() {
        this.name = null;
        this.email = null;
        this.password = null;
        this.gender = null;
        this.dateOfBirth = null;
        this.height = 0;
        this.weight = 0;
        this.units = Units.METRIC;
        this.userGoal = null;
        this.validationErrors.clear();
    }
} 