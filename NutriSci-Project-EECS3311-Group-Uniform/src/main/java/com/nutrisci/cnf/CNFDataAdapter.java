package com.nutrisci.cnf;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CNFDataAdapter {
    Map<String, Integer> columnMapping;
    NumberFormat numberFormat;
    Set<String> validFoodGroups;

    public static List<Map<String, String>> importCSV(String filePath) {
        List<Map<String, String>> rows = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("./CSV/" + filePath))) {
            String[] headers = reader.readNext(); // read header row
            if (headers == null) {
                throw new RuntimeException("Empty CSV file.");
            }

            String[] values;
            while ((values = reader.readNext()) != null) {
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String key = headers[i].trim();
                    String value = i < values.length ? values[i].trim() : "";
                    row.put(key, value);
                }
                rows.add(row);
            }

        } catch (Exception e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }

        return rows;
    }
}
