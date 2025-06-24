package com.nutrisci.model;

import java.time.LocalDate;
import java.time.Period;

/**
 * Core data container for all user information in the NutriSci application.
 * This class represents a user profile with personal information, health metrics,
 * and nutritional goals.
 * 
 * @author NutriSci Team
 * @version 1.0
 */
public class User {
    private String name;
    private String email;
    private String password;
    private Gender gender;
    private LocalDate dateOfBirth;
    private double height; // in cm for metric, inches for imperial
    private double weight; // in kg for metric, lbs for imperial
    private Units units;
    private Goal userGoal;
    
    /**
     * Default constructor for User
     */
    public User() {
        this.units = Units.METRIC; // Default to metric units
    }
    
    /**
     * Constructor with basic user information
     * 
     * @param name User's full name
     * @param email User's email address
     * @param password User's password
     */
    public User(String name, String email, String password) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    /**
     * Calculates the current age of the user based on their date of birth.
     * 
     * @return Current age in years
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
    
    /**
     * Calculates the Body Mass Index (BMI) for health metrics.
     * Formula: weight (kg) / height (m)²
     * 
     * @return BMI value as a double
     */
    // helped by AI - Complex unit conversion and BMI calculation logic
    public double getBMI() {
        if (height <= 0 || weight <= 0) {
            return 0.0;
        }
        
        double heightInMeters;
        double weightInKg;
        
        if (units == Units.METRIC) {
            heightInMeters = height / 100.0; // Convert cm to meters
            weightInKg = weight;
        } else {
            // Convert imperial to metric for BMI calculation
            heightInMeters = (height * 2.54) / 100.0; // inches to meters
            weightInKg = weight * 0.453592; // lbs to kg
        }
        
        return weightInKg / (heightInMeters * heightInMeters);
    }
    
    /**
     * Validates that all required user profile fields are present and valid.
     * 
     * @return true if profile is valid, false otherwise
     */
    // helped by AI - Comprehensive validation logic with multiple field checks
    public boolean validateProfile() {
        // Check for null or empty required fields
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.length() < 8) {
            return false;
        }
        if (dateOfBirth == null) {
            return false;
        }
        
        // Validate age range (13-120 years)
        int age = getAge();
        if (age < 13 || age > 120) {
            return false;
        }
        
        // Validate height and weight are positive
        if (height <= 0 || weight <= 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns the user's height formatted according to their preferred units.
     * 
     * @return Formatted height string with appropriate units
     */
    public String getDisplayHeight() {
        if (units == Units.METRIC) {
            return String.format("%.1f cm", height);
        } else {
            return String.format("%.1f inches", height);
        }
    }
    
    /**
     * Returns the user's weight formatted according to their preferred units.
     * 
     * @return Formatted weight string with appropriate units
     */
    public String getDisplayWeight() {
        if (units == Units.METRIC) {
            return String.format("%.1f kg", weight);
        } else {
            return String.format("%.1f lbs", weight);
        }
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public Units getUnits() {
        return units;
    }
    
    public void setUnits(Units units) {
        this.units = units;
    }
    
    public Goal getUserGoal() {
        return userGoal;
    }
    
    public void setUserGoal(Goal userGoal) {
        this.userGoal = userGoal;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + getAge() +
                ", bmi=" + String.format("%.1f", getBMI()) +
                ", units=" + units +
                '}';
    }
} 