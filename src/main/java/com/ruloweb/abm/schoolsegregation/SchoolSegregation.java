package com.ruloweb.abm.schoolsegregation;

import com.ruloweb.abm.schoolsegregation.agent.Household;
import com.ruloweb.abm.schoolsegregation.agent.HouseholdType;
import com.ruloweb.abm.schoolsegregation.agent.School;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeMap;

public class SchoolSegregation extends SimState {
    // TODO: add segregation between householders
    final int WIDTH = 160;
    final int HEIGHT = 160;
    final Logger logger = LogManager.getLogger(SchoolSegregation.class);
    final LinkedList<School> schools = new LinkedList<>();
    final LinkedList<Household> households = new LinkedList<>();

    public Continuous2D fieldSchools = new Continuous2D(1.0,WIDTH,HEIGHT);
    public Continuous2D fieldHouseholders = new Continuous2D(1.0,WIDTH,HEIGHT);

    int numHouseholders = 6000;
    int numSchools = 25;
    double percRed = 0.4;
    double percExtraCapacity = 0.25;
    double ethnicPreferenceThreshold = 0.4;
    double f = 0.7;
    double M = 0.8;
    double searchRadiusPerc = 1.0;
    boolean schoolEnrollmentByLottery = false;

    public int getNumHouseholders() {
        return numHouseholders;
    }

    public void setNumHouseholders(int numHouseholders) {
        this.numHouseholders = numHouseholders;
    }

    public Object domNumHouseholders() {
        return new sim.util.Interval(100, 10000);
    }

    public int getNumSchools() {
        return numSchools;
    }

    public void setNumSchools(int numHouseholders) {
        this.numHouseholders = numHouseholders;
    }

    public Object domNumSchools() {
        return new sim.util.Interval(1, 200);
    }

    public double getPercRed() {
        return percRed;
    }

    public void setPercRed(double percRed) {
        this.percRed = percRed;
    }

    public Object domPercRed() {
        return new sim.util.Interval(0, 1.0);
    }

    public double getPercExtraCapacity() {
        return percExtraCapacity;
    }

    public void setPercExtraCapacity(double percExtraCapacity) {
        this.percExtraCapacity = percExtraCapacity;
    }

    public Object domPercExtraCapacity() {
        return new sim.util.Interval(0, 1.0);
    }

    public double getEthnicPreferenceThreshold() {
        return ethnicPreferenceThreshold;
    }

    public void setEthnicPreferenceThreshold(double ethnicPreferenceThreshold) {
        this.ethnicPreferenceThreshold = ethnicPreferenceThreshold;
    }

    public Object domEthnicPreferenceThreshold() {
        return new sim.util.Interval(0, 1.0);
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public Object domF() {
        return new sim.util.Interval(0, 1.0);
    }

    public double getM() {
        return M;
    }

    public void setM(double m) {
        M = m;
    }

    public Object domM() {
        return new sim.util.Interval(0, 1.0);
    }

    public double getSearchRadiusPerc() {
        return searchRadiusPerc;
    }

    public void setSearchRadiusPerc(double searchRadiusPerc) {
        this.searchRadiusPerc = searchRadiusPerc;
    }

    public Object domSearchRadiusPerc() {
        return new sim.util.Interval(0, 1.0);
    }

    public boolean getSchoolEnrollmentByLottery() {
        return schoolEnrollmentByLottery;
    }

    public void setSchoolEnrollmentByLottery(boolean schoolEnrollmentByLottery) {
        this.schoolEnrollmentByLottery = schoolEnrollmentByLottery;
    }

    public double getDissimilarityIndex() {
        double redTotal = 0;
        double blueTotal = 0;
        double dissimilarityIndex = 0;

        for (School school: this.schools) {
            redTotal += school.getRed();
            blueTotal += school.getBlue();
        }

        for (School school: this.schools) {
            dissimilarityIndex += Math.abs(school.getRed() / redTotal - school.getBlue() / blueTotal);
        }

        return 0.5 * dissimilarityIndex;
    }

    public SchoolSegregation(long seed) {
        super(seed);
    }

    public void start() {
        super.start();
        logger.debug("Initializing model");

        fieldSchools.clear();
        fieldHouseholders.clear();
        schools.clear();

        double offsetX = fieldSchools.getWidth() / 10;
        double offsetY = fieldSchools.getHeight() / 10;
        double gridX = fieldSchools.getWidth() / 5;
        double gridY = fieldSchools.getHeight() / 5;

        // Create schools
        for (int i = 0; i < numSchools; i++) {
            School school = new School();

            schools.add(school);

            fieldSchools.setObjectLocation(
                    school,
                    new Double2D(
                            offsetX + gridX * (i % 5),
                            offsetY + gridY * (int)(i / 5)
                    )
            );
        }

        this.schedule.scheduleRepeating((Steppable) this::step);
    }

    private void createHouseholders() {
        // TODO: the number of red householders should be exact, not an approximation.
        for (int i = 0; i < numHouseholders; i++) {
            HouseholdType type = random.nextDouble() < this.percRed ? HouseholdType.RED : HouseholdType.BLUE;
            Household household = new Household(type);
            this.households.add(household);

            fieldHouseholders.setObjectLocation(
                    household,
                    new Double2D(
                            fieldHouseholders.getWidth() * random.nextDouble(),
                            fieldHouseholders.getHeight() * random.nextDouble()
                    )
            );

            household.calculateSchoolsDistance(this, this.schools);
        }
    }

    private void enroll() {
        for (Household household: this.households) {
            School closestSchool = household.getSortedByDistanceSchools().firstEntry().getValue();
            closestSchool.enroll(household);
            household.setSchool(closestSchool);
        }
    }

    private void setCurrentToPrevious() {
        if (this.schedule.getSteps() == 0) {
            for (School school: this.schools) {
                school.setCurrentToPrevious();
            }
        }
    }

    private void calculateSchoolsCapacity() {
        for (School school: this.schools) {
            school.updateCapacity(this.getPercExtraCapacity());
        }
    }

    private void calculateSchoolsEthnicPreferences() {
        for (Household household: this.households) {
            household.calculateSchoolsDistanceByEthnicPreferences(this);
        }
    }

    private void moveUnhappyHouseholders() {
        if (this.getSchoolEnrollmentByLottery()) {
            for (Household household: this.households) {
                if (!household.isHappy(this)) {
                    enrollToSchools(household);
                }
            }
            runLottery();
            enrollStudentsFromEnrollmentList();
        }
        else {
            for (Household household: this.households) {
                if (!household.isHappy(this)) {
                    School oldSchool = household.getSchool();
                    School newSchool = findNewSchool(household);
                    if (newSchool != null) {
                        oldSchool.withdraw(household);
                        newSchool.enroll(household);
                        household.setSchool(newSchool);
                    }
                }
            }
        }
    }

    private School findNewSchool(Household household) {
        TreeMap<Double, School> sortedByEthPrefSchools = household.getSortedByEthPrefSchools();
        School newSchool = null;

        if (sortedByEthPrefSchools.isEmpty()) {
            return null;
        }

        for (School school: sortedByEthPrefSchools.values()) {
            if (!school.isFull()) {
                newSchool = school;
                break;
            }
        }

        return newSchool;
    }

    private void enrollToSchools(Household household) {
        TreeMap<Double, School> sortedByEthPrefSchools = household.getSortedByEthPrefSchools();

        for (School school: sortedByEthPrefSchools.values()) {
            school.addToEnrollmentList(household);
        }
    }

    public void runLottery() {
        for (School school: this.schools) {
            school.runLottery();
        }
    }

    public void enrollStudentsFromEnrollmentList() {
        for (Household household: this.households) {
            for (School school: household.getSortedByEthPrefSchools().values()) {
                if (school.isInLotteryResult(household)) {
                    household.getSchool().withdraw(household);
                    school.enroll(household);
                    household.setSchool(school);
                }
            }
        }
    }

    private void removeHouseholders() {
        this.fieldHouseholders.clear();
        this.households.clear();
    }

    private void resetSchools() {
        for (School school: this.schools) {
            school.reset();
        }
    }

    private void step(SimState state) {
        removeHouseholders();
        createHouseholders();
        enroll();
        setCurrentToPrevious();
        calculateSchoolsCapacity();
        calculateSchoolsEthnicPreferences();
        moveUnhappyHouseholders();
        resetSchools();
    }

    public static void main(String[] args) {
        doLoop(SchoolSegregation.class, args);
        System.exit(0);
    }

}

