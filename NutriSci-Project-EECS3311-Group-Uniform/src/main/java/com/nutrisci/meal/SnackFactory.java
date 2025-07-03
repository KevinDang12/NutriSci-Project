package com.nutrisci.meal;

import java.time.LocalDate;

public class SnackFactory extends MealFactory {
    @Override
    public Meal createMeal(LocalDate date) {
        Snack snack = new Snack();
        if (snack.canAddToDate(date)) {
            snack.date = date;
            return snack;
        } else {
            throw new IllegalArgumentException("SnackFactory can only create SNACK meals.");
        }
    }
} 