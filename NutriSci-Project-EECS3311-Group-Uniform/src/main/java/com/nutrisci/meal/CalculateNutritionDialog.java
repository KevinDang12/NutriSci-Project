package com.nutrisci.meal;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.nutrisci.calculator.NutritionalCalculator;
import com.nutrisci.calculator.NutritionalData;

/**
 * Calculate the nutritional data for the selected food items
 */
public class CalculateNutritionDialog extends JDialog {
    private List<FoodItem> selectedFoodNames;
    private NutritionalCalculator nutritionalCalculator;
    private NutritionalData nutritionalData1;
    private NutritionalData nutritionalData2;

    private JLabel meal1Label;
    private JLabel meal2Label;

    private JComboBox<String> nutritionSelector;
    private JLabel displayLabel;

    /**
     * Perform the calculations for one meal
     * helped by AI
     * @param owner The owner frame
     * @param foodNames The selected food items
     */
    public CalculateNutritionDialog(Frame owner, Map<Long, FoodItem> foodNames) {
        super(owner, "Nutritional Calculator", true);
        this.selectedFoodNames = new ArrayList<>(foodNames.values());

        nutritionalCalculator = new NutritionalCalculator();
        nutritionalData1 = nutritionalCalculator.calculateMealNutrition(this.selectedFoodNames);

        setLayout(new BorderLayout(10, 10));
        setSize(450, 300);
        setLocationRelativeTo(owner);

        // Display panel
        displayLabel = new JLabel();
        displayLabel.setFont(new Font("Arial", Font.BOLD, 16));
        displayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        displayLabel.setText(updateDisplay("Calories", nutritionalData1));

        // Combo box for nutrition type
        String[] nutritionOptions = {"Calories", "Protein", "Carbohydrates", "Fats", "Fiber"};
        nutritionSelector = new JComboBox<>(nutritionOptions);
        nutritionSelector.addActionListener(e -> displayLabel.setText(updateDisplay(nutritionSelector.getSelectedItem().toString(), nutritionalData1)));

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Nutrition:"));
        topPanel.add(nutritionSelector);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(displayLabel, BorderLayout.CENTER);

        // Layout setup
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        displayLabel.setText(updateDisplay(nutritionSelector.getSelectedItem().toString(), nutritionalData1));
    }

    /**
     * Perform the calculations for two meals
     * helped by AI
     * @param owner The owner panel
     * @param foodNamesMeal1 The selected food items for meal 1
     * @param foodNamesMeal2 The selected food items for meal 2
     */
    public CalculateNutritionDialog(Frame owner, Map<Long, FoodItem> foodNamesMeal1, Map<Long, FoodItem> foodNamesMeal2) {
        super(owner, "Nutrition Calculator - Two Meals", true);
        setSize(500, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        NutritionalCalculator calculator = new NutritionalCalculator();
        nutritionalData1 = calculator.calculateMealNutrition(new ArrayList<>(foodNamesMeal1.values()));
        nutritionalData2 = calculator.calculateMealNutrition(new ArrayList<>(foodNamesMeal2.values()));

        nutritionSelector = new JComboBox<>(new String[]{"Calories", "Protein", "Carbohydrates", "Fats", "Fiber"});
        nutritionSelector.addActionListener(e -> updateNutritionalDisplay());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Nutrition:"));
        topPanel.add(nutritionSelector);
        add(topPanel, BorderLayout.NORTH);

        // Meal comparison panel
        JPanel comparisonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        comparisonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        comparisonPanel.add(new JLabel("Meal 1", SwingConstants.CENTER));
        comparisonPanel.add(new JLabel("Meal 2", SwingConstants.CENTER));

        meal1Label = new JLabel("", SwingConstants.CENTER);
        meal2Label = new JLabel("", SwingConstants.CENTER);
        meal1Label.setFont(new Font("Arial", Font.BOLD, 16));
        meal2Label.setFont(new Font("Arial", Font.BOLD, 16));

        comparisonPanel.add(meal1Label);
        comparisonPanel.add(meal2Label);

        add(comparisonPanel, BorderLayout.CENTER);

        updateNutritionalDisplay();
    }

    /**
     * Update the nutritional display data for both meals
     */
    private void updateNutritionalDisplay() {
        meal1Label.setText(updateDisplay(nutritionSelector.getSelectedItem().toString(), nutritionalData1));
        meal2Label.setText(updateDisplay(nutritionSelector.getSelectedItem().toString(), nutritionalData2));
    }

    /**
     * Update the display data for the given meal
     * @param nutritionType The nutrition type to display
     * @param data The nutritional data to retrieve the data from
     * @return The nutritional display data
     */
    private String updateDisplay(String nutritionType, NutritionalData data) {
        String text;
        switch (nutritionType) {
            case "Calories":
                text = "Calories: " + data.getCalories() + " kcal";
                break;
            case "Protein":
                text = "Protein: " + data.getProtein() + " g";
                break;
            case "Carbohydrates":
                text = "Carbohydrates: " + data.getCarbs() + " g";
                break;
            case "Fats":
                text = "Fats: " + data.getFat() + " g";
                break;
            case "Fiber":
                text = "Fiber: " + data.getFiber() + " g";
                break;
            default:
                text = "No data available.";
        }
        return text;
    }

    /**
     * Show the dialog
     */
    public void showDialog() {
        setVisible(true);
    }
}
