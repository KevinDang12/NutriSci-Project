package com.nutrisci.visualization;

import org.jfree.chart.JFreeChart;
import java.time.LocalDate;

public interface ChartDisplayStrategy {
    JFreeChart generateChart(LocalDate date);
    void updateChart(LocalDate date);
}