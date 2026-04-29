package com.timetable.input;

import com.timetable.model.*;
import java.util.*;

public class InputCollector {
    private Scanner sc = new Scanner(System.in);

    public SemesterConfig collectSemesterConfig() {
        System.out.println("\n=== SEMESTER CONFIGURATION ===");
        System.out.print("Total working weeks in semester: ");
        int weeks = sc.nextInt();
        System.out.print("Number of holidays: ");
        int holidays = sc.nextInt();
        System.out.print("Periods per day (excluding lunch): ");
        int periods = sc.nextInt();
        sc.nextLine();

        System.out.print("Class start time (e.g. 09:00): ");
        String start = sc.nextLine().trim();
        System.out.print("Lunch start time (e.g. 13:00): ");
        String lunchStart = sc.nextLine().trim();
        System.out.print("Lunch end time (e.g. 14:00): ");
        String lunchEnd = sc.nextLine().trim();
        System.out.print("Class end time (e.g. 17:00): ");
        String end = sc.nextLine().trim();

        List<String> periodTimes = new ArrayList<>();
        int[] startParts = parseTime(start);
        int h = startParts[0], m = startParts[1];
        int[] lunchStartParts = parseTime(lunchStart);
        int[] lunchEndParts = parseTime(lunchEnd);

        for (int i = 0; i < periods; i++) {
            int nh = h, nm = m + 60;
            if (nm >= 60) { nh++; nm -= 60; }
            String slot = String.format("%02d:%02d - %02d:%02d", h, m, nh, nm);
            periodTimes.add(slot);
            h = nh; m = nm;
            if (h == lunchStartParts[0] && m == lunchStartParts[1]) {
                h = lunchEndParts[0];
                m = lunchEndParts[1];
            }
        }
        return new SemesterConfig(weeks, holidays, start, lunchStart, lunchEnd, end, periods, periodTimes);
    }

    public List<Subject> collectSubjects() {
        System.out.println("\n=== SUBJECTS ===");
        System.out.print("Number of subjects: ");
        int n = sc.nextInt(); sc.nextLine();
        List<Subject> subjects = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("\nSubject " + (i + 1) + ":");
            System.out.print("  Code (e.g. CS101): "); String code = sc.nextLine().trim();
            System.out.print("  Name: "); String name = sc.nextLine().trim();
            System.out.print("  Credits: "); int credits = sc.nextInt(); sc.nextLine();
            System.out.print("  Has Lab? (y/n): "); boolean hasLab = sc.nextLine().trim().equalsIgnoreCase("y");
            subjects.add(new Subject(code, name, credits, hasLab));
        }
        return subjects;
    }

    public List<Staff> collectStaff(List<Subject> subjects) {
        System.out.println("\n=== STAFF ASSIGNMENT ===");
        List<Staff> staffList = new ArrayList<>();
        int idCounter = 1;
        for (Subject sub : subjects) {
            System.out.print("\nNumber of staff handling \"" + sub.getName() + "\": ");
            int n = sc.nextInt(); sc.nextLine();
            for (int i = 0; i < n; i++) {
                System.out.print("  Staff " + (i + 1) + " name: ");
                String name = sc.nextLine().trim();
                staffList.add(new Staff("S" + idCounter++, name, sub.getCode()));
            }
        }
        return staffList;
    }

    public int collectNumberOfClasses() {
        System.out.print("\nNumber of classes/sections: ");
        int n = sc.nextInt(); sc.nextLine();
        return n;
    }

    private int[] parseTime(String t) {
        String[] p = t.split(":");
        return new int[]{Integer.parseInt(p[0]), Integer.parseInt(p[1])};
    }
}