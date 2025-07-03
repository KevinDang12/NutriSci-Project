package com.nutrisci.meal;

import java.time.LocalDate;

public class LunchFactory extends MealFactory {
    @Override
    public Meal createMeal(LocalDate date) {
        Lunch lunch = new Lunch();
        if (lunch.canAddToDate(date)) {
            lunch.date = date;
            return lunch;
        } else {
            throw new IllegalArgumentException("LunchFactory can only create LUNCH meals.");
        }
    }
} 