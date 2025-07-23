package com.nutrisci.ui;

import javax.swing.*;
import java.awt.*;

// Simple test class to demonstrate login and signup functionality
public class LoginSignupTest {
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create a test frame
        JFrame testFrame = new JFrame("NutriSci Login/Signup Test");
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setSize(400, 200);
        testFrame.setLocationRelativeTo(null);
        
        // Create test panel
        JPanel testPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel titleLabel = new JLabel("NutriSci Login/Signup Test");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        testPanel.add(titleLabel, gbc);
        
        JButton testLoginButton = new JButton("Test Login Dialog");
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        testPanel.add(testLoginButton, gbc);
        
        JButton testSignupButton = new JButton("Test Signup Dialog");
        gbc.gridx = 1;
        testPanel.add(testSignupButton, gbc);
        
        // Add listeners
        testLoginButton.addActionListener(e -> {
            LoginDialog loginDialog = new LoginDialog(testFrame);
            loginDialog.setVisible(true);
            
            if (loginDialog.isLoginSuccessful()) {
                JOptionPane.showMessageDialog(testFrame, 
                    "Login successful!\nEmail: " + loginDialog.getUserEmail(), 
                    "Test Result", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        testSignupButton.addActionListener(e -> {
            SignupDialog signupDialog = new SignupDialog(testFrame);
            signupDialog.setVisible(true);
            
            if (signupDialog.isSignupSuccessful()) {
                SignupDialog.UserSignupData data = signupDialog.getSignupData();
                JOptionPane.showMessageDialog(testFrame, 
                    "Signup successful!\nEmail: " + data.email + 
                    "\nAge: " + data.age + 
                    "\nHeight: " + data.height + " cm" +
                    "\nWeight: " + data.weight + " kg" +
                    "\nGender: " + data.gender +
                    "\nGoal: " + data.goal, 
                    "Test Result", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        testFrame.add(testPanel);
        testFrame.setVisible(true);
    }
} 