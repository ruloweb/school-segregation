package com.ruloweb.abm.schoolsegregation.agent;

import sim.engine.SimState;
import sim.engine.Steppable;

import java.util.*;

public class School implements Steppable {
    int id;
    long prevTotal;
    long prevRed;
    long prevBlue;
    long total;
    long red;
    long blue;
    long capacity;
    final HashMap<Integer, LinkedList<Household>> enrollmentList = new HashMap<>();

    public School(int id) {
        this.id = id;
    }

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
    }

    public void addToEnrollmentList(int pos, Household household) {
        if (!this.enrollmentList.containsKey(pos)) {
            this.enrollmentList.put(pos, new LinkedList<>());
        }
        this.enrollmentList.get(pos).add(household);
    }

    public void enrollStudents(int pos) {
        // TODO: it should use the random object from the SchoolSegregation object
        if (!this.enrollmentList.containsKey(pos)) {
            return;
        }

        LinkedList<Household> householders = this.enrollmentList.get(pos);
        Collections.shuffle(householders);
        int max = (int)(this.capacity - this.total);
        int size = householders.size();
        List<Household> householdersSublist = householders.subList(0, Math.min(size, max));

        for (Household household: householdersSublist) {
            if (!household.hasMoved()) {
                household.getSchool().withdraw(household);
                this.enroll(household);
                household.setSchool(this);
                household.setAsMoved();
            }
        }
    }

    @Override
    public void step(SimState simState) {}

    @Override
    public String toString() {
        return "School " + this.id;
    }
}
