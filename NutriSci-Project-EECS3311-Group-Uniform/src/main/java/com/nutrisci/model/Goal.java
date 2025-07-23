package com.nutrisci.model;

import java.io.Serializable;

// Represents a user's nutrition goal
public class Goal implements Serializable {
    private GoalType type;
    private boolean increase; // true = increase, false = decrease
    private int percent; // 5, 10, or 15

    public Goal(GoalType type, boolean increase, int percent) {
        this.type = type;
        this.increase = increase;
        this.percent = percent;
    }

    public GoalType getType() { return type; }
    public boolean isIncrease() { return increase; }
    public int getPercent() { return percent; }

    public void setType(GoalType type) { this.type = type; }
    public void setIncrease(boolean increase) { this.increase = increase; }
    public void setPercent(int percent) { this.percent = percent; }

    public String getDirectionString() { return increase ? "Increase" : "Decrease"; }

    @Override
    public String toString() {
        return getDirectionString() + " " + type.getDisplayName() + " by " + percent + "%";
    }
} 