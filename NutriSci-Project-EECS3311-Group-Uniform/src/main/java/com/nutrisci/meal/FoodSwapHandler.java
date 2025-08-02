package com.nutrisci.meal;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.nutrisci.model.Goal;
import com.nutrisci.model.User;
import com.nutrisci.service.FoodSwapService;
import com.nutrisci.service.FoodSwapService.FoodSwapSuggestion;
import com.nutrisci.util.UserSessionManager;

public class FoodSwapHandler {

    private FoodSwapService foodSwapService;

    /**
     * Suggest a food swap based on user's goal
     * @return 
     */
    public FoodSwapSuggestion suggestFoodSwap(Component component, Map<Long, FoodItem> selectedFoodNames) {
        foodSwapService = new FoodSwapService();
        if (selectedFoodNames.isEmpty()) {
            JOptionPane.showMessageDialog(component, 
                "Please add at least one food item before requesting a swap suggestion.", 
                "No Food Items", 
                JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        
        // Get the current user's goal from the session
        User currentUser = UserSessionManager.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getGoal() == null) {
            JOptionPane.showMessageDialog(component, 
                "Please set a goal first before requesting food swap suggestions.", 
                "No Goal Set", 
                JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        
        Goal userGoal = currentUser.getGoal();
        List<FoodItem> currentFoodItems = new ArrayList<>(selectedFoodNames.values());
        FoodSwapService.FoodSwapSuggestion suggestion = foodSwapService.suggestSwap(currentFoodItems, userGoal);
        
        if (suggestion == null) {
            JOptionPane.showMessageDialog(component, 
                "No suitable swap suggestions found for your current meal.", 
                "No Suggestions", 
                JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        
        // Show swap suggestion dialog
        // showSwapSuggestionDialog(suggestion);
        return suggestion;
    }
    
    /**
     * Show dialog with swap suggestion
     * helped by AI
     */
    public boolean showSwapSuggestionDialog(Component component, FoodSwapService.FoodSwapSuggestion suggestion) {
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
        
        int choice = JOptionPane.showConfirmDialog(component, 
            message, 
            "Food Swap Suggestion", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            // applySwap(suggestion);
            return true;
        }

        return false;
    }
    
    /**
     * Suggest food swap for a specific panel (used in compare mode)
     */
    public void suggestFoodSwapForPanel(Component component, JPanel panel, Map<Long, String> foodNamesForPanel, Map<Long, FoodItem> selectedFoodNamesForPanel) {
        if (selectedFoodNamesForPanel.isEmpty()) {
            JOptionPane.showMessageDialog(component, 
                "Please add at least one food item before requesting a swap suggestion.", 
                "No Food Items", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get the current user's goal from the session
        User currentUser = UserSessionManager.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getGoal() == null) {
            JOptionPane.showMessageDialog(component, 
                "Please set a goal first before requesting food swap suggestions.", 
                "No Goal Set", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Goal userGoal = currentUser.getGoal();
        List<FoodItem> currentFoodItems = new ArrayList<>(selectedFoodNamesForPanel.values());
        FoodSwapService.FoodSwapSuggestion suggestion = foodSwapService.suggestSwap(currentFoodItems, userGoal);
        
        if (suggestion == null) {
            JOptionPane.showMessageDialog(component, 
                "No suitable swap suggestions found for your current meal.", 
                "No Suggestions", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show swap suggestion dialog for this panel
        showSwapSuggestionDialogForPanel(component, suggestion, panel, foodNamesForPanel, selectedFoodNamesForPanel);
    }
    
    /**
     * Show swap suggestion dialog for a specific panel
     */
    private void showSwapSuggestionDialogForPanel(Component component,
                                                FoodSwapService.FoodSwapSuggestion suggestion, 
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
        
        int choice = JOptionPane.showConfirmDialog(component, 
            message, 
            "Food Swap Suggestion", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            applySwapForPanel(component, suggestion, panel, foodNamesForPanel, selectedFoodNamesForPanel);
        }
    }
    
    /**
     * Apply swap for a specific panel
     */
    private void applySwapForPanel(Component component,
                                  FoodSwapService.FoodSwapSuggestion suggestion, 
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
            
            JOptionPane.showMessageDialog(component, 
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
