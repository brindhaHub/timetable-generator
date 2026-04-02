package com.timetable;

import com.timetable.generator.TimetableGenerator;
import com.timetable.input.InputCollector;
import com.timetable.model.*;
import com.timetable.output.TimetablePrinter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("--------------------------------------------");
        System.out.println("|    COLLEGE TIMETABLE GENERATOR v1.0      |");
        System.out.println("--------------------------------------------");

        InputCollector collector = new InputCollector();

        SemesterConfig semConfig   = collector.collectSemesterConfig();
        List<Subject>  subjects    = collector.collectSubjects();
        List<Staff>    staffList   = collector.collectStaff(subjects);
        int            numClasses  = collector.collectNumberOfClasses();

        TimetablePrinter printer = new TimetablePrinter();

        for (int c = 1; c <= numClasses; c++) {
            String className = "Section-" + (char)('A' + c - 1);
            TimetableGenerator generator = new TimetableGenerator(subjects, staffList, semConfig);
            TimetableEntry[][] timetable = generator.generate();
            printer.print(timetable, semConfig, className);
        }

        System.out.println("\n✅ All timetables generated successfully!");
    }
}