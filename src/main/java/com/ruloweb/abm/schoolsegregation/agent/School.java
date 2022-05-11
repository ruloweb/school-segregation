package com.ruloweb.abm.schoolsegregation.agent;

import sim.engine.SimState;
import sim.engine.Steppable;

public class School implements Steppable {
    long prevTotal;
    long prevRed;
    long prevBlue;
    long total;
    long red;
    long blue;
    long capacity;

    public long getTotal() {
        return prevTotal;
    }

    public long getBlue() {
        return prevBlue;
    }

    public long getRed() {
        return prevRed;
    }

    public void setCurrentToPrevious() {
        prevTotal = total;
        prevRed = red;
        prevBlue = blue;
    }

    public void enroll(Household household) {
        this.total++;
        if (household.getType() == HouseholdType.RED) {
            this.red++;
        }
        else {
            this.blue++;
        }
    }

    public void withdraw(Household household) {
        this.total--;
        if (household.getType() == HouseholdType.RED) {
            this.red--;
        }
        else {
            this.blue--;
        }
    }

    public void updateCapacity(double percExtraCapacity) {
        this.capacity = Math.round(this.total + this.total * percExtraCapacity);
    }

    public boolean isFull() {
        return total >= capacity;
    }

    public void reset() {
        setCurrentToPrevious();
        total = red = blue = 0;
    }

    @Override
    public void step(SimState simState) {}
}
