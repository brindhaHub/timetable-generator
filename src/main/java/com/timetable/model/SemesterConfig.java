package com.timetable.model;

import java.util.List;

public class SemesterConfig {
    private int totalWorkingWeeks;
    private int holidaysCount;
    private String startTime;
    private String lunchStart;
    private String lunchEnd;
    private String endTime;
    private int periodsPerDay;
    private List<String> periodTimes;

    public SemesterConfig(int totalWorkingWeeks, int holidaysCount,
                          String startTime, String lunchStart,
                          String lunchEnd, String endTime,
                          int periodsPerDay, List<String> periodTimes) {
        this.totalWorkingWeeks = totalWorkingWeeks;
        this.holidaysCount = holidaysCount;
        this.startTime = startTime;
        this.lunchStart = lunchStart;
        this.lunchEnd = lunchEnd;
        this.endTime = endTime;
        this.periodsPerDay = periodsPerDay;
        this.periodTimes = periodTimes;
    }

    public int getTotalWorkingWeeks() { return totalWorkingWeeks; }
    public int getHolidaysCount() { return holidaysCount; }
    public String getStartTime() { return startTime; }
    public String getLunchStart() { return lunchStart; }
    public String getLunchEnd() { return lunchEnd; }
    public String getEndTime() { return endTime; }
    public int getPeriodsPerDay() { return periodsPerDay; }
    public List<String> getPeriodTimes() { return periodTimes; }
}