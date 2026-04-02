package com.timetable.model;

public class Subject {
    private String code;
    private String name;
    private int credits;
    private boolean hasLab;
    private int theoryHoursPerWeek;
    private int labHoursPerWeek;

    public Subject(String code, String name, int credits, boolean hasLab) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.hasLab = hasLab;
        this.theoryHoursPerWeek = credits;
        this.labHoursPerWeek = hasLab ? 2 : 0;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public boolean hasLab() { return hasLab; }
    public int getTheoryHoursPerWeek() { return theoryHoursPerWeek; }
    public int getLabHoursPerWeek() { return labHoursPerWeek; }

    @Override
    public String toString() { return name + (hasLab ? " (Theory+Lab)" : " (Theory)"); }
}