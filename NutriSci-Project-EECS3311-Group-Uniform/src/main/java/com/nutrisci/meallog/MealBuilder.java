package com.nutrisci.meallog;

import java.time.LocalDate;
import java.util.List;

public class MealBuilder {
    Meal mealBeingBuilt;
    boolean mealTypeSet, dataSet;
    List<String> buildErrors;
    
    public MealBuilder setMealType(MealType type) {
        LocalDate date = LocalDate.now();
        mealBeingBuilt = MealFactory.createMeal(type, date);
        mealTypeSet = true;
        return this;
    }

    public MealBuilder addFoodItem(FoodItem item, double quantity) {
        mealBeingBuilt.addFoodItem(item, quantity);
        return this;
    }

    public MealBuilder removeFoodItem(FoodItem item) {
        mealBeingBuilt.removeFoodItem(item);
        return this;
    }

    public Meal buildPreview() {
        return MealFactory.duplicateMeal(mealBeingBuilt, LocalDate.now());
    }
}
