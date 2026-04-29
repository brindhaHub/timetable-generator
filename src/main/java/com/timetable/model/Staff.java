package com.timetable.model;

public class Staff {
    private String id;
    private String name;
    private String subjectCode;

    public Staff(String id, String name, String subjectCode) {
        this.id = id;
        this.name = name;
        this.subjectCode = subjectCode;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSubjectCode() { return subjectCode; }

    @Override
    public String toString() { return name; }
}