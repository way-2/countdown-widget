package com.way2.countdown_widget;

import static java.time.temporal.ChronoUnit.DAYS;

import android.util.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class Utils {
    public static DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static class DaysLeftCalculations {
        private final float daysLeft;
        private final float percent;

        public DaysLeftCalculations(final float daysLeft, final float percent) {
            this.daysLeft = daysLeft;
            this.percent = percent;
        }

        public float getDaysLeft() {
            return this.daysLeft;
        }

        public float getPercent() {
            return this.percent;
        }
    }

    public static DaysLeftCalculations calculatePercentLeft(String countdownDateString, String startedDateString, boolean includeWeekends) {
        LocalDate countdownDate = null;
        LocalDate startedDate = null;
        try {
            countdownDate = LocalDate.parse(countdownDateString, myDateFormatter);
            startedDate = LocalDate.parse(startedDateString, myDateFormatter);
        } catch (
                DateTimeParseException dateTimeParseException) {
            Log.w("ERROR", "updateAppWidget: Cannot parse dates using current date");
            countdownDate = LocalDate.now();
            startedDate = LocalDate.now();
        }
        float totalDays = (float) 0;
        float daysLeft = (float) 0;
        if (includeWeekends) {
            totalDays = DAYS.between(startedDate, countdownDate);
            daysLeft = DAYS.between(LocalDate.now(), countdownDate);
        } else {
            totalDays = getWorkingDaysWithoutStream(startedDate, countdownDate);
            daysLeft = getWorkingDaysWithoutStream(LocalDate.now(), countdownDate);
        }
        float percent = 1;
        if (daysLeft > 0) {
            percent = (totalDays - daysLeft) / totalDays;
        }
        return new DaysLeftCalculations(daysLeft, percent);
    }

    private static float getWorkingDaysWithoutStream(LocalDate start, LocalDate end) {
        boolean startOnWeekend = false;

        // If starting at the weekend, move to following Monday
        if(start.getDayOfWeek().getValue() > 5){
            start = start.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            startOnWeekend = true;
        }
        boolean endOnWeekend = false;
        // If ending at the weekend, move to previous Friday
        if(end.getDayOfWeek().getValue() > 5){
            end = end.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
            endOnWeekend = true;
        }
        // Cover case where starting on Saturday and ending following Sunday
        if(start.isAfter(end)){
            return 0;
        }
        // Get total weeks
        long weeks = ChronoUnit.WEEKS.between(start, end);

        long addValue = startOnWeekend || endOnWeekend ? 1 : 0;

        // Add on days that did not make up a full week
        return ( weeks * 5 ) + ( end.getDayOfWeek().getValue() - start.getDayOfWeek().getValue() ) + addValue;
    }
}
