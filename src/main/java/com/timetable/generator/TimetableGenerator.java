package com.timetable.generator;

import com.timetable.model.*;
import java.util.*;

public class TimetableGenerator {
    private TimetableEntry[][] timetable;
    private List<Subject> subjects;
    private List<Staff> staffList;
    private SemesterConfig config;
    private int days = 5;

    public TimetableGenerator(List<Subject> subjects, List<Staff> staffList, SemesterConfig config) {
        this.subjects = subjects;
        this.staffList = staffList;
        this.config = config;
        this.timetable = new TimetableEntry[days][config.getPeriodsPerDay()];
    }

    public TimetableEntry[][] generate() {
        // PASS 1: Place lab sessions first (needs 2 consecutive slots)
        for (Subject s : subjects) {
            if (s.hasLab()) {
                boolean placed = placeLabSession(s);
                if (!placed) {
                    System.out.println("WARNING: Could not place lab for " + s.getName());
                }
            }
        }

        // PASS 2: Place theory sessions
        for (Subject s : subjects) {
            int needed = s.getTheoryHoursPerWeek();
            int placed = 0;
            while (placed < needed) {
                if (placeTheorySession(s)) placed++;
                else {
                    System.out.println("WARNING: Only placed " + placed + "/" + needed
                        + " theory hours for " + s.getName() + " (not enough free slots)");
                    break;
                }
            }
        }
        return timetable;
    }

    private boolean placeLabSession(Subject subject) {
        Staff staff = getStaffFor(subject.getCode());
        for (int day = 0; day < days; day++) {
            for (int period = 0; period < config.getPeriodsPerDay() - 1; period++) {
                if (timetable[day][period] == null && timetable[day][period + 1] == null) {
                    timetable[day][period]     = new TimetableEntry(subject, staff, true, false);
                    timetable[day][period + 1] = new TimetableEntry(subject, staff, true, true);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean placeTheorySession(Subject subject) {
        Staff staff = getStaffFor(subject.getCode());
        // Try to spread across different days
        List<int[]> candidates = new ArrayList<>();
        for (int day = 0; day < days; day++) {
            for (int period = 0; period < config.getPeriodsPerDay(); period++) {
                if (timetable[day][period] == null) {
                    candidates.add(new int[]{day, period});
                }
            }
        }
        Collections.shuffle(candidates, new Random(subject.getCode().hashCode() + candidates.size()));
        if (!candidates.isEmpty()) {
            int[] slot = candidates.get(0);
            timetable[slot[0]][slot[1]] = new TimetableEntry(subject, staff, false, false);
            return true;
        }
        return false;
    }

    private Staff getStaffFor(String subjectCode) {
        return staffList.stream()
            .filter(s -> s.getSubjectCode().equals(subjectCode))
            .findFirst()
            .orElse(null);
    }
}