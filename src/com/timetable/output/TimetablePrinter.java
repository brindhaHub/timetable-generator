package com.timetable.output;

import com.timetable.model.*;
import java.util.List;

public class TimetablePrinter {
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public void print(TimetableEntry[][] timetable, SemesterConfig config, String className) {
        List<String> periods = config.getPeriodTimes();
        int cols = config.getPeriodsPerDay();
        int colWidth = 22;

        System.out.println("\n--------------------------------------------------");
        System.out.printf( "|  TIMETABLE : %-35s|%n", className);
        System.out.printf( "|   Lunch     : %-35s|%n",
                config.getLunchStart() + " - " + config.getLunchEnd());
        System.out.println("-----------------------------------------------------");

        // Header
        System.out.printf("%-11s", "Day");
        for (int p = 0; p < cols; p++) {
            System.out.printf("| %-" + colWidth + "s", "P" + (p+1) + " [" + periods.get(p) + "]");
        }
        System.out.println("|");
        System.out.println("-".repeat(11 + (colWidth + 2) * cols + 1));

        // Rows
        for (int day = 0; day < 5; day++) {
            System.out.printf("%-11s", DAYS[day]);
            for (int period = 0; period < cols; period++) {
                TimetableEntry entry = timetable[day][period];
                String cell;
                if (entry == null) {
                    cell = "FREE";
                } else {
                    String type = entry.isLab()
                        ? (entry.isLabPart2() ? "LAB②" : "LAB①")
                        : "Theory";
                    cell = entry.getSubject().getCode() + "-" + type
                         + " " + entry.getStaff().getName();
                }
                if (cell.length() > colWidth) cell = cell.substring(0, colWidth);
                System.out.printf("| %-" + colWidth + "s", cell);
            }
            System.out.println("|");
        }
        System.out.println("-".repeat(11 + (colWidth + 2) * cols + 1));
    }
}