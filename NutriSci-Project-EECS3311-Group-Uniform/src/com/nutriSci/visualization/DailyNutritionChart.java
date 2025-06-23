package com.nutrisci.visualization;

import com.nutrisci.meallog.Meal;
import com.nutrisci.meallog.FoodItem;

import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.time.LocalDate;
import java.util.*;

public class DailyNutritionChart implements ChartDisplayStrategy {
    private final NutritionalCalculator calculator = new NutritionalCalculator();
    private final ColorScheme nutritionColors = new ColorScheme();

    private List<Meal> getMealsForDate(LocalDate date) {
        // Placeholder - integrate your real MealManager or DB query here
        return new ArrayList<>();
    }

    @Override
    public JFreeChart generateChart(LocalDate date) {
        List<Meal> dailyMeals = getMealsForDate(date);
        if (dailyMeals == null || dailyMeals.isEmpty()) {
            return handleEmptyDay(date);
        }

        double[] macros = calculator.calculateMacros(dailyMeals);
        double totalCalories = calculator.calculateTotalCalories(macros[0], macros[1], macros[2]);

        PieDataset dataset = calculateMacronutrientData(macros[0], macros[1], macros[2], totalCalories);
        JFreeChart chart = ChartFactoryUtil.createNutritionPieChart(dataset, date, nutritionColors);

        // Optional: Add goal indicators or additional styling
        return chart;
    }

    @Override
    public void updateChart(LocalDate date) {
        // Placeholder for partial update if chart is cached
        generateChart(date);
    }

    private PieDataset calculateMacronutrientData(double protein, double carbs, double fat, double totalCalories) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        Map<String, String> labels = formatNutritionLabels(protein, carbs, fat, totalCalories);

        dataset.setValue(labels.get("Protein"), protein * 4);
        dataset.setValue(labels.get("Carbs"), carbs * 4);
        dataset.setValue(labels.get("Fat"), fat * 9);

        return dataset;
    }

    private Map<String, String> formatNutritionLabels(double protein, double carbs, double fat, double totalCalories) {
        Map<String, String> labelMap = new HashMap<>();

        double proteinCals = protein * 4, carbsCals = carbs * 4, fatCals = fat * 9;

        labelMap.put("Protein", String.format("Protein: %.1fg (%.0f%%)", protein, (proteinCals / totalCalories) * 100));
        labelMap.put("Carbs", String.format("Carbs: %.1fg (%.0f%%)", carbs, (carbsCals / totalCalories) * 100));
        labelMap.put("Fat", String.format("Fat: %.1fg (%.0f%%)", fat, (fatCals / totalCalories) * 100));

        return labelMap;
    }

    private JFreeChart handleEmptyDay(LocalDate date) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("No meals logged", 1);

        JFreeChart chart = ChartFactory.createPieChart(
            "No Data for " + date,
            dataset,
            true,
            true,
            false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("No meals logged", Color.LIGHT_GRAY);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        return chart;
    }
}