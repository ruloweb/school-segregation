package com.ruloweb.abm.schoolsegregation.agent;

import sim.engine.SimState;
import sim.engine.Steppable;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

public class School implements Steppable {
    long prevTotal;
    long prevRed;
    long prevBlue;
    long total;
    long red;
    long blue;
    long capacity;
    final LinkedList<Household> enrollmentList = new LinkedList<>();
    final HashSet<Household> lotteryResult = new HashSet<>();

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
        enrollmentList.clear();
        lotteryResult.clear();
    }

    public void addToEnrollmentList(Household household) {
        this.enrollmentList.add(household);
    }

    public void runLottery() {
        // TODO: it should use the random object from the SchoolSegregation object
        Collections.shuffle(enrollmentList);
        int i = 0, total = (int)(this.capacity - this.total);

        for (Household household: enrollmentList) {
            if (i >= total) {
                break;
            }
            lotteryResult.add(household);
        }
    }

    public boolean isInLotteryResult(Household household) {
        return lotteryResult.contains(household);
    }

    @Override
    public void step(SimState simState) {}
}
