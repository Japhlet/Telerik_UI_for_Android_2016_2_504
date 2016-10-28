package com.telerik.android.common;

import java.util.Calendar;

/**
 * This class provides methods for performing date-time calculations and is internally used by the Chart engine.
 */
public class DateTimeExtensions {

    /**
     * Subtracts two {@link java.util.Calendar} values.
     *
     * @param value  the first value to subtract from.
     * @param value2 the second value to subtract.
     * @return an instance of the {@link TimeSpan} class representing the difference.
     */
    public static TimeSpan subtract(Calendar value, Calendar value2) {
        long left = value.getTimeInMillis();
        long right = value2.getTimeInMillis();

        return TimeSpan.fromMilliseconds(left - right);
    }

    /**
     * Gets the quarter component of the date represented by the DATE instance.
     */
    public static int getQuarterOfYear(Calendar dateTime) {
        return ((dateTime.get(Calendar.MONTH) - 1) / 3) + 1;
    }

    /**
     * Gets an integer representing the current hour from the time the  year within the provided {@link Calendar} instance started.
     *
     * @param dateTime the {@link Calendar} value representing the year to get the hour from.
     * @return the hour.
     */
    public static int getHourOfYear(Calendar dateTime) {
        return ((dateTime.get(Calendar.DAY_OF_YEAR) - 1) * 24) + dateTime.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Gets an integer representing the current minute from the time the  year within the provided {@link Calendar} instance started.
     *
     * @param dateTime the {@link Calendar} value representing the year to get the minute from.
     * @return the minute.
     */
    public static int getMinuteOfYear(Calendar dateTime) {
        return ((getHourOfYear(dateTime) - 1) * 60) + dateTime.get(Calendar.MINUTE);
    }

    /**
     * Gets an integer representing the current second from the time the  year within the provided {@link Calendar} instance started.
     *
     * @param dateTime the {@link Calendar} value representing the year to get the second from.
     * @return the minute.
     */
    public static int getSecondOfYear(Calendar dateTime) {
        return ((getMinuteOfYear(dateTime) - 1) * 60) + dateTime.get(Calendar.SECOND);
    }
}

