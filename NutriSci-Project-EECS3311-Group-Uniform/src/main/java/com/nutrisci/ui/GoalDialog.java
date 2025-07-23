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
    // Simple progress chart using JFreeChart
    private ChartPanel createProgressChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // For demo: just use random progress (replace with real progress logic)
        double progress = Math.random() * 100;
        dataset.addValue(progress, "Progress", "Current");
        dataset.addValue(100, "Progress", "Goal");
        JFreeChart chart = ChartFactory.createBarChart(
                "Goal Progress", "", "% Achieved", dataset);
        return new ChartPanel(chart);
    }
    public boolean isGoalUpdated() { return goalUpdated; }
    public Goal getGoal() { return goal; }
} 