package com.nutrisci.meal;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class OpenMealSelectionDialog extends JDialog {

    private boolean mealSelected = false;
    
    /**
     * Dialog to select meal 1 or meal 2 to log to the database
     * helped by AI
     */
    public OpenMealSelectionDialog(Frame owner, Map<Long, FoodItem> selectedFoodNames, Map<Long, FoodItem> comparedSelectedFoodNames, MealBuilder meal) {
        super(owner, "Select Meal", true);
        setSize(300, 150);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    
        JLabel label = new JLabel("Which meal do you want to save?", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);
    
        JButton meal1Button = new JButton("Meal 1");
        JButton meal2Button = new JButton("Meal 2");
    
        meal1Button.addActionListener(e -> {
            List<FoodItem> foodItems = new ArrayList<>(selectedFoodNames.values());
            meal.setFoodItems(foodItems);
            mealSelected = true;
            dispose();
        });
    
        meal2Button.addActionListener(e -> {
            List<FoodItem> foodItems = new ArrayList<>(comparedSelectedFoodNames.values());
            meal.setFoodItems(foodItems);
            mealSelected = true;
            dispose();
        });
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(meal1Button);
        buttonPanel.add(meal2Button);
    
        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Add listener for X button close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mealSelected = false;
            }
        });
    }

    public boolean isMealSelected() {
        return mealSelected;
    }
}
