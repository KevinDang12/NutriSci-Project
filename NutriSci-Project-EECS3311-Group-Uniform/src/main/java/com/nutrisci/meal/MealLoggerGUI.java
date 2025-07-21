package com.nutrisci.meal;

import com.nutrisci.database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class MealLoggerGUI extends JPanel {
    private JComboBox<MealType> mealTypeComboBox;
    private JPanel foodItemsPanel;
    private JButton addFoodItemButton;
    private JButton logMealButton;
    private JButton importButton;

    private JButton compareMealButton;
    private JButton swapMealButton;
    private JButton calculateButton;

    private DatabaseManager db;

    private final Map<Long, String> listOfFoodNames;
    private Map<Long, String> foodNames;
    private Map<Long, FoodItem> selectedFoodNames;

    // Add right panel state
    private Map<Long, String> comparedFoodNames;
    private Map<Long, FoodItem> comparedSelectedFoodNames;
    private long userID = 0;

    private JPanel bottomPanel;
    private JScrollPane scrollPane;
    private MealBuilder meal1;
    private MealBuilder meal2;

    public MealLoggerGUI() {
        setLayout(new BorderLayout(10, 10));
        listOfFoodNames = fetchFoodNames();
        foodNames = new HashMap<>(listOfFoodNames);
        selectedFoodNames = new HashMap<>();
        comparedFoodNames = new HashMap<>(listOfFoodNames);
        comparedSelectedFoodNames = new HashMap<>();

        // Top panel for meal type
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Meal Type:"));
        List<MealType> exitingMealTypes = db.getAvailableMealTypes(userID, LocalDate.now());

        mealTypeComboBox = new JComboBox<>();

        for (MealType type : MealType.values()) {
            if (!exitingMealTypes.contains(type) || type.equals(MealType.SNACK)) {
                mealTypeComboBox.addItem(type);
            }
        }
        
        // mealTypeComboBox.getModel().setSelectedItem(MealType.SNACK);
        MealType mealType = mealTypeComboBox.getItemAt(0);
        meal1 = new MealBuilder().setMealType(mealType);
        meal2 = new MealBuilder().setMealType(mealType);

        db = DatabaseManager.getInstance();

        // Add listener for meal type changes
        mealTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                MealType type = (MealType) e.getItem();
                MealBuilder newBuilder1 = new MealBuilder().setMealType(type);

                if (meal1.mealBeingBuilt != null) {
                    for (FoodItem item : meal1.mealBeingBuilt.getFoodItems()) {
                        // Use the current servingSize as the quantity
                        FoodItem newItem = new FoodItem(item.getId(), item.description, item.nutrients, item.foodGroup);
                        newBuilder1.addFoodItem(newItem);
                    }
                    meal1 = newBuilder1;
                }

                MealBuilder newBuilder2 = new MealBuilder().setMealType(type);
                if (meal2.mealBeingBuilt != null) {
                    for (FoodItem item : meal2.mealBeingBuilt.getFoodItems()) {
                        // Use the current servingSize as the quantity
                        FoodItem newItem = new FoodItem(item.getId(), item.description, item.nutrients, item.foodGroup);
                        newBuilder2.addFoodItem(newItem);
                    }
                    meal2 = newBuilder2;
                }
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
        addFoodItemButton.addActionListener(e -> addFoodItemSelectorToPanel(foodItemsPanel, foodNames, selectedFoodNames));

        logMealButton = new JButton("Log Meal");
        logMealButton.addActionListener(e -> logMeal());

        calculateButton = new JButton("Calculate...");
        calculateButton.addActionListener(e -> calculateMealNutrition(false));

        compareMealButton = new JButton("Compare Meal");
        swapMealButton = new JButton("Swap Meal");

        importButton = new JButton("Import Meal");
        importButton.addActionListener(e -> importMeal(foodItemsPanel, foodNames, selectedFoodNames));

        bottomPanel.add(importButton);
        bottomPanel.add(addFoodItemButton);
        bottomPanel.add(calculateButton);
        bottomPanel.add(compareMealButton);
        bottomPanel.add(swapMealButton);
        bottomPanel.add(logMealButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Compare Meal Button Action
        compareMealButton.addActionListener(e -> showComparePanels());
    }

    private void calculateMealNutrition(boolean compare) {
        if (!compare) {
            CalculateNutritionDialog dialog = new CalculateNutritionDialog((Frame) SwingUtilities.getWindowAncestor(this), selectedFoodNames);
            dialog.showDialog();
        } else {
            CalculateNutritionDialog dialog = new CalculateNutritionDialog((Frame) SwingUtilities.getWindowAncestor(this), selectedFoodNames, comparedSelectedFoodNames);
            dialog.showDialog();
        }
    }

    private void importMeal(JPanel panel, Map<Long, String> foodNamesForPanel, Map<Long, FoodItem> selectedFoodNamesForPanel) {
        MealImportDialog dialog = new MealImportDialog((Frame) SwingUtilities.getWindowAncestor(this));
        Long id = dialog.showDialog();

        if (id != null) {
            List<Long> foodIds = db.importMeal(id);

            selectedFoodNamesForPanel.clear();
            foodNamesForPanel.clear();
            foodNamesForPanel.putAll(listOfFoodNames);

            panel.removeAll();

            for (long foodId : foodIds) {
                String foodName = foodNamesForPanel.get(foodId);
                System.out.println(foodName);
                addFoodItemLabelToPanel(panel, foodId, foodName, foodNamesForPanel, selectedFoodNamesForPanel);
            }
        }
    }

    /**
     * TODO
     * Log Meal ^
     * Log meal when comparing ^
     * Compare food items ^
     * Calculate
     * Swap food items
     * DB for user ^
     * User DB ^
     * Authenticate user^
     */
    private void logMeal() {
        if (comparedSelectedFoodNames.size() <= 0 && selectedFoodNames.size() <= 0) {
            JDialog dialog = new JDialog((Frame) null, "Unable to add Meal", true);
            dialog.setSize(300, 100);
            dialog.setLocationRelativeTo(null);
            dialog.setLayout(new BorderLayout());
            
            JLabel label = new JLabel("You need to add at least 1 food item", SwingConstants.CENTER);
            dialog.add(label, BorderLayout.NORTH);
            dialog.setVisible(true);

            return;

        } else if (comparedSelectedFoodNames.size() <= 0) {
            List<FoodItem> foodItems = new ArrayList<>(selectedFoodNames.values());
            meal1.setFoodItems(foodItems);
            System.out.println(meal1.mealBeingBuilt.foodItems);
        
            db.saveMeal(meal1.build(), userID);

        } else if (selectedFoodNames.size() <= 0) {
            List<FoodItem> foodItems = new ArrayList<>(comparedSelectedFoodNames.values());
            meal2.setFoodItems(foodItems);
            System.out.println(meal2.mealBeingBuilt.foodItems);

            db.saveMeal(meal2.build(), userID);

        } else {
            openMealSelectionDialog();
            return;
        }
        
        resetPanel();
    }

    private void resetPanel() {
        meal1 = new MealBuilder().setMealType(MealType.SNACK);
        meal1.clearFoodItems();

        for (long key : selectedFoodNames.keySet()) {
            String descString = selectedFoodNames.get(key).description;
            foodNames.put(key, descString);
        }

        selectedFoodNames.clear();

        for (long key : comparedSelectedFoodNames.keySet()) {
            String descString = comparedSelectedFoodNames.get(key).description;
            comparedFoodNames.put(key, descString);
        }

        comparedSelectedFoodNames.clear();

        this.removeAll(); // Remove all current components

        List<MealType> exitingMealTypes = db.getAvailableMealTypes(userID, LocalDate.now());

        mealTypeComboBox = new JComboBox<>();

        for (MealType type : MealType.values()) {
            if (!exitingMealTypes.contains(type) || type.equals(MealType.SNACK)) {
                mealTypeComboBox.addItem(type);
            }
        }

        MealType mealType = mealTypeComboBox.getItemAt(0);
        meal1 = new MealBuilder().setMealType(mealType);
        meal2 = new MealBuilder().setMealType(mealType);

        // Rebuild top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Meal Type:"));
        topPanel.add(mealTypeComboBox);
        this.add(topPanel, BorderLayout.NORTH);

        // Reset center panel
        foodItemsPanel = new JPanel();
        foodItemsPanel.setLayout(new BoxLayout(foodItemsPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(foodItemsPanel);
        this.add(scrollPane, BorderLayout.CENTER);

        for (ActionListener al : calculateButton.getActionListeners()) {
            calculateButton.removeActionListener(al);
        }
        calculateButton.addActionListener(e -> calculateMealNutrition(false));

        // Reset bottom panel
        bottomPanel.removeAll();
        bottomPanel.add(importButton);
        bottomPanel.add(addFoodItemButton);
        bottomPanel.add(calculateButton);
        bottomPanel.add(compareMealButton);
        bottomPanel.add(swapMealButton);
        bottomPanel.add(logMealButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.revalidate();
        this.repaint();
    }

    private void openMealSelectionDialog() {
        JDialog dialog = new JDialog((Frame) null, "Select Meal", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());
    
        JLabel label = new JLabel("Which meal do you want to save?", SwingConstants.CENTER);
        dialog.add(label, BorderLayout.NORTH);
    
        JButton meal1Button = new JButton("Meal 1");
        JButton meal2Button = new JButton("Meal 2");
    
        meal1Button.addActionListener(e -> {
            List<FoodItem> foodItems = new ArrayList<>(selectedFoodNames.values());
            meal1.setFoodItems(foodItems);
            db.saveMeal(meal1.build(), userID);
            dialog.dispose();
        });
    
        meal2Button.addActionListener(e -> {
            List<FoodItem> foodItems = new ArrayList<>(comparedSelectedFoodNames.values());
            meal1.setFoodItems(foodItems);
            db.saveMeal(meal1.build(), userID);
            dialog.dispose();
        });
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(meal1Button);
        buttonPanel.add(meal2Button);
    
        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
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

        FoodItem newItem = db.loadFoodItem(id);
        selectedFoodNames.put(id, newItem);
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

                FoodItem newFoodItem = db.loadFoodItem(newId);
                selectedFoodNames.put(newId, newFoodItem);
                foodNames.remove(newId);

                foodLabel.setText(newFood);
            }
        });

        removeButton.addActionListener(e -> {
            String foodItem = (String) foodLabel.getText();
            Long oldId = Long.parseLong(foodLabel.getName());
            selectedFoodNames.remove(oldId);
            foodNames.put(oldId, foodItem);
            foodItemsPanel.remove(itemPanel);
            foodItemsPanel.revalidate();
            foodItemsPanel.repaint();
        });

        // Panel for right-aligned buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(8, 2)));
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
        for (ActionListener al : calculateButton.getActionListeners()) {
            calculateButton.removeActionListener(al);
        }
        calculateButton.addActionListener(e -> calculateMealNutrition(true));

        bottomPanel.add(calculateButton);
        bottomPanel.add(logMealButton);
        bottomPanel.revalidate();
        bottomPanel.repaint();

        // Create left panel (original)
        JPanel leftContainer = new JPanel();
        leftContainer.setLayout(new BorderLayout());
        JScrollPane leftScroll = new JScrollPane(foodItemsPanel);
        foodItemsPanel.setName("Meal 1");
        leftContainer.add(createPanelButtonBar(foodItemsPanel, true, foodNames, selectedFoodNames), BorderLayout.NORTH);
        leftContainer.add(leftScroll, BorderLayout.CENTER);

        // Create right panel (copy)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JPanel rightContainer = new JPanel();
        rightContainer.setLayout(new BorderLayout());
        JScrollPane rightScroll = new JScrollPane(rightPanel);
        rightPanel.setName("Meal 2");
        rightContainer.add(createPanelButtonBar(rightPanel, false, comparedFoodNames, comparedSelectedFoodNames), BorderLayout.NORTH);
        rightContainer.add(rightScroll, BorderLayout.CENTER);

        JPanel comparePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        comparePanel.add(leftContainer);
        comparePanel.add(rightContainer);

        this.add(comparePanel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private JPanel createPanelButtonBar(JPanel panelRef, boolean isLeft, Map<Long, String> foodNamesForPanel, Map<Long, FoodItem> selectedFoodNamesForPanel) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton importBtn = new JButton("Import Meal");
        JButton addBtn = new JButton("Add Food Item");
        JButton swapBtn = new JButton("Swap Meal");
        bar.add(importBtn);
        bar.add(addBtn);
        bar.add(swapBtn);
        if (isLeft) {
            importBtn.addActionListener(e -> importMeal(foodItemsPanel, foodNames, selectedFoodNames));
            addBtn.addActionListener(e -> addFoodItemSelectorToPanel(panelRef, foodNamesForPanel, selectedFoodNamesForPanel));
            // You can wire up swapBtn for the left panel as needed
        } else {
            importBtn.addActionListener(e -> importMeal(panelRef, foodNamesForPanel, selectedFoodNamesForPanel));
            addBtn.addActionListener(e -> addFoodItemSelectorToPanel(panelRef, foodNamesForPanel, selectedFoodNamesForPanel));
            // You can wire up swapBtn for the right panel as needed
        }
        return bar;
    }

    // // Helper for right panel: open FoodSearchDialog and add food item to the given panel
    private void addFoodItemSelectorToPanel(JPanel panel, Map<Long, String> foodNamesForPanel, Map<Long, FoodItem> selectedFoodNamesForPanel) {
        FoodSearchDialog dialog = new FoodSearchDialog((Frame) SwingUtilities.getWindowAncestor(this), foodNamesForPanel);
        Long id = dialog.showDialog();
        String selectedFood = foodNamesForPanel.get(id);
        if (id != null) {
            addFoodItemLabelToPanel(panel, id, selectedFood, foodNamesForPanel, selectedFoodNamesForPanel);
            foodNamesForPanel.remove(id);
            FoodItem selectedFoodItem = db.loadFoodItem(id);
            selectedFoodNamesForPanel.put(id, selectedFoodItem);
        }
    }

    // Helper for both panels: add a food item label to a given panel, with full edit/remove support
    private void addFoodItemLabelToPanel(JPanel panel, Long id, String foodName, Map<Long, String> foodNamesForPanel, Map<Long, FoodItem> selectedFoodNamesForPanel) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        JLabel foodLabel = new JLabel(foodName);
        foodLabel.setName(id.toString());
        JButton editButton = new JButton("Edit");
        JButton removeButton = new JButton("X");

        FoodItem selectedFoodItem = db.loadFoodItem(id);
        selectedFoodNamesForPanel.put(id, selectedFoodItem);
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

                FoodItem newFoodItem = db.loadFoodItem(id);
                selectedFoodNamesForPanel.put(newId, newFoodItem);
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