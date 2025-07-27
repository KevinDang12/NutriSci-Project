package com.nutrisci.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import com.nutrisci.model.GoalType;
import com.nutrisci.service.NutritionDataService;
import java.util.Map;

// Simple home/profile page panel
public class HomePagePanel extends JPanel {
    private JLabel welcomeLabel;
    private JButton addMealButton;
    private JButton goalButton;
    private JButton logoutButton;
    
    // Chart components
    private JComboBox<GoalType> nutrientComboBox;
    private JButton timeRangeButton;
    private ChartPanel chartPanel;
    private boolean showDaily = true;
    private NutritionDataService nutritionService;

    public HomePagePanel(String userNameOrEmail) {
        nutritionService = new NutritionDataService();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Welcome section
        welcomeLabel = new JLabel("Welcome, " + userNameOrEmail + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(welcomeLabel);
        add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Navigation buttons
        addMealButton = new JButton("Add Meal");
        addMealButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(addMealButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        
        goalButton = new JButton("Goal");
        goalButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(goalButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        
        logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(logoutButton);
        add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Chart section
        setupChartSection();
    }
    
    private void setupChartSection() {
        // helped by AI
        // Chart controls panel
        JPanel chartControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        chartControlsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Nutrient selection dropdown
        nutrientComboBox = new JComboBox<>(GoalType.values());
        nutrientComboBox.setSelectedItem(GoalType.CALORIES);
        chartControlsPanel.add(new JLabel("Nutrient:"));
        chartControlsPanel.add(nutrientComboBox);
        
        // Time range button
        timeRangeButton = new JButton("Daily");
        chartControlsPanel.add(timeRangeButton);
        
        add(chartControlsPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Chart panel
        chartPanel = createChartPanel();
        chartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        add(chartPanel);
        
        // Add listeners
        nutrientComboBox.addActionListener(e -> updateChart());
        timeRangeButton.addActionListener(e -> toggleTimeRange());
    }
    
    private ChartPanel createChartPanel() {
        // helped by AI
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        GoalType selectedNutrient = (GoalType) nutrientComboBox.getSelectedItem();
        
        // Get real data from database
        Map<String, Double> nutritionData;
        if (showDaily) {
            nutritionData = nutritionService.getDailyNutritionData(selectedNutrient);
        } else {
            nutritionData = nutritionService.getMonthlyNutritionData(selectedNutrient);
        }
        
        // Add data to chart
        for (Map.Entry<String, Double> entry : nutritionData.entrySet()) {
            dataset.addValue(entry.getValue(), "Intake", entry.getKey());
        }
        
        // If no data, show a message
        if (nutritionData.isEmpty()) {
            dataset.addValue(0, "Intake", "No data available");
        }
        
        String title = selectedNutrient.getDisplayName() + " Intake (" + 
                      (showDaily ? "Today" : "This Month") + ")";
        String unitLabel = nutritionService.getUnitLabel(selectedNutrient);
        
        JFreeChart chart = ChartFactory.createBarChart(
            title, 
            showDaily ? "Meal" : "Week", 
            selectedNutrient.getDisplayName() + " (" + unitLabel + ")", 
            dataset
        );
        
        return new ChartPanel(chart);
    }
    
    private void updateChart() {
        ChartPanel newChartPanel = createChartPanel();
        newChartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        newChartPanel.setPreferredSize(new Dimension(600, 300));
        
        // Replace the old chart panel
        remove(chartPanel);
        chartPanel = newChartPanel;
        add(chartPanel);
        
        revalidate();
        repaint();
    }
    
    private void toggleTimeRange() {
        showDaily = !showDaily;
        timeRangeButton.setText(showDaily ? "Daily" : "Monthly");
        updateChart();
    }

    public void setAddMealAction(ActionListener listener) {
        addMealButton.addActionListener(listener);
    }
    public void setGoalAction(ActionListener listener) {
        goalButton.addActionListener(listener);
    }
    public void setLogoutAction(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }
} 