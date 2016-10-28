package com.telerik.widget.calendar;

import java.util.Calendar;
import java.util.Locale;

/**
 * Contains methods which ease life while working with dates.
 */
public class CalendarTools {

    static final int MAX_DAYS_IN_A_MONTH = 31;

    static final int DAYS_IN_A_WEEK = 7;
    static final int WEEKS_IN_A_MONTH = 6;

    private static Calendar workCalendar = Calendar.getInstance();
    private static Locale currentLocal;
    private static int hoursOffset;

    static void setLocale(Locale locale) {
        currentLocal = locale;
        workCalendar = Calendar.getInstance(locale);
    }

    static void setCalendar(Calendar calendar) {
        workCalendar = (Calendar)calendar.clone();
    }

    /**
     * Gets a {@link java.lang.Long} that represents the first date in the month
     * that contains the provided date.
     *
     * @param date the date
     * @return the first date of the month that contains the display date
     */
    public static synchronized long getDateStart(long date) {
        workCalendar.setTimeInMillis(date);
        workCalendar.set(Calendar.AM_PM, 0);
        workCalendar.set(Calendar.HOUR, 0);
        workCalendar.set(Calendar.MINUTE, 0);
        workCalendar.set(Calendar.SECOND, 0);
        workCalendar.set(Calendar.MILLISECOND, 0);
        fixTimeZoneDeviation(workCalendar);
        return workCalendar.getTimeInMillis();
    }

    public static synchronized boolean isWeekend(long date) {
        workCalendar.setTimeInMillis(date);
        int dayOfWeek = workCalendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    public static synchronized long getDateEnd(long date) {
        workCalendar.setTimeInMillis(getDateStart(date));
        workCalendar.add(Calendar.DATE, 1);
        fixTimeZoneDeviation(workCalendar);
        workCalendar.add(Calendar.SECOND, -1);
        return workCalendar.getTimeInMillis();
    }

    /**
     * Gets a {@link java.lang.Long} that represents the first date in the month
     * that contains the provided date.
     *
     * @param date the date
     * @return the first date of the month that contains the display date
     */
    public static synchronized long getFirstDateInMonth(long date) {
        workCalendar.setTimeInMillis(date);
        workCalendar.set(Calendar.DAY_OF_MONTH, 1);
        fixTimeZoneDeviation(workCalendar);
        return workCalendar.getTimeInMillis();
    }

    public static synchronized long getFirstDateInWeek(long date) {
        workCalendar.setTimeInMillis(date);
        workCalendar.set(Calendar.DAY_OF_WEEK, 1);
        fixTimeZoneDeviation(workCalendar);
        return workCalendar.getTimeInMillis();
    }

    public static synchronized long getFirstDateInYear(long date) {
        workCalendar.setTimeInMillis(date);
        workCalendar.set(Calendar.DAY_OF_YEAR, 1);
        fixTimeZoneDeviation(workCalendar);
        return workCalendar.getTimeInMillis();
    }

    public static synchronized long getFirstMonthInYear(long date) {
        workCalendar.setTimeInMillis(date);
        workCalendar.set(Calendar.DAY_OF_MONTH, 1);
        workCalendar.set(Calendar.MONTH, 1);
        fixTimeZoneDeviation(workCalendar);
        return workCalendar.getTimeInMillis();
    }

    /**
     * Calculates a new date value based on the given first day to display and the current display mode of the calendar.
     *
     * @param valueIncreases     the new value will be bigger if <code>true</code> and smaller if <code>false</code>
     * @param firstDateToDisplay the date from which to calculate the new value.
     * @return the new value
     */
    public static synchronized long calculateNewValue(boolean valueIncreases, long firstDateToDisplay, CalendarDisplayMode displayMode) {
        workCalendar.setTimeInMillis(firstDateToDisplay);
        switch (displayMode) {
            case Week:
                workCalendar.add(Calendar.DATE, valueIncreases ? 7 : -7);
                break;
            case Month:
                workCalendar.add(Calendar.MONTH, valueIncreases ? 1 : -1);
                break;
            case Year:
                workCalendar.add(Calendar.YEAR, valueIncreases ? 1 : -1);
                break;
        }

        fixTimeZoneDeviation(workCalendar);
        return workCalendar.getTimeInMillis();
    }

    /**
     * Gets a {@link java.lang.Long} that represents the first date that should be displayed
     * in a month that contains the displayed date in accordance with the specified calendar.
     * For example, if the display date is Tuesday, 1st of February and according to the
     * specified calendar the first day of the week is Sunday, the returned value will be the
     * date that was on Sunday on the week that contains the displayed date.
     * In the example, that is 30th of January.
     *
     * @param displayDate the display date
     * @return the first date that will be visible on a
     * calendar that shows the specified display date
     */
    public static synchronized long getFirstDisplayDate(Long displayDate) {
        return getFirstDateOfWeekWith(getFirstDateInMonth(displayDate));
    }

    /**
     * Gets a {@link java.lang.Long} representing the first date of the week that contains the
     * specified displayed date in accordance with the specified calendar.
     * For example, if the display date is Tuesday, 1st of February and according to the
     * specified calendar the first day of the week is Sunday, the returned value will be the
     * date that was on Sunday on the week that contains the displayed date.
     * In the example, that is 30th of January.
     *
     * @param date the date
     * @return the first date of the week that contains the provided date
     */
    public static synchronized long getFirstDateOfWeekWith(Long date) {
        workCalendar.setTimeInMillis(date);

        int firstDayOfWeek = workCalendar.getFirstDayOfWeek();
        int daysToSubtract = workCalendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek;
        if (daysToSubtract < 0) {
            daysToSubtract += CalendarTools.DAYS_IN_A_WEEK;
        }

        workCalendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract);
        fixTimeZoneDeviation(workCalendar);
        return workCalendar.getTimeInMillis();
    }

    /**
     * Gets a {@link java.lang.String} which represent the short name of the day with the
     * specified index in accordance with the first day of the week
     * determined by the locale and in the language that it represents.
     * For example, if the locale is en, this means that the language is english and the
     * first day of the week is Sunday (index 1). If the value of day is 1, the return value will
     * be Sun. If the value of day is 2, the return value will be Mon, etc.
     *
     * @param day the index of the day of the week
     * @return string that represent the short name of the day with the specified index
     * in accordance with the locale
     */
    public static synchronized String getShortDayName(int day) {
        workCalendar.set(Calendar.DAY_OF_WEEK, day);
        return workCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, currentLocal);
    }

    public static synchronized long getLastDateInMonth(long displayDate) {
        workCalendar.setTimeInMillis(displayDate);
        workCalendar.set(Calendar.DAY_OF_MONTH, workCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        fixTimeZoneDeviation(workCalendar);
        return workCalendar.getTimeInMillis();
    }

    public static synchronized long getLastDateInWeek(long date) {
        workCalendar.setTimeInMillis(date);
        workCalendar.set(Calendar.DAY_OF_WEEK, workCalendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        fixTimeZoneDeviation(workCalendar);
        return workCalendar.getTimeInMillis();
    }

    public static synchronized long getLastDateInYear(long date) {
        workCalendar.setTimeInMillis(date);
        workCalendar.set(Calendar.DAY_OF_YEAR, workCalendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        fixTimeZoneDeviation(workCalendar);
        return workCalendar.getTimeInMillis();
    }

    public static synchronized long getLastMonthInYear(long displayDate) {
        workCalendar.setTimeInMillis(displayDate);
        workCalendar.set(Calendar.DAY_OF_MONTH, 1);
        workCalendar.set(Calendar.MONTH, 12);
        fixTimeZoneDeviation(workCalendar);
        return workCalendar.getTimeInMillis();
    }

    /**
     * Fixes the case where calendar looses or gains an extra hour resulting in changing the date and
     * breaking the fragments.
     */
    private static synchronized void fixTimeZoneDeviation(Calendar calendar) {
        hoursOffset = calendar.get(Calendar.HOUR);
        if (hoursOffset > 0)
            if (hoursOffset == 1) {
                calendar.add(Calendar.HOUR, -1);
            } else {
                calendar.add(Calendar.HOUR, 1);
            }
    }
}
