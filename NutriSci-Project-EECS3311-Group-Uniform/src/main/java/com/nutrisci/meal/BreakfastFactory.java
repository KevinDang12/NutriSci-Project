package com.nutrisci.meal;

import java.time.LocalDate;

public class BreakfastFactory extends MealFactory {
    @Override
    public Meal createMeal(LocalDate date) {
        Breakfast breakfast = new Breakfast();
        if (breakfast.canAddToDate(date)) {
            breakfast.date = date;
            return breakfast;
        } else {
            throw new IllegalArgumentException("BreakfastFactory can only create BREAKFAST meals.");
        }
    }
} 