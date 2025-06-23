package com.nutrisci.visualization;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorScheme {
    private final Map<String, Color> nutrientColors;

    public ColorScheme() {
        nutrientColors = new HashMap<>();
        nutrientColors.put("Protein", new Color(70, 130, 180)); // Blue
        nutrientColors.put("Carbs", new Color(60, 179, 113));   // Green
        nutrientColors.put("Fat", new Color(255, 165, 0));      // Orange
    }

    public Color getColor(String nutrient) {
        return nutrientColors.getOrDefault(nutrient, Color.GRAY);
    }
}