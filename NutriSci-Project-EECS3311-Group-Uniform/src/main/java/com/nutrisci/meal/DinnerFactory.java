package com.nutrisci.meal;

import java.time.LocalDate;

public class DinnerFactory extends MealFactory {
    @Override
    public Meal createMeal(LocalDate date) {
        Dinner dinner = new Dinner();
        if (dinner.canAddToDate(date)) {
            dinner.date = date;
            return dinner;
        } else {
            throw new IllegalArgumentException("DinnerFactory can only create DINNER meals.");
        }
    }
} 