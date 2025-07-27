package com.nutrisci.meal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.nutrisci.service.FoodSwapService;
import com.nutrisci.model.Goal;
import com.nutrisci.model.User;
import com.nutrisci.util.UserSessionManager;

/**
 * The Meal logging panel
 */
public class MealLoggerPanel extends JPanel {
    private JComboBox<MealType> mealTypeComboBox;
    private JPanel foodItemsPanel;
    private JButton addFoodItemButton;
    private JButton logMealButton;
    private JButton importButton;

    private JButton compareMealButton;
    private JButton calculateButton;
    private JButton foodSwapButton;

    private final Map<Long, String> listOfFoodNames;
    private Map<Long, String> foodNames;
    private Map<Long, FoodItem> selectedFoodNames;

    // Add right panel state
    private Map<Long, String> comparedFoodNames;
    private Map<Long, FoodItem> comparedSelectedFoodNames;

    private JPanel bottomPanel;
    private JScrollPane scrollPane;
    private MealBuilder meal1;
    private MealBuilder meal2;

    private MealManager mealManager;
    private FoodSwapService foodSwapService;

    /**
     * Set up the MealLogger panel
     */
    public MealLoggerPanel() {
        mealManager = new MealManager();
        foodSwapService = new FoodSwapService();

        setLayout(new BorderLayout(10, 10));
        listOfFoodNames = fetchFoodNames();
        foodNames = new HashMap<>(listOfFoodNames);
        selectedFoodNames = new HashMap<>();
        comparedFoodNames = new HashMap<>(listOfFoodNames);
        comparedSelectedFoodNames = new HashMap<>();

        // Top panel for meal type
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Meal Type:"));
        List<MealType> exitingMealTypes = mealManager.getAvailableMealTypes(LocalDate.now());

        mealTypeComboBox = new JComboBox<>();

        for (MealType type : MealType.values()) {
            if (!exitingMealTypes.contains(type) || type.equals(MealType.SNACK)) {
                mealTypeComboBox.addItem(type);
            }
        }
        
        MealType mealType = mealTypeComboBox.getItemAt(0);
        meal1 = new MealBuilder().setMealType(mealType);
        meal2 = new MealBuilder().setMealType(mealType);

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
        foodSwapButton = new JButton("Food Swap");
        foodSwapButton.addActionListener(e -> suggestFoodSwap());

        importButton = new JButton("Import Meal");
        importButton.addActionListener(e -> importMeal(foodItemsPanel, foodNames, selectedFoodNames));

        bottomPanel.add(importButton);
        bottomPanel.add(addFoodItemButton);
        bottomPanel.add(calculateButton);
        bottomPanel.add(compareMealButton);
        bottomPanel.add(foodSwapButton);
        bottomPanel.add(logMealButton);

        add(bottomPanel, BorderLayout.SOUTH);

        compareMealButton.addActionListener(e -> showComparePanels());
    }

    /**
     * Open the meal calculation page
     * @param compare Check if comparing two meals
     */
    private void calculateMealNutrition(boolean compare) {
        if (!compare) {
            CalculateNutritionDialog dialog = new CalculateNutritionDialog((Frame) SwingUtilities.getWindowAncestor(this), selectedFoodNames);
            dialog.showDialog();
        } else {
            CalculateNutritionDialog dialog = new CalculateNutritionDialog((Frame) SwingUtilities.getWindowAncestor(this), selectedFoodNames, comparedSelectedFoodNames);
            dialog.showDialog();
        }
    }

    /**
     * Import the meal to the assoicated food panel
     * @param foodName The food name
     * @param foodNamesForPanel List of available food items
     * @param selectedFoodNamesForPanel List of selected food items
     */
    private void importMeal(JPanel panel, Map<Long, String> foodNamesForPanel, Map<Long, FoodItem> selectedFoodNamesForPanel) {
        MealImportDialog dialog = new MealImportDialog((Frame) SwingUtilities.getWindowAncestor(this));
        Long id = dialog.showDialog();

        if (id != null) {
            List<Long> foodIds = mealManager.importMeal(id);

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
     * Log the meal to the database
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
        
            mealManager.addMeal(meal1.build());

        } else if (selectedFoodNames.size() <= 0) {
            List<FoodItem> foodItems = new ArrayList<>(comparedSelectedFoodNames.values());
            meal2.setFoodItems(foodItems);
            System.out.println(meal2.mealBeingBuilt.foodItems);

            mealManager.addMeal(meal2.build());

        } else {
            openMealSelectionDialog();
        }
        
        resetPanel();
    }

    /**
     * Reset panel to default state
     */
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

        List<MealType> exitingMealTypes = mealManager.getAvailableMealTypes(LocalDate.now());

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
        bottomPanel.add(foodSwapButton);
        bottomPanel.add(logMealButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.revalidate();
        this.repaint();
    }

    /**
     * Dialog to select meal 1 or meal 2 to log to the database
     * helped by AI
     */
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
            mealManager.addMeal(meal1.build());
            dialog.dispose();
        });
    
        meal2Button.addActionListener(e -> {
            List<FoodItem> foodItems = new ArrayList<>(comparedSelectedFoodNames.values());
            meal2.setFoodItems(foodItems);
            mealManager.addMeal(meal2.build());
            dialog.dispose();
        });
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(meal1Button);
        buttonPanel.add(meal2Button);
    
        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Fetch the list of food names from the database
     * @return The Map of food id and their food name
     */
    private Map<Long, String> fetchFoodNames() {
        try {
            return mealManager.getFoodItems();
        } catch (Exception e) {
            System.err.println("Error getting food items.");
            return null;
        }
    }

    /**
     * Get the id from the selected food name
     * @param selectedFood The food name
     * @return The food id
     */
    public Long getSelectedFoodId(String selectedFood) {
        if (selectedFood == null) return null;
        for (Map.Entry<Long, String> entry : foodNames.entrySet()) {
            if (entry.getValue().equals(selectedFood)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Create the compare panel between two meals
     * helped by AI
     */
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

    /**
     * Create the meal logging panel for meal 1 or meal 2
     * @param panelRef The panel to logging a meal
     * @param isLeft Is left or right panel
     * @param foodNamesForPanel List of available food items
     * @param selectedFoodNamesForPanel List of selected food items
     * @return The created food panel
     */
    private JPanel createPanelButtonBar(JPanel panelRef, boolean isLeft, Map<Long, String> foodNamesForPanel, Map<Long, FoodItem> selectedFoodNamesForPanel) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton importBtn = new JButton("Import Meal");
        JButton addBtn = new JButton("Add Food Item");
        JButton foodSwapBtn = new JButton("Food Swap");
        bar.add(importBtn);
        bar.add(addBtn);
        bar.add(foodSwapBtn);
        if (isLeft) {
            importBtn.addActionListener(e -> importMeal(foodItemsPanel, foodNames, selectedFoodNames));
            addBtn.addActionListener(e -> addFoodItemSelectorToPanel(panelRef, foodNamesForPanel, selectedFoodNamesForPanel));
            foodSwapBtn.addActionListener(e -> suggestFoodSwapForPanel(panelRef, foodNamesForPanel, selectedFoodNamesForPanel));
            // You can wire up swapBtn for the left panel as needed
        } else {
            importBtn.addActionListener(e -> importMeal(panelRef, foodNamesForPanel, selectedFoodNamesForPanel));
            addBtn.addActionListener(e -> addFoodItemSelectorToPanel(panelRef, foodNamesForPanel, selectedFoodNamesForPanel));
            foodSwapBtn.addActionListener(e -> suggestFoodSwapForPanel(panelRef, foodNamesForPanel, selectedFoodNamesForPanel));
            // You can wire up swapBtn for the right panel as needed
        }
        return bar;
    }

    /**
     * Open FoodSearchDialog and add food item to the given panel
     * @param panel The panel to add the food item
     * @param foodNamesForPanel List of available food items
     * @param selectedFoodNamesForPanel List of selected food items
     */
    private void addFoodItemSelectorToPanel(JPanel panel, Map<Long, String> foodNamesForPanel, Map<Long, FoodItem> selectedFoodNamesForPanel) {
        FoodSearchDialog dialog = new FoodSearchDialog((Frame) SwingUtilities.getWindowAncestor(this), foodNamesForPanel);
        Long id = dialog.showDialog();
        String selectedFood = foodNamesForPanel.get(id);
        if (id != null) {
            addFoodItemLabelToPanel(panel, id, selectedFood, foodNamesForPanel, selectedFoodNamesForPanel);
            foodNamesForPanel.remove(id);
            FoodItem selectedFoodItem = mealManager.loadFoodItem(id);
            selectedFoodNamesForPanel.put(id, selectedFoodItem);
        }
    }

    /**
     * Add a food item label to a given panel
     * @param panel The panel to add the food item
     * @param id The id of the food name
     * @param foodName The food name
     * @param foodNamesForPanel List of available food items
     * @param selectedFoodNamesForPanel List of selected food items
     */
    private void addFoodItemLabelToPanel(JPanel panel, Long id, String foodName, Map<Long, String> foodNamesForPanel, Map<Long, FoodItem> selectedFoodNamesForPanel) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        JLabel foodLabel = new JLabel(foodName);
        foodLabel.setName(id.toString());
        JButton editButton = new JButton("Edit");
        JButton removeButton = new JButton("X");

        FoodItem selectedFoodItem = mealManager.loadFoodItem(id);
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

                FoodItem newFoodItem = mealManager.loadFoodItem(id);
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

    // After adding a meal, update goal progress
    private void afterMealAdded() {
        // TODO: Call updateGoalProgress() or similar logic
        // For now, just print a message
        System.out.println("Goal progress updated after meal added.");
    }
    
    /**
     * Suggest a food swap based on user's goal
     */
    private void suggestFoodSwap() {
        if (selectedFoodNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please add at least one food item before requesting a swap suggestion.", 
                "No Food Items", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get the current user's goal from the session
        User currentUser = UserSessionManager.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getGoal() == null) {
            JOptionPane.showMessageDialog(this, 
                "Please set a goal first before requesting food swap suggestions.", 
                "No Goal Set", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Goal userGoal = currentUser.getGoal();
        List<FoodItem> currentFoodItems = new ArrayList<>(selectedFoodNames.values());
        FoodSwapService.FoodSwapSuggestion suggestion = foodSwapService.suggestSwap(currentFoodItems, userGoal);
        
        if (suggestion == null) {
            JOptionPane.showMessageDialog(this, 
                "No suitable swap suggestions found for your current meal.", 
                "No Suggestions", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show swap suggestion dialog
        showSwapSuggestionDialog(suggestion);
    }
    
    /**
     * Show dialog with swap suggestion
     * helped by AI
     */
    private void showSwapSuggestionDialog(FoodSwapService.FoodSwapSuggestion suggestion) {
        String message = String.format(
            "Swap Suggestion:\n\n" +
            "Replace: %s\n" +
            "With: %s\n\n" +
            "%s\n\n" +
            "Would you like to apply this swap?",
            suggestion.getOriginalItem().getDescription(),
            suggestion.getReplacementItem().getDescription(),
            suggestion.getImprovementDescription()
        );
        
        int choice = JOptionPane.showConfirmDialog(this, 
            message, 
            "Food Swap Suggestion", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            applySwap(suggestion);
        }
    }
    
    /**
     * Apply the suggested swap
     * helped by AI
     */
    private void applySwap(FoodSwapService.FoodSwapSuggestion suggestion) {
        FoodItem originalItem = suggestion.getOriginalItem();
        FoodItem replacementItem = suggestion.getReplacementItem();
        
        // Find and remove the original item
        Long originalId = null;
        for (Map.Entry<Long, FoodItem> entry : selectedFoodNames.entrySet()) {
            if (entry.getValue().getDescription().equals(originalItem.getDescription())) {
                originalId = entry.getKey();
                break;
            }
        }
        
        if (originalId != null) {
            // Remove original item
            selectedFoodNames.remove(originalId);
            foodNames.put(originalId, originalItem.getDescription());
            
            // Add replacement item
            Long replacementId = replacementItem.getId();
            selectedFoodNames.put(replacementId, replacementItem);
            foodNames.remove(replacementId);
            
            // Update the UI
            refreshFoodItemsPanel();
            
            JOptionPane.showMessageDialog(this, 
                "Swap applied successfully!", 
                "Swap Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Refresh the food items panel after a swap
     */
    private void refreshFoodItemsPanel() {
        foodItemsPanel.removeAll();
        
        for (Map.Entry<Long, FoodItem> entry : selectedFoodNames.entrySet()) {
            Long id = entry.getKey();
            FoodItem item = entry.getValue();
            addFoodItemLabelToPanel(foodItemsPanel, id, item.getDescription(), foodNames, selectedFoodNames);
        }
        
        foodItemsPanel.revalidate();
        foodItemsPanel.repaint();
    }
    
    /**
     * Suggest food swap for a specific panel (used in compare mode)
     */
    private void suggestFoodSwapForPanel(JPanel panel, Map<Long, String> foodNamesForPanel, Map<Long, FoodItem> selectedFoodNamesForPanel) {
        if (selectedFoodNamesForPanel.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please add at least one food item before requesting a swap suggestion.", 
                "No Food Items", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get the current user's goal from the session
        User currentUser = UserSessionManager.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getGoal() == null) {
            JOptionPane.showMessageDialog(this, 
                "Please set a goal first before requesting food swap suggestions.", 
                "No Goal Set", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Goal userGoal = currentUser.getGoal();
        List<FoodItem> currentFoodItems = new ArrayList<>(selectedFoodNamesForPanel.values());
        FoodSwapService.FoodSwapSuggestion suggestion = foodSwapService.suggestSwap(currentFoodItems, userGoal);
        
        if (suggestion == null) {
            JOptionPane.showMessageDialog(this, 
                "No suitable swap suggestions found for your current meal.", 
                "No Suggestions", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show swap suggestion dialog for this panel
        showSwapSuggestionDialogForPanel(suggestion, panel, foodNamesForPanel, selectedFoodNamesForPanel);
    }
    
    /**
     * Show swap suggestion dialog for a specific panel
     */
    private void showSwapSuggestionDialogForPanel(FoodSwapService.FoodSwapSuggestion suggestion, 
                                                 JPanel panel, 
                                                 Map<Long, String> foodNamesForPanel, 
                                                 Map<Long, FoodItem> selectedFoodNamesForPanel) {
        String message = String.format(
            "Swap Suggestion:\n\n" +
            "Replace: %s\n" +
            "With: %s\n\n" +
            "%s\n\n" +
            "Would you like to apply this swap?",
            suggestion.getOriginalItem().getDescription(),
            suggestion.getReplacementItem().getDescription(),
            suggestion.getImprovementDescription()
        );
        
        int choice = JOptionPane.showConfirmDialog(this, 
            message, 
            "Food Swap Suggestion", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            applySwapForPanel(suggestion, panel, foodNamesForPanel, selectedFoodNamesForPanel);
        }
    }
    
    /**
     * Apply swap for a specific panel
     */
    private void applySwapForPanel(FoodSwapService.FoodSwapSuggestion suggestion, 
                                  JPanel panel, 
                                  Map<Long, String> foodNamesForPanel, 
                                  Map<Long, FoodItem> selectedFoodNamesForPanel) {
        FoodItem originalItem = suggestion.getOriginalItem();
        FoodItem replacementItem = suggestion.getReplacementItem();
        
        // Find and remove the original item
        Long originalId = null;
        for (Map.Entry<Long, FoodItem> entry : selectedFoodNamesForPanel.entrySet()) {
            if (entry.getValue().getDescription().equals(originalItem.getDescription())) {
                originalId = entry.getKey();
                break;
            }
        }
        
        if (originalId != null) {
            // Remove original item
            selectedFoodNamesForPanel.remove(originalId);
            foodNamesForPanel.put(originalId, originalItem.getDescription());
            
            // Add replacement item
            Long replacementId = replacementItem.getId();
            selectedFoodNamesForPanel.put(replacementId, replacementItem);
            foodNamesForPanel.remove(replacementId);
            
            // Update the UI
            refreshFoodItemsPanelForPanel(panel, foodNamesForPanel, selectedFoodNamesForPanel);
            
            JOptionPane.showMessageDialog(this, 
                "Swap applied successfully!", 
                "Swap Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Refresh food items panel for a specific panel
     */
    private void refreshFoodItemsPanelForPanel(JPanel panel, 
                                              Map<Long, String> foodNamesForPanel, 
                                              Map<Long, FoodItem> selectedFoodNamesForPanel) {
        panel.removeAll();
        
        for (Map.Entry<Long, FoodItem> entry : selectedFoodNamesForPanel.entrySet()) {
            Long id = entry.getKey();
            FoodItem item = entry.getValue();
            addFoodItemLabelToPanel(panel, id, item.getDescription(), foodNamesForPanel, selectedFoodNamesForPanel);
        }
        
        panel.revalidate();
        panel.repaint();
    }
} 