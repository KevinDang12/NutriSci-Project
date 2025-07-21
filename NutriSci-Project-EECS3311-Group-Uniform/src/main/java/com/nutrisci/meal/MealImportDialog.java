package com.nutrisci.meal;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.nutrisci.database.DatabaseManager;

public class MealImportDialog extends JDialog {
    private String selectedMeal;
    private JTextField searchField;
    private JList<String> mealList;
    private DefaultListModel<String> listModel;
    private JButton selectButton;
    private List<String> allMealValues;
    Map<Long, String> meals;

    DatabaseManager db = DatabaseManager.getInstance();

    public MealImportDialog(Frame owner) {
        super(owner, "Import Meal", true);

        setLayout(new BorderLayout(10, 10));
        setSize(450, 400);
        setLocationRelativeTo(owner);

        searchField = new JTextField();
        listModel = new DefaultListModel<>();

        meals = db.importMeals(0);

        this.allMealValues = new ArrayList<>(meals.values());

        allMealValues.forEach(listModel::addElement);
        
        mealList = new JList<>(listModel);
        mealList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(mealList);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }

            private void filter() {
                String filter = searchField.getText().toLowerCase();
                listModel.clear();
                for (String name : allMealValues) {
                    if (name.toLowerCase().contains(filter)) {
                        listModel.addElement(name);
                    }
                }
            }
        });

        selectButton = new JButton("Select");
        selectButton.addActionListener(e -> {
            selectedMeal = mealList.getSelectedValue();
            setVisible(false);
        });

        mealList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    selectedMeal = mealList.getSelectedValue();
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

    public Long showDialog() {
        setVisible(true);
        return getSelectedMealId();
    }

    public Long getSelectedMealId() {
        if (selectedMeal == null) return null;
        for (Map.Entry<Long, String> entry : meals.entrySet()) {
            if (entry.getValue().equals(selectedMeal)) {
                System.out.println(entry.getKey());
                return entry.getKey();
            }
        }
        return null;
    }
}
