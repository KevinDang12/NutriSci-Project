package com.nutrisci.meal;

// Enum for different meal types in the app
public enum MealType {
    BREAKFAST {
        @Override
        public MealBuilder createBuilder() {
            System.out.println("Breakfast");
            return new BreakfastBuilder();
        }
    },
    LUNCH {
        @Override
        public MealBuilder createBuilder() {
            System.out.println("Lunch");
            return new LunchBuilder();
        }
    },
    DINNER {
        @Override
        public MealBuilder createBuilder() {
            System.out.println("Dinner");
            return new DinnerBuilder();
        }
    },
    SNACK {
        @Override
        public MealBuilder createBuilder() {
            System.out.println("Snack");
            return new SnackBuilder();
        }
    };

    public abstract MealBuilder createBuilder();
}