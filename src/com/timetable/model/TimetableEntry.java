package com.timetable.model;

public class TimetableEntry {
    private Subject subject;
    private Staff staff;
    private boolean isLab;
    private boolean isLabPart2;

    public TimetableEntry(Subject subject, Staff staff, boolean isLab, boolean isLabPart2) {
        this.subject = subject;
        this.staff = staff;
        this.isLab = isLab;
        this.isLabPart2 = isLabPart2;
    }

    public Subject getSubject() { return subject; }
    public Staff getStaff() { return staff; }
    public boolean isLab() { return isLab; }
    public boolean isLabPart2() { return isLabPart2; }

    @Override
    public String toString() {
        String type = isLab ? (isLabPart2 ? "[LAB-2]" : "[LAB-1]") : "[T]";
        return subject.getCode() + " " + type + " | " + staff.getName();
    }
}