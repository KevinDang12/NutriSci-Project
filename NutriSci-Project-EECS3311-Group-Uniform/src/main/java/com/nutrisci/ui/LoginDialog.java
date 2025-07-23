package com.nutrisci.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Simple login dialog for NutriSci
public class LoginDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JButton cancelButton;
    private boolean loginSuccessful = false;
    private String userEmail = "";
    private String userPassword = "";

    // Creates the login dialog
    public LoginDialog(Frame parent) {
        super(parent, "Login to NutriSci", true);
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
        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");
        cancelButton = new JButton("Cancel");
    }

    // Sets up the layout
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel titleLabel = new JLabel("Welcome to NutriSci");
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
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Sets up event listeners
    private void setupListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Enter key for login
        getRootPane().setDefaultButton(loginButton);
    }

    // Handles login attempt
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both email and password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // TODO: Add actual authentication logic here
        // For now, just accept any non-empty email/password
        if (!email.isEmpty() && !password.isEmpty()) {
            userEmail = email;
            userPassword = password;
            loginSuccessful = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid email or password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Handles signup button click
    private void handleSignup() {
        dispose();
        SignupDialog signupDialog = new SignupDialog((Frame) getOwner());
        signupDialog.setVisible(true);
    }

    // Returns true if login was successful
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    // Returns the entered email
    public String getUserEmail() {
        return userEmail;
    }

    // Returns the entered password
    public String getUserPassword() {
        return userPassword;
    }
} 