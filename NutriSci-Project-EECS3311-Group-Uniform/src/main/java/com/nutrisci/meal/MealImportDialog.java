package com.nutrisci.meal;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * The Meal Import Dialog
 */
public class MealImportDialog extends JDialog {
    private String selectedMeal;
    private JTextField searchField;
    private JList<String> mealList;
    private DefaultListModel<String> listModel;
    private JButton selectButton;
    private List<String> allMealValues;
    Map<Long, String> meals;

    private MealManager mealManager;

    /**
     * Create the panel to import a meal in the given list
     * @param owner The owner panel
     */
    public MealImportDialog(Frame owner) {
        super(owner, "Import Meal", true);
        mealManager = new MealManager();

        setLayout(new BorderLayout(10, 10));
        setSize(450, 400);
        setLocationRelativeTo(owner);

        searchField = new JTextField();
        listModel = new DefaultListModel<>();

        meals = mealManager.importMeals();

        this.allMealValues = new ArrayList<>(meals.values());

        this.allMealValues.sort(String::compareToIgnoreCase);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);

        allMealValues.sort(Comparator.comparing(entry -> {
            String datePart = entry.substring(0, entry.indexOf(" -")).replace("a.m.", "AM").replace("p.m.", "PM");
            return LocalDateTime.parse(datePart, formatter);
        }));

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

    /**
     * Show dialog and retrieve the selected food ID
     * @return The food ID
     */
    public Long showDialog() {
        setVisible(true);
        return getSelectedMealId();
    }

    /**
     * Get the selected meal ID
     * @return The meal ID
     */
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
