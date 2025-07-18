package com.nutrisci.meal;

import com.nutrisci.database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MealLoggerGUI extends JPanel {
    private JComboBox<MealType> mealTypeComboBox;
    private JPanel foodItemsPanel;
    private JButton addFoodItemButton;
    private JButton logMealButton;

    private JButton compareMealButton;
    private JButton swapMealButton;
    private JButton calculateButton;

    private DatabaseManager db;

    // private List<String> foodNames;
    // private List<String> selectedFoodNames;

    private Map<Long, String> foodNames;
    private Map<Long, String> selectedFoodNames;

    // Add right panel state
    private Map<Long, String> comparedFoodNames;
    private Map<Long, String> comparedSelectedFoodNames;

    private JPanel bottomPanel;
    private JScrollPane scrollPane;
    private MealBuilder meal;

    public MealLoggerGUI() {
        setLayout(new BorderLayout(10, 10));
        foodNames = fetchFoodNames();
        selectedFoodNames = new HashMap<>();
        comparedFoodNames = new HashMap<>(foodNames);
        comparedSelectedFoodNames = new HashMap<>();

        // Top panel for meal type
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Meal Type:"));
        mealTypeComboBox = new JComboBox<>(MealType.values());
        mealTypeComboBox.getModel().setSelectedItem(MealType.SNACK);
        meal = new MealBuilder();

        // Add listener for meal type changes
        mealTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                MealType selectedType = (MealType) e.getItem();
                MealBuilder newBuilder = new MealBuilder().setMealType(selectedType);
                if (meal.mealBeingBuilt != null) {
                    for (FoodItem item : meal.mealBeingBuilt.getFoodItems()) {
                        // Use the current servingSize as the quantity
                        newBuilder.addFoodItem(item);
                    }
                }
                meal = newBuilder;
            }
        });

        topPanel.add(mealTypeComboBox);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for food items
        foodItemsPanel = new JPanel();
        foodItemsPanel.setLayout(new BoxLayout(foodItemsPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(foodItemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for add/log buttons
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addFoodItemButton = new JButton("Add Food Item");
        addFoodItemButton.addActionListener(e -> addFoodItemSelector());

        logMealButton = new JButton("Log Meal");
        calculateButton = new JButton("Calculate...");
        logMealButton.addActionListener(e -> logMeal()); // Implement as needed

        compareMealButton = new JButton("Compare Meal");
        swapMealButton = new JButton("Swap Meal");

        bottomPanel.add(addFoodItemButton);
        bottomPanel.add(calculateButton);
        bottomPanel.add(compareMealButton);
        bottomPanel.add(swapMealButton);
        bottomPanel.add(logMealButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Compare Meal Button Action
        compareMealButton.addActionListener(e -> showComparePanels());
    }

    /**
     * TODO
     * Log Meal:
     * Log meal when comparing
     * Compare food items
     * Swap food items
     * DB for user
     */
    private void logMeal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logMeal'");
    }

    private Map<Long, String> fetchFoodNames() {
        Map<Long, String> foodNames = new HashMap<>();
        try {
            db = DatabaseManager.getInstance();
            foodNames = db.getFoodItems();

        } catch (Exception e) {
            System.err.println("Error getting food items.");
        }
        return foodNames;
    }

    /**
     * Open a new window instead that allows the user to search for a food item and add it.
     * They can click edit to change the item and swap them around.
     * Sort the food names, swap the food names when editing
     */
    private void addFoodItemSelector() {
        FoodSearchDialog dialog = new FoodSearchDialog((Frame) SwingUtilities.getWindowAncestor(this), foodNames);
        Long id = dialog.showDialog();
        if (id != null) {
            addFoodItemLabel(id);
        }
    }

    public Long getSelectedFoodId(String selectedFood) {
        if (selectedFood == null) return null;
        for (Map.Entry<Long, String> entry : foodNames.entrySet()) {
            if (entry.getValue().equals(selectedFood)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void addFoodItemLabel(Long id) {

        String foodName = foodNames.get(id);

        JPanel itemPanel = new JPanel(new BorderLayout());
        JLabel foodLabel = new JLabel(foodName);
        foodLabel.setName(id.toString());
        JButton editButton = new JButton("Edit");
        JButton removeButton = new JButton("X");

        selectedFoodNames.put(id, foodName);
        foodNames.remove(id);

        editButton.addActionListener(e -> {
            FoodSearchDialog dialog = new FoodSearchDialog((Frame) SwingUtilities.getWindowAncestor(this), foodNames);
            Long newId = dialog.showDialog();
            String newFood = foodNames.get(newId);
            if (newId != null) {
                String oldFood = (String) foodLabel.getText();
                Long oldId = Long.parseLong(foodLabel.getName());

                selectedFoodNames.remove(oldId);
                foodNames.put(oldId, oldFood);

                selectedFoodNames.put(newId, newFood);
                foodNames.remove(newId);

                foodLabel.setText(newFood);
                // foodNames.sort(String::compareToIgnoreCase); // Ensure foodNames is always sorted
            }
        });

        removeButton.addActionListener(e -> {
            String foodItem = (String) foodLabel.getText();
            Long oldId = Long.parseLong(foodLabel.getName());
            selectedFoodNames.remove(oldId);
            foodNames.put(oldId, foodItem);
            // foodNames.sort(String::compareToIgnoreCase); // Ensure foodNames is always sorted
            foodItemsPanel.remove(itemPanel);
            foodItemsPanel.revalidate();
            foodItemsPanel.repaint();
        });

        // Panel for right-aligned buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(8, 2))); // Add spacing between buttons
        buttonPanel.add(removeButton);

        itemPanel.add(foodLabel, BorderLayout.WEST);
        itemPanel.add(buttonPanel, BorderLayout.EAST);

        // Remove extra vertical space between rows
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, foodLabel.getPreferredSize().height + 8));

        foodItemsPanel.add(itemPanel);
        foodItemsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        foodItemsPanel.revalidate();
        foodItemsPanel.repaint();
    }

    private void showComparePanels() {
        this.remove(scrollPane);
        // Remove all buttons from bottomPanel except logMealButton and calculateButton
        bottomPanel.removeAll();
        bottomPanel.add(calculateButton);
        bottomPanel.add(logMealButton);
        bottomPanel.revalidate();
        bottomPanel.repaint();

        // Create left panel (original)
        JPanel leftContainer = new JPanel();
        leftContainer.setLayout(new BorderLayout());
        JScrollPane leftScroll = new JScrollPane(foodItemsPanel);
        leftContainer.add(createPanelButtonBar(foodItemsPanel, true, foodNames, selectedFoodNames), BorderLayout.NORTH);
        leftContainer.add(leftScroll, BorderLayout.CENTER);

        // Create right panel (copy)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JPanel rightContainer = new JPanel();
        rightContainer.setLayout(new BorderLayout());
        JScrollPane rightScroll = new JScrollPane(rightPanel);
        rightContainer.add(createPanelButtonBar(rightPanel, false, comparedFoodNames, comparedSelectedFoodNames), BorderLayout.NORTH);
        rightContainer.add(rightScroll, BorderLayout.CENTER);

        JPanel comparePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        comparePanel.add(leftContainer);
        comparePanel.add(rightContainer);

        this.add(comparePanel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private JPanel createPanelButtonBar(JPanel panelRef, boolean isLeft, Map<Long, String> foodNamesForPanel, Map<Long, String> selectedFoodNamesForPanel) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn = new JButton("Add Food Item");
        JButton swapBtn = new JButton("Swap Meal");
        bar.add(addBtn);
        bar.add(swapBtn);
        if (isLeft) {
            addBtn.addActionListener(e -> addFoodItemSelector());
            // You can wire up swapBtn for the left panel as needed
        } else {
            addBtn.addActionListener(e -> addFoodItemSelectorToPanel(panelRef, foodNamesForPanel, selectedFoodNamesForPanel));
            // You can wire up swapBtn for the right panel as needed
        }
        return bar;
    }

    // // Helper for right panel: open FoodSearchDialog and add food item to the given panel
    private void addFoodItemSelectorToPanel(JPanel panel, Map<Long, String> foodNamesForPanel, Map<Long, String> selectedFoodNamesForPanel) {
        FoodSearchDialog dialog = new FoodSearchDialog((Frame) SwingUtilities.getWindowAncestor(this), foodNamesForPanel);
        Long id = dialog.showDialog();
        String selectedFood = foodNamesForPanel.get(id);
        if (id != null) {
            addFoodItemLabelToPanel(panel, id, selectedFood, foodNamesForPanel, selectedFoodNamesForPanel);
            foodNamesForPanel.remove(id);
            selectedFoodNamesForPanel.put(id, selectedFood);
        }
    }

    // Helper for both panels: add a food item label to a given panel, with full edit/remove support
    private void addFoodItemLabelToPanel(JPanel panel, Long id, String foodName, Map<Long, String> foodNamesForPanel, Map<Long, String> selectedFoodNamesForPanel) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        JLabel foodLabel = new JLabel(foodName);
        foodLabel.setName(id.toString());
        JButton editButton = new JButton("Edit");
        JButton removeButton = new JButton("X");

        selectedFoodNamesForPanel.put(id, foodName);
        foodNamesForPanel.remove(id);

        editButton.addActionListener(e -> {
            FoodSearchDialog dialog = new FoodSearchDialog((Frame) SwingUtilities.getWindowAncestor(this), foodNamesForPanel);
            Long newId = dialog.showDialog();
            String newFood = foodNamesForPanel.get(newId);

            if (newId != null) {
                String oldFood = (String) foodLabel.getText();
                Long oldId = Long.parseLong(foodLabel.getName());

                selectedFoodNamesForPanel.remove(oldId);
                foodNamesForPanel.put(oldId, oldFood);

                selectedFoodNamesForPanel.put(newId, newFood);
                foodNamesForPanel.remove(newId);

                foodLabel.setText(newFood);
            }
        });
        
        removeButton.addActionListener(e -> {
            String foodItem = (String) foodLabel.getText();
            Long oldId = Long.parseLong(foodLabel.getName());
            selectedFoodNamesForPanel.remove(oldId);
            foodNamesForPanel.put(oldId, foodItem);
            panel.remove(itemPanel);
            panel.revalidate();
            panel.repaint();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(8, 2)));
        buttonPanel.add(removeButton);
        itemPanel.add(foodLabel, BorderLayout.WEST);
        itemPanel.add(buttonPanel, BorderLayout.EAST);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, foodLabel.getPreferredSize().height + 8));
        panel.add(itemPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.revalidate();
        panel.repaint();
    }
} 