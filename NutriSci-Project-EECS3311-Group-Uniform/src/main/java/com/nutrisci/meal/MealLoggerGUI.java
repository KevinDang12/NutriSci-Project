package com.nutrisci.meal;

import javax.swing.*;

import com.nutrisci.cnf.CNFDataAdapter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MealLoggerGUI extends JPanel {
    private JComboBox<MealType> mealTypeComboBox;
    private JPanel foodItemsPanel;
    private JButton addFoodItemButton;
    private JButton logMealButton;
    private List<JComboBox<String>> foodItemComboBoxes;
    private List<JButton> removeButtons;
    private List<String> foodNames;
    private Set<String> selectedFoodNames;

    public MealLoggerGUI() {
        setLayout(new BorderLayout(10, 10));
        foodItemComboBoxes = new ArrayList<>();
        removeButtons = new ArrayList<>();
        foodNames = fetchFoodNames();

        // Top panel for meal type
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Meal Type:"));
        mealTypeComboBox = new JComboBox<>(MealType.values());
        topPanel.add(mealTypeComboBox);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for food items
        foodItemsPanel = new JPanel();
        foodItemsPanel.setLayout(new BoxLayout(foodItemsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(foodItemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Add first food item selector by default
        addFoodItemSelector();

        // Bottom panel for add/log buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addFoodItemButton = new JButton("Add Food Item");
        addFoodItemButton.addActionListener(e -> addFoodItemSelector());
        bottomPanel.add(addFoodItemButton);

        // Log Meal
        logMealButton = new JButton("Log Meal");
        // logMealButton.addActionListener(e -> logMeal()); // Implement as needed
        bottomPanel.add(logMealButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private List<String> fetchFoodNames() {
        List<String> names = new ArrayList<>();
        try {
            String currentPath = System.getProperty("user.dir");
            System.out.println("Current path: " + currentPath);
            List<Map<String, String>> data = CNFDataAdapter.importCSV("FOOD NAME.csv");
            
            for (Map<String, String> item : data) {
                String desc = item.get("FoodDescription");
                names.add(desc);
            }
        } catch (Exception e) {
            names.add("Error loading food items");
        }
        return names;
    }

    private void addFoodItemSelector() {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    
        // Find currently selected items
        Set<String> selected = new HashSet<>();
        for (JComboBox<String> box : foodItemComboBoxes) {
            String sel = (String) box.getSelectedItem();
            if (sel != null) selected.add(sel);
        }
    
        // Find the first available (unselected) food name
        String initialSelection = null;
        for (String name : foodNames) {
            if (!selected.contains(name)) {
                initialSelection = name;
                break;
            }
        }
    
        // Fallback if all items are taken
        if (initialSelection == null && !foodNames.isEmpty()) {
            initialSelection = foodNames.get(0);
        }
    
        // Create combo box with temporary placeholder — real items will be added in updateAllFoodComboBoxes()
        JComboBox<String> foodComboBox = new JComboBox<>();
        foodComboBox.setPreferredSize(new Dimension(400, 25));
        foodComboBox.addActionListener(e -> updateAllFoodComboBoxes());
    
        JButton removeButton = new JButton("X");
        removeButton.addActionListener(e -> removeFoodItemSelector(itemPanel, foodComboBox, removeButton));
    
        itemPanel.add(new JLabel("Food Item:"));
        itemPanel.add(foodComboBox);
        itemPanel.add(removeButton);
    
        foodItemsPanel.add(itemPanel);
        foodItemComboBoxes.add(foodComboBox);
        removeButtons.add(removeButton);
    
        updateAllFoodComboBoxes(); // Populate all boxes
    
        // Set initial selection for the new box
        if (initialSelection != null) {
            foodComboBox.setSelectedItem(initialSelection);
        }
    
        foodItemsPanel.revalidate();
        foodItemsPanel.repaint();
    }

    private void updateAllFoodComboBoxes() {
        // Collect all currently selected food names
        Set<String> selected = new HashSet<>();
        for (JComboBox<String> box : foodItemComboBoxes) {
            String sel = (String) box.getSelectedItem();
            if (sel != null) selected.add(sel);
        }
    
        // Update each combo box
        for (JComboBox<String> box : foodItemComboBoxes) {
            String currentSelection = (String) box.getSelectedItem();
    
            box.removeAllItems();
            for (String name : foodNames) {
                if (!selected.contains(name) || name.equals(currentSelection)) {
                    box.addItem(name);
                }
            }
    
            // Restore selection
            box.setSelectedItem(currentSelection);
        }
    }    

    private void removeFoodItemSelector(JPanel itemPanel, JComboBox<String> comboBox, JButton removeButton) {
        foodItemsPanel.remove(itemPanel);
        foodItemComboBoxes.remove(comboBox);
        removeButtons.remove(removeButton);
        foodItemsPanel.revalidate();
        foodItemsPanel.repaint();
    }

    // Optionally, provide a method to get the selected meal data
    public MealType getSelectedMealType() {
        return (MealType) mealTypeComboBox.getSelectedItem();
    }

    public List<String> getSelectedFoodNames() {
        List<String> selected = new ArrayList<>();
        for (JComboBox<String> comboBox : foodItemComboBoxes) {
            String name = (String) comboBox.getSelectedItem();
            if (name != null) selected.add(name);
        }
        return selected;
    }
} 