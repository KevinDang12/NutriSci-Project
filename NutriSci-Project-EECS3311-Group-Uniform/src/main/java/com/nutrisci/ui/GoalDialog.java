package com.nutrisci.ui;

import com.nutrisci.model.Goal;
import com.nutrisci.model.GoalType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

// Dialog for viewing and updating the user's goal
public class GoalDialog extends JDialog {
    private JComboBox<GoalType> goalTypeComboBox;
    private JComboBox<String> directionComboBox;
    private JComboBox<Integer> percentComboBox;
    private JButton saveButton;
    private boolean goalUpdated = false;
    private Goal goal;

    public GoalDialog(Frame parent, Goal currentGoal) {
        super(parent, "Your Goal", true);
        this.goal = currentGoal;
        setupUI();
        setupLayout();
        pack();
        setLocationRelativeTo(parent);
    }

    private void setupUI() {
        goalTypeComboBox = new JComboBox<>(GoalType.values());
        directionComboBox = new JComboBox<>(new String[]{"Increase", "Decrease"});
        percentComboBox = new JComboBox<>(new Integer[]{5, 10, 15});
        saveButton = new JButton("Save Goal");
        if (goal != null) {
            goalTypeComboBox.setSelectedItem(goal.getType());
            directionComboBox.setSelectedItem(goal.isIncrease() ? "Increase" : "Decrease");
            percentComboBox.setSelectedItem(goal.getPercent());
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Goal Type:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(goalTypeComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Direction:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(directionComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Percent:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(percentComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(saveButton, gbc);
        add(mainPanel, BorderLayout.NORTH);
        // Progress chart
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Progress"));
        chartPanel.add(createProgressChart(), BorderLayout.CENTER);
        add(chartPanel, BorderLayout.CENTER);
        // Save button action
        saveButton.addActionListener(e -> {
            GoalType type = (GoalType) goalTypeComboBox.getSelectedItem();
            boolean increase = directionComboBox.getSelectedItem().equals("Increase");
            int percent = (Integer) percentComboBox.getSelectedItem();
            goal = new Goal(type, increase, percent);
            goalUpdated = true;
            dispose();
        });
    }
    // Progress chart using JFreeChart with real user data
    private ChartPanel createProgressChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get real progress data for the current user
        double currentProgress = calculateUserProgress();
        double goalTarget = 100.0; // Goal is always 100%
        
        dataset.addValue(currentProgress, "Progress", "Current");
        dataset.addValue(goalTarget, "Progress", "Goal");
        
        JFreeChart chart = ChartFactory.createBarChart(
                "Goal Progress", "", "% Achieved", dataset);
        return new ChartPanel(chart);
    }
    
    /**
     * Calculate the user's actual progress towards their goal
     * @return Progress percentage (0-100)
     */
    private double calculateUserProgress() {
        if (goal == null) {
            return 0.0;
        }
        
        try {
            // Get current user's meal data for today
            com.nutrisci.util.UserSessionManager sessionManager = com.nutrisci.util.UserSessionManager.getInstance();
            com.nutrisci.model.User currentUser = sessionManager.getCurrentUser();
            
            if (currentUser == null) {
                return 0.0;
            }
            
            // Get today's meals and calculate nutrition
            com.nutrisci.meal.MealManager mealManager = new com.nutrisci.meal.MealManager();
            java.time.LocalDate today = java.time.LocalDate.now();
            java.util.List<com.nutrisci.meal.Meal> todaysMeals = mealManager.getMealsForDate(today);
            
            if (todaysMeals.isEmpty()) {
                return 0.0; // No meals logged today
            }
            
            // Calculate total nutrition for today
            com.nutrisci.calculator.NutritionalCalculator calculator = new com.nutrisci.calculator.NutritionalCalculator();
            com.nutrisci.calculator.NutritionalData totalNutrition = new com.nutrisci.calculator.NutritionalData(0, 0, 0, 0, 0);
            
            for (com.nutrisci.meal.Meal meal : todaysMeals) {
                totalNutrition.add(calculator.calculateMealNutrition(meal.getFoodItems()));
            }
            
            // Calculate progress based on goal type
            double currentValue = 0.0;
            double targetValue = 0.0;
            
            switch (goal.getType()) {
                case CALORIES:
                    currentValue = totalNutrition.getCalories();
                    // For demo purposes, assume target is 2000 calories
                    targetValue = 2000.0;
                    break;
                case PROTEIN:
                    currentValue = totalNutrition.getProtein();
                    // For demo purposes, assume target is 50g protein
                    targetValue = 50.0;
                    break;
                case FIBRE:
                    currentValue = totalNutrition.getFiber();
                    // For demo purposes, assume target is 25g fiber
                    targetValue = 25.0;
                    break;
                default:
                    return 0.0;
            }
            
            if (targetValue == 0) {
                return 0.0;
            }
            
            double progress = (currentValue / targetValue) * 100.0;
            
            // Apply goal direction (increase/decrease)
            if (goal.isIncrease()) {
                // For increase goals, progress is how much we've achieved
                return Math.min(progress, 100.0);
            } else {
                // For decrease goals, progress is inverse (less is better)
                // If we're at 80% of target, that's 20% progress towards decrease goal
                return Math.max(0.0, 100.0 - progress);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    public boolean isGoalUpdated() { return goalUpdated; }
    public Goal getGoal() { return goal; }
} 