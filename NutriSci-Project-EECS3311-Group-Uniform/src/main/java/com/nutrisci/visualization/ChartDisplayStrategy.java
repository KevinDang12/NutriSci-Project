package com.nutrisci.visualization;

import org.jfree.chart.JFreeChart;
import java.time.LocalDate;

// Interface for chart display strategies
public interface ChartDisplayStrategy {
    // Generates a chart for a given date
    JFreeChart generateChart(LocalDate date);
    // Updates the chart for a new date
    void updateChart(LocalDate date);
}