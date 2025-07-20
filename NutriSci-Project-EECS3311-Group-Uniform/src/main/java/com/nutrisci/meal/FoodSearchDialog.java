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

public class FoodSearchDialog extends JDialog {
    private String selectedFood;
    private JTextField searchField;
    private JList<String> foodList;
    private DefaultListModel<String> listModel;
    private JButton selectButton;
    private final Map<Long, String> foodNames;
    private final List<String> allFoodValues;

    public FoodSearchDialog(Frame owner, Map<Long, String> foodNames) {
        super(owner, "Select Food Item", true);
        this.foodNames = foodNames;
        this.allFoodValues = new ArrayList<>(foodNames.values());

        allFoodValues.sort(String::compareToIgnoreCase);

        setLayout(new BorderLayout(10, 10));
        setSize(450, 400);
        setLocationRelativeTo(owner);

        searchField = new JTextField();
        listModel = new DefaultListModel<>();
        allFoodValues.forEach(listModel::addElement);
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
                for (String name : allFoodValues) {
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

    public Long showDialog() {
        setVisible(true);
        return getSelectedFoodId();
    }

    public Long getSelectedFoodId() {
        if (selectedFood == null) return null;
        for (Map.Entry<Long, String> entry : foodNames.entrySet()) {
            if (entry.getValue().equals(selectedFood)) {
                System.out.println(entry.getKey());
                return entry.getKey();
            }
        }
        return null;
    }
}
