package com.nutrisci.ui;

import javax.swing.*;
import java.awt.*;
import com.nutrisci.meal.MealLoggerPanel;
import com.nutrisci.model.Goal;
import com.nutrisci.ui.GoalDialog;
import com.nutrisci.util.UserSessionManager;
import com.nutrisci.model.User;
import java.awt.CardLayout;

// Main application class for NutriSci
public class NutriSciApp {
    private JFrame mainFrame;
    private LoginDialog loginDialog;
    private MealLoggerPanel mealLoggerPanel;
    private boolean isLoggedIn = false;
    private JButton goalButton;
    private Goal userGoal;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private HomePagePanel homePagePanel;
    private UserSessionManager userSessionManager;

    // Creates the main application
    public NutriSciApp() {
        userSessionManager = UserSessionManager.getInstance();
        setupMainFrame();
        showLogin();
    }

    // Sets up the main application frame
    private void setupMainFrame() {
        mainFrame = new JFrame("NutriSci: SwEATch to better!");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainFrame.setContentPane(mainPanel);
        // Add goal button to the top
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        goalButton = new JButton("Goal");
        topPanel.add(goalButton);
        mainFrame.add(topPanel, BorderLayout.NORTH);
    }

    // Shows the login dialog
    private void showLogin() {
        // Check if user is already logged in (e.g., from signup)
        if (userSessionManager.isUserLoggedIn()) {
            isLoggedIn = true;
            showHomePage();
            return;
        }
        
        loginDialog = new LoginDialog(mainFrame);
        loginDialog.setVisible(true);
        
        if (loginDialog.isLoginSuccessful()) {
            isLoggedIn = true;
            showHomePage();
        } else {
            // User cancelled login, exit application
            System.exit(0);
        }
    }

    private void showHomePage() {
        // Get user email from session
        String email = "";
        User currentUser = userSessionManager.getCurrentUser();
        if (currentUser != null) {
            email = currentUser.getEmail();
        } else if (loginDialog != null) {
            email = loginDialog.getUserEmail();
        }
        
        homePagePanel = new HomePagePanel(email);
        homePagePanel.setAddMealAction(e -> showMealLogger());
        homePagePanel.setGoalAction(e -> openGoalDialog());
        homePagePanel.setLogoutAction(e -> logout());
        mainPanel.add(homePagePanel, "home");
        cardLayout.show(mainPanel, "home");
        mainFrame.setVisible(true);
    }

    // Shows the main application after successful login
    private void showMainApplication() {
        mealLoggerPanel = new MealLoggerPanel();
        
        // Add it to the main frame
        // Add 'Back to Home' button
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> showHomePage());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        JPanel mealLoggerContainer = new JPanel(new BorderLayout());
        mealLoggerContainer.add(topPanel, BorderLayout.NORTH);
        mealLoggerContainer.add(mealLoggerPanel, BorderLayout.CENTER);
        mainPanel.add(mealLoggerContainer, "meal");
        cardLayout.show(mainPanel, "meal");
        
        // Show the main frame
        mainFrame.setVisible(true);
        
        // Show welcome message
        String email = loginDialog.getUserEmail();
        JOptionPane.showMessageDialog(mainFrame, 
            "Welcome to NutriSci, " + email + "!\nYou can now start logging your meals.", 
            "Welcome", 
            JOptionPane.INFORMATION_MESSAGE);
        // Set up goal button action
        goalButton.addActionListener(e -> openGoalDialog());
    }

    private void openGoalDialog() {
        // Get the current user's goal from the session
        User currentUser = userSessionManager.getCurrentUser();
        if (currentUser != null) {
            userGoal = currentUser.getGoal();
        }
        
        GoalDialog dialog = new GoalDialog(mainFrame, userGoal);
        dialog.setVisible(true);
        if (dialog.isGoalUpdated()) {
            userGoal = dialog.getGoal();
            
            // Update the user's goal in the session and database
            if (currentUser != null) {
                currentUser.setGoal(userGoal);
                userSessionManager.updateUserProfile(currentUser);
            }
            
            JOptionPane.showMessageDialog(mainFrame, "Goal updated to: " + userGoal, "Goal Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showMealLogger() {
        mealLoggerPanel = new MealLoggerPanel();
        // Add 'Back to Home' button
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> showHomePage());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        JPanel mealLoggerContainer = new JPanel(new BorderLayout());
        mealLoggerContainer.add(topPanel, BorderLayout.NORTH);
        mealLoggerContainer.add(mealLoggerPanel, BorderLayout.CENTER);
        mainPanel.add(mealLoggerContainer, "meal");
        cardLayout.show(mainPanel, "meal");
        mainFrame.setVisible(true);
    }

    private void logout() {
        isLoggedIn = false;
        userSessionManager.logout();
        mainPanel.removeAll();
        showLogin();
    }

    // Main method to start the application
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new NutriSciApp();
            }
        });
    }
} 