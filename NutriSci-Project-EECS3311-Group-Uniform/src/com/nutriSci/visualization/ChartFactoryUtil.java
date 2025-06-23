package com.nutrisci.visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.awt.*;
import java.time.LocalDate;

public class ChartFactoryUtil {
    public static JFreeChart createNutritionPieChart(PieDataset dataset, LocalDate date, ColorScheme colorScheme) {
        JFreeChart chart = ChartFactory.createPieChart(
            "Nutrition Summary for " + date,
            dataset,
            true,
            true,
            false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));

        dataset.getKeys().forEach(key -> {
            String label = (String) key;
            if (label.contains("Protein")) plot.setSectionPaint(label, colorScheme.getColor("Protein"));
            else if (label.contains("Carbs")) plot.setSectionPaint(label, colorScheme.getColor("Carbs"));
            else if (label.contains("Fat")) plot.setSectionPaint(label, colorScheme.getColor("Fat"));
        });

        return chart;
    }
}