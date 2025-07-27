package com.nutrisci.model;

import java.time.LocalDate;
import java.time.Period;

/**
 * Core data container for all user information in the NutriSci application.
 * This class represents a user profile with personal information, health metrics,
 * and nutritional goals.
 */
public class User {
    private long id; // User's database ID
    private String name; // User's name
    private String email; // User's email
    private String password; // User's password
    private Gender gender; // User's gender
    private LocalDate dateOfBirth; // User's date of birth
    private double height; // Height (cm or inches)
    private double weight; // Weight (kg or lbs)
    private Units units; // Preferred measurement units
    private com.nutrisci.model.Goal goal; // User's nutrition goal
    
    /**
     * Creates a user with default units (metric)
     */
    public User() {
        this.units = Units.METRIC;
    }
    
    /**
     * Creates a user with name, email, and password
     */
    public User(String name, String email, String password) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    /**
     * Returns the user's age in years
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
    
    /**
     * Calculates BMI using height and weight
     * helped by AI
     */
    public double getBMI() {
        if (height <= 0 || weight <= 0) {
            return 0.0;
        }
        double heightInMeters;
        double weightInKg;
        if (units == Units.METRIC) {
            heightInMeters = height / 100.0;
            weightInKg = weight;
        } else {
            heightInMeters = (height * 2.54) / 100.0;
            weightInKg = weight * 0.453592;
        }
        return weightInKg / (heightInMeters * heightInMeters);
    }
    
    /**
     * Checks if all required profile fields are valid
     */
    public boolean validateProfile() {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Validation failed: name is empty");
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Validation failed: email is empty");
            return false;
        }
        if (password == null || password.length() < 1) {
            System.out.println("Validation failed: password is too short (length: " + (password != null ? password.length() : 0) + ")");
            return false;
        }
        if (dateOfBirth == null) {
            System.out.println("Validation failed: dateOfBirth is null");
            return false;
        }
        int age = getAge();
        if (age < 13 || age > 120) {
            System.out.println("Validation failed: age is invalid (" + age + ")");
            return false;
        }
        if (height <= 0 || weight <= 0) {
            System.out.println("Validation failed: height or weight is invalid (height: " + height + ", weight: " + weight + ")");
            return false;
        }
        System.out.println("User validation passed successfully");
        return true;
    }
    
    /**
     * Returns height as a string with units
     */
    public String getDisplayHeight() {
        if (units == Units.METRIC) {
            return String.format("%.1f cm", height);
        } else {
            return String.format("%.1f inches", height);
        }
    }
    
    /**
     * Returns weight as a string with units
     */
    public String getDisplayWeight() {
        if (units == Units.METRIC) {
            return String.format("%.1f kg", weight);
        } else {
            return String.format("%.1f lbs", weight);
        }
    }
    
    // Getters and setters for all fields
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public Units getUnits() { return units; }
    public void setUnits(Units units) { this.units = units; }
    public com.nutrisci.model.Goal getGoal() { return goal; }
    public void setGoal(com.nutrisci.model.Goal goal) { this.goal = goal; }
    
    /**
     * Returns a summary string for the user
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + getAge() +
                ", bmi=" + String.format("%.1f", getBMI()) +
                ", units=" + units +
                ", goal=" + (goal != null ? goal.toString() : "None") +
                '}';
    }
} 