package com.nutrisci.meal;

import com.nutrisci.cnf.CNFDataAdapter;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MealLoggerGUI extends JPanel {
    private JComboBox<MealType> mealTypeComboBox;
    private JPanel foodItemsPanel;
    private JButton addFoodItemButton;
    private JButton logMealButton;

    private JButton compareMealButton;
    private JButton swapMealButton;
    private JButton calculateButton;

    private List<String> foodNames;
    private List<String> selectedFoodNames;
    // Add right panel state
    private List<String> rightPanelFoodNames;
    private List<String> rightPanelSelectedFoodNames;

    private JPanel bottomPanel;
    private JScrollPane scrollPane;

    public MealLoggerGUI() {
        setLayout(new BorderLayout(10, 10));
        foodNames = fetchFoodNames();
        selectedFoodNames = new ArrayList<>();
        rightPanelFoodNames = new ArrayList<>();
        rightPanelSelectedFoodNames = new ArrayList<>();

        // Top panel for meal type
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Meal Type:"));
        mealTypeComboBox = new JComboBox<>(MealType.values());
        mealTypeComboBox.getModel().setSelectedItem(MealType.SNACK);
        MealBuilder meal = new SnackBuilder();

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
        // logMealButton.addActionListener(e -> logMeal()); // Implement as needed

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
            names.sort(String::compareToIgnoreCase); // Sort alphabetically, case-insensitive
        } catch (Exception e) {
            names.add("Error loading food items");
        }
        return names;
    }

    /**
     * Open a new window instead that allows the user to search for a food item and add it.
     * They can click edit to change the item and swap them around.
     * Sort the food names, swap the food names when editing
     */
    private void addFoodItemSelector() {
        FoodSearchDialog dialog = new FoodSearchDialog((Frame) SwingUtilities.getWindowAncestor(this), foodNames);
        String selectedFood = dialog.showDialog();
        if (selectedFood != null && !selectedFood.isEmpty()) {
            addFoodItemLabel(selectedFood);
        }
    }

    private void addFoodItemLabel(String foodName) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        JLabel foodLabel = new JLabel(foodName);
        JButton editButton = new JButton("Edit");
        JButton removeButton = new JButton("X");

        selectedFoodNames.add(foodName);
        foodNames.remove(foodName);

        editButton.addActionListener(e -> {
            FoodSearchDialog dialog = new FoodSearchDialog((Frame) SwingUtilities.getWindowAncestor(this), foodNames);
            String newFood = dialog.showDialog();
            if (newFood != null && !newFood.isEmpty()) {
                String oldFood = (String) foodLabel.getText();

                selectedFoodNames.remove(oldFood);
                foodNames.add(oldFood);

                selectedFoodNames.add(newFood);
                foodNames.remove(newFood);

                foodLabel.setText(newFood);
                foodNames.sort(String::compareToIgnoreCase); // Ensure foodNames is always sorted
            }
        });

        removeButton.addActionListener(e -> {
            String foodItem = (String) foodLabel.getText();
            selectedFoodNames.remove(foodItem);
            foodNames.add(foodItem);
            foodNames.sort(String::compareToIgnoreCase); // Ensure foodNames is always sorted
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
        // Copy state from left to right
        rightPanelFoodNames = new ArrayList<>(foodNames);
        rightPanelSelectedFoodNames = new ArrayList<>(selectedFoodNames);
        for (String food : rightPanelSelectedFoodNames) {
            addFoodItemLabelToPanel(rightPanel, food, rightPanelFoodNames, rightPanelSelectedFoodNames);
        }
        JPanel rightContainer = new JPanel();
        rightContainer.setLayout(new BorderLayout());
        JScrollPane rightScroll = new JScrollPane(rightPanel);
        rightContainer.add(createPanelButtonBar(rightPanel, false, rightPanelFoodNames, rightPanelSelectedFoodNames), BorderLayout.NORTH);
        rightContainer.add(rightScroll, BorderLayout.CENTER);

        JPanel comparePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        comparePanel.add(leftContainer);
        comparePanel.add(rightContainer);

        this.add(comparePanel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private JPanel createPanelButtonBar(JPanel panelRef, boolean isLeft, List<String> foodNamesForPanel, List<String> selectedFoodNamesForPanel) {
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

    // Helper for right panel: open FoodSearchDialog and add food item to the given panel
    private void addFoodItemSelectorToPanel(JPanel panel, List<String> foodNamesForPanel, List<String> selectedFoodNamesForPanel) {
        FoodSearchDialog dialog = new FoodSearchDialog((Frame) SwingUtilities.getWindowAncestor(this), foodNamesForPanel);
        String selectedFood = dialog.showDialog();
        if (selectedFood != null && !selectedFood.isEmpty()) {
            addFoodItemLabelToPanel(panel, selectedFood, foodNamesForPanel, selectedFoodNamesForPanel);
            foodNamesForPanel.remove(selectedFood);
            selectedFoodNamesForPanel.add(selectedFood);
        }
    }

    // Helper for both panels: add a food item label to a given panel, with full edit/remove support
    private void addFoodItemLabelToPanel(JPanel panel, String foodName, List<String> foodNamesForPanel, List<String> selectedFoodNamesForPanel) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        JLabel foodLabel = new JLabel(foodName);
        JButton editButton = new JButton("Edit");
        JButton removeButton = new JButton("X");

        selectedFoodNamesForPanel.add(foodName);
        foodNamesForPanel.remove(foodName);

        editButton.addActionListener(e -> {
            FoodSearchDialog dialog = new FoodSearchDialog((Frame) SwingUtilities.getWindowAncestor(this), foodNamesForPanel);
            String newFood = dialog.showDialog();
            if (newFood != null && !newFood.isEmpty()) {
                String oldFood = (String) foodLabel.getText();
                selectedFoodNamesForPanel.remove(oldFood);
                foodNamesForPanel.add(oldFood);
                selectedFoodNamesForPanel.add(newFood);
                foodNamesForPanel.remove(newFood);
                foodLabel.setText(newFood);
                foodNamesForPanel.sort(String::compareToIgnoreCase);
            }
        });
        removeButton.addActionListener(e -> {
            String foodItem = (String) foodLabel.getText();
            selectedFoodNamesForPanel.remove(foodItem);
            foodNamesForPanel.add(foodItem);
            foodNamesForPanel.sort(String::compareToIgnoreCase);
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

    // Inner class for food search dialog
    private static class FoodSearchDialog extends JDialog {
        private String selectedFood;
        private JTextField searchField;
        private JList<String> foodList;
        private DefaultListModel<String> listModel;
        private JButton selectButton;

        public FoodSearchDialog(Frame owner, List<String> foodNames) {
            super(owner, "Select Food Item", true);
            setLayout(new BorderLayout(10, 10));
            setSize(450, 400);
            setLocationRelativeTo(owner);

            searchField = new JTextField();
            listModel = new DefaultListModel<>();
            foodNames.forEach(listModel::addElement);
            foodList = new JList<>(listModel);
            foodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(foodList);

            searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
                private void filter() {
                    String filter = searchField.getText().toLowerCase();
                    listModel.clear();
                    for (String name : foodNames) {
                        if (name.toLowerCase().contains(filter)) {
                            listModel.addElement(name);
                        }
                    }
                }
            });

            selectButton = new JButton("Select");
            selectButton.addActionListener(e -> {
                selectedFood = foodList.getSelectedValue();
                setVisible(false);
            });

            foodList.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        selectedFood = foodList.getSelectedValue();
                        setVisible(false);
                    }
                }
            });

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.add(selectButton);

            add(searchField, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        public String showDialog() {
            setVisible(true);
            return selectedFood;
        }
    }
} 