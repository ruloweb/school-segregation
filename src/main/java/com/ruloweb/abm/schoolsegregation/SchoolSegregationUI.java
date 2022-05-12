package com.ruloweb.abm.schoolsegregation;

import com.ruloweb.abm.schoolsegregation.agent.Household;
import com.ruloweb.abm.schoolsegregation.agent.HouseholdType;
import com.ruloweb.abm.schoolsegregation.agent.School;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.ShapePortrayal2D;

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
        schoolsPortrayal.setPortrayalForClass(
                School.class,
                new ShapePortrayal2D(ShapePortrayal2D.X_POINTS_TRIANGLE_UP, ShapePortrayal2D.Y_POINTS_TRIANGLE_UP, 5.0, true) {
                    @Override
                    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                        School school = (School)object;

                        if (school.getTotal() == 0) {
                            paint = Color.BLACK;
                        }
                        else {
                            int red = (int)(255 * school.getRed() / school.getTotal());
                            int blue = (int)(255 * school.getBlue() / school.getTotal());
                            paint = new Color(red, 0, blue);
                        }

                        super.draw(object, graphics, info);
                    }
                }
        );
        householdersPortrayal.setField(model.fieldHouseholders);
        householdersPortrayal.setPortrayalForClass(Household.class, new OvalPortrayal2D() {
            @Override
            public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                Household household = (Household)object;
                paint = household.getType() == HouseholdType.RED ? new Color(0xFF5555) : new Color(0x5555FF);
                super.draw(object, graphics, info);
            }
        });

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
