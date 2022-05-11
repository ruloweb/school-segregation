package com.ruloweb.abm.schoolsegregation;

import com.ruloweb.abm.schoolsegregation.agent.Household;
import com.ruloweb.abm.schoolsegregation.agent.School;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;

import javax.swing.*;
import java.awt.*;

public class SchoolSegregationUI extends GUIState {
    private Display2D display;
    private JFrame displayFrame;
    ContinuousPortrayal2D schoolsPortrayal = new ContinuousPortrayal2D();
    ContinuousPortrayal2D householdersPortrayal = new ContinuousPortrayal2D();

    public SchoolSegregationUI() {
        super(new SchoolSegregation(System.currentTimeMillis()));
    }

    public SchoolSegregationUI(SimState state) {
        super(state);
    }

    public Object getSimulationInspectedObject() {
        return state;
    }

    @Override
    public void start() {
        super.start();
        setupPortrayals();
    }

    @Override
    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
    }

    public void setupPortrayals() {
        SchoolSegregation model = (SchoolSegregation)state;

        // tell the portrayals what to portray and how to portray them
        schoolsPortrayal.setField(model.fieldSchools);
        schoolsPortrayal.setPortrayalForClass(School.class, new OvalPortrayal2D(Color.RED));
        householdersPortrayal.setField(model.fieldHouseholders);
        householdersPortrayal.setPortrayalForClass(Household.class, new OvalPortrayal2D());

        // reschedule the displayer
        display.reset();
        display.setBackdrop(Color.WHITE);

        // redraw the display
        display.repaint();
    }

    @Override
    public void init(Controller c) {
        super.init(c);
        display = new Display2D(800, 800, this);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Map Display");
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);

        display.attach(schoolsPortrayal, "Schools");
        display.attach(householdersPortrayal, "Householders");
    }

    @Override
    public void quit() {
        super.quit();
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame =null;
        display =null;
    }

    public static void main(String[] args) {
        SchoolSegregationUI vid = new SchoolSegregationUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }

}
