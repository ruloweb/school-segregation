package com.ruloweb.abm.schoolsegregation.agent;

import com.ruloweb.abm.schoolsegregation.SchoolSegregation;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

import java.util.LinkedList;
import java.util.TreeMap;

public class Household implements Steppable {
    private final HouseholdType type;
    private School school;
    private final TreeMap<Double, School> schools = new TreeMap<>();

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public TreeMap<Double, School> getSortedSchools() {
        return schools;
    }

    public Household(HouseholdType type) {
        this.type = type;
    }

    public void calculateSchoolsDistance(SchoolSegregation model, LinkedList<School> schoolsList) {
        Double2D householdLoc = model.fieldHouseholders.getObjectLocationAsDouble2D(this);

        for (School school: schoolsList) {
            Double2D schoolLoc = model.fieldSchools.getObjectLocationAsDouble2D(school);
            double distance = Math.pow(householdLoc.x - schoolLoc.x, 2) + Math.pow(householdLoc.y - schoolLoc.y, 2);
            this.schools.put(distance, school);
        }
    }

    public HouseholdType getType() {
        return type;
    }

    public double ethnicPreference(School school, double f, double M) {
        double ethnicPreference;
        long same = this.type == HouseholdType.RED ? school.getRed() : school.getBlue();
        long total = school.getTotal();
        double optimalSame = total * f;

        if (same <= optimalSame) {
            ethnicPreference = same / optimalSame;
        }
        else {
            ethnicPreference = M + (((total - same) * (1 - M)) / (total * (1 - f)));
        }

        return ethnicPreference;
    }

    public boolean isHappy(SchoolSegregation model) {
        if (this.school == null) {
            throw new RuntimeException("Can not check happiness if no school has been chosen");
        }

        double ethnicPreference = this.ethnicPreference(this.school, model.getF(), model.getM());
        double threshold = model.random.nextGaussian() * 0.05 + model.getEthnicPreferenceThreshold();

        return ethnicPreference >= threshold;
    }

    @Override
    public void step(SimState simState) {}
}
