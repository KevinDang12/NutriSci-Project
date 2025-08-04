package com.nutrisci.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import com.nutrisci.database.DatabaseManager;
import com.nutrisci.model.Gender;
import com.nutrisci.model.Goal;
import com.nutrisci.model.GoalType;
import com.nutrisci.model.Units;
import com.nutrisci.model.User;
import com.nutrisci.util.UserSessionManager;

// Simple signup dialog for NutriSci
public class SignupDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField ageField;
    private JTextField heightField;
    private JTextField weightField;
    private JComboBox<Gender> genderComboBox;
    private JComboBox<GoalType> goalTypeComboBox;
    private JComboBox<String> directionComboBox;
    private JComboBox<Integer> percentComboBox;
    private JButton signupButton;
    private JButton backButton;
    private boolean signupSuccessful = false;
    private UserSignupData signupData;
    private UserSessionManager userSessionManager;
    private DatabaseManager databaseManager;

    // Data class to hold signup information
    public static class UserSignupData {
        public String email;
        public String password;
        public int age;
        public double height;
        public double weight;
        public Gender gender;
        public com.nutrisci.model.Goal goal;
    }

    // Creates the signup dialog
    public SignupDialog(Frame parent) {
        super(parent, "Sign Up for NutriSci", true);
        userSessionManager = UserSessionManager.getInstance();
        databaseManager = DatabaseManager.getInstance();
        setupUI();
        setupLayout();
        setupListeners();
        pack();
        setLocationRelativeTo(parent);
    }

    // Sets up the UI components
    private void setupUI() {
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        ageField = new JTextField(10);
        heightField = new JTextField(10);
        weightField = new JTextField(10);
        genderComboBox = new JComboBox<>(Gender.values());
        goalTypeComboBox = new JComboBox<>(GoalType.values());
        directionComboBox = new JComboBox<>(new String[]{"Increase", "Decrease"});
        percentComboBox = new JComboBox<>(new Integer[]{5, 10, 15});
        signupButton = new JButton("Sign Up");
        backButton = new JButton("Back to Login");
    }

    // Sets up the layout
    // helped by AI
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Main panel with scroll pane
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel titleLabel = new JLabel("Create Your NutriSci Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Email field
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        // Confirm password field
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);
        
        // Age field
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Age:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(ageField, gbc);
        
        // Height field
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Height (cm):"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(heightField, gbc);
        
        // Weight field
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(new JLabel("Weight (kg):"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(weightField, gbc);
        
        // Gender field
        gbc.gridx = 0;
        gbc.gridy = 7;
        mainPanel.add(new JLabel("Gender:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(genderComboBox, gbc);
        
        // Goal field
        gbc.gridx = 0;
        gbc.gridy = 8;
        mainPanel.add(new JLabel("Goal Type:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(goalTypeComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 9;
        mainPanel.add(new JLabel("Direction:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(directionComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 10;
        mainPanel.add(new JLabel("Percent:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(percentComboBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Sets up event listeners
    private void setupListeners() {
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginDialog loginDialog = new LoginDialog((Frame) getOwner());
                loginDialog.setVisible(true);
            }
        });
        
        // Enter key for signup
        getRootPane().setDefaultButton(signupButton);
    }

    /**
     * Validate wehther the user credentials are empty
     * @param email The user's email
     * @param password The user's password
     * @param confirmPassword The user's confirmed password
     * @return true if all fields are entered, otherwise false with an error message
     */
    private boolean validateCredntials(String email, String password, String confirmPassword) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            ageField.getText().trim().isEmpty() || heightField.getText().trim().isEmpty() ||
            weightField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields", 
                "Signup Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Check if the username exists in the database
     * @param email The user's email
     * @return true if the user does not exist, otherwise false with an error message
     */
    private boolean usernameExists(String email) {
        if (databaseManager.checkIfUserExists(email)) {
            JOptionPane.showMessageDialog(this, 
                "The provided email exists, please enter a different email", 
                "Signup Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Check whether the user's password and confirm password are correct
     * @param password The user's password
     * @param confirmPassword The user's confirmed password
     * @return true if the passwords match, otherwise false with an error message
     */
    private boolean passwordMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match", 
                "Signup Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Check if the age is valid
     * @return true if valid, otherwise false
     */
    private int validateAge() {
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
            if (age < 13 || age > 120) {
                JOptionPane.showMessageDialog(this, 
                    "Age must be between 13 and 120", 
                    "Signup Error", 
                    JOptionPane.ERROR_MESSAGE);
                return -1;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid age", 
                "Signup Error", 
                JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        return age;
    }

    /**
     * Check if the height is valid
     * @return true if valid, otherwise false
     */
    private double validateHeight() {
        double height;
        try {
            height = Double.parseDouble(heightField.getText().trim());
            if (height <= 0 || height > 300) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid height (1-300 cm)", 
                    "Signup Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.out.println("Valid Height");
                return -1;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid height", 
                "Signup Error", 
                JOptionPane.ERROR_MESSAGE);
                System.out.println("Error Height");
            return -1;
        }
        return height;
    }

    /**
     * Check if the weight is valid
     * @return true if valid, otherwise false
     */
    private double validateWeight() {
        double weight;
        try {
            weight = Double.parseDouble(weightField.getText().trim());
            if (weight <= 0 || weight > 500) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid weight (1-500 kg)", 
                    "Signup Error", 
                    JOptionPane.ERROR_MESSAGE);
                    System.out.println("Valid Weight");
                return -1;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid weight", 
                "Signup Error", 
                JOptionPane.ERROR_MESSAGE);
                System.out.println("Error Weight");
            return -1;
        }
        return weight;
    }

    /**
     * Add the user to the database
     * @param signupData The user's signup data
     */
    private void createSignupData(UserSignupData signupData) {
        // Create User object and save to database
        try {
            User newUser = new User();
            newUser.setName(signupData.email.split("@")[0]); // Use email prefix as name
            newUser.setEmail(signupData.email);
            newUser.setPassword(signupData.password);
            newUser.setGender(signupData.gender);
            newUser.setDateOfBirth(LocalDate.now().minusYears(signupData.age));
            newUser.setHeight(signupData.height);
            newUser.setWeight(signupData.weight);
            newUser.setUnits(Units.METRIC);
            newUser.setGoal(signupData.goal);
            
            // Register user in database
            boolean registrationSuccess = userSessionManager.registerUser(newUser);
            
            if (registrationSuccess) {
                // Automatically log in the user after successful registration
                boolean loginSuccess = userSessionManager.login(signupData.email, signupData.password);
                
                if (loginSuccess) {
                    JOptionPane.showMessageDialog(this, 
                        "Account created successfully! You are now logged in.", 
                        "Signup Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    signupSuccessful = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Account created successfully! Please login with your email and password.", 
                        "Signup Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    signupSuccessful = true;
                    dispose();
                    LoginDialog loginDialog = new LoginDialog((Frame) getOwner());
                    loginDialog.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to create account. Please try again.", 
                    "Signup Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error creating account: " + e.getMessage(), 
                "Signup Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Handles signup attempt
    // helped by AI
    private void handleSignup() {
        // Get all the values
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validate required fields
        if (!validateCredntials(email, password, confirmPassword)) {
            return;
        }

        // Check if username exists
        if (!usernameExists(email)) {
            return;
        }
        
        // Check if passwords match
        if (!passwordMatch(password, confirmPassword)) {
            return;
        }
        
        // Validate age
        int age = validateAge();
        if (age <= -1) {
            return;
        }
        
        // Validate height
        double height = validateHeight();
        System.out.println("Height");
        if (height <= -1.0) {
            System.out.println("Unvalid Height");
            return;
        }
        
        // Validate weight
        double weight = validateWeight();
        System.out.println("Weight");
        if (weight <= -1.0) {
            System.out.println("Unvalid Weight");
            return;
        }
        
        // Create signup data
        signupData = new UserSignupData();
        signupData.email = email;
        signupData.password = password;
        signupData.age = age;
        signupData.height = height;
        signupData.weight = weight;
        signupData.gender = (Gender) genderComboBox.getSelectedItem();
        // New goal selection
        GoalType goalType = (GoalType) goalTypeComboBox.getSelectedItem();
        boolean increase = directionComboBox.getSelectedItem().equals("Increase");
        int percent = (Integer) percentComboBox.getSelectedItem();
        signupData.goal = new Goal(goalType, increase, percent);

        createSignupData(signupData);
    }

    // Returns true if signup was successful
    public boolean isSignupSuccessful() {
        return signupSuccessful;
    }

    // Returns the signup data
    public UserSignupData getSignupData() {
        return signupData;
    }
} 