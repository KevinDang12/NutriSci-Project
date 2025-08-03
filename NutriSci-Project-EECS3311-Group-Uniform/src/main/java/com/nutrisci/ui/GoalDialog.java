package com.nutrisci.ui;

import com.nutrisci.model.Goal;
import com.nutrisci.model.GoalType;
import com.nutrisci.model.User;
import com.nutrisci.util.UserSessionManager;
import com.nutrisci.calculator.*;
import com.nutrisci.meal.MealManager;
import com.nutrisci.meal.Meal;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

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
        // helped by AI
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
    // helped by AI
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
     * Calculate the total nutrition of each meal
     * @return The total nutritional data
     */
    private NutritionalData calculateTotalNutrition() {
        MealManager mealManager = new MealManager();
        LocalDate today = LocalDate.now();
        List<Meal> todaysMeals = mealManager.getMealsForDate(today);
        
        if (todaysMeals.isEmpty()) {
            return null; // No meals logged today
        }
        
        // Calculate total nutrition for today
        NutritionalCalculator calculator = new NutritionalCalculator();
        NutritionalData totalNutrition = new NutritionalData(0, 0, 0, 0, 0);

        for (Meal meal : todaysMeals) {
            totalNutrition = totalNutrition.add(calculator.calculateMealNutrition(meal.getFoodItems()));
        }
        
        return totalNutrition;
    }

    private double calculateProgress(NutritionalData totalNutrition) {
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
        return progress;
    }

    /**
     * Calculates the user's current progress towards their goal
     * helped by AI
     */
    private double calculateUserProgress() {
        if (goal == null) {
            return 0.0;
        }
        
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        User currentUser = sessionManager.getCurrentUser();

        try {
            // Get current user's meal data for today            
            if (currentUser == null) {
                return 0.0;
            }
            
            // Get today's meals and calculate nutrition
            NutritionalData totalNutrition = calculateTotalNutrition();
            if (totalNutrition == null) {
                return 0.0;
            }
            
            // Calculate progress based on goal type
            double progress = calculateProgress(totalNutrition);
            
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