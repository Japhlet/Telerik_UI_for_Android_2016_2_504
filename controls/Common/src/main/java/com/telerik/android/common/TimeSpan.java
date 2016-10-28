package com.telerik.android.common;

import java.security.InvalidParameterException;
import java.util.Calendar;

/**
 * Represents a period in time specified by a minimum time and a maximum time.
 */
public class TimeSpan implements Comparable {

    private Calendar min; // TODO See if calendar can be replaced with something lighter
    private Calendar max;

    /**
     * Creates a new {@link TimeSpan} instance from the specified days.
     *
     * @param days The time span in days.
     * @return A new {@link TimeSpan} instance.
     */
    public static TimeSpan fromDays(long days) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(0);

        Calendar max = Calendar.getInstance();
        max.setTimeInMillis(days * 24 * 60 * 60 * 1000);

        return new TimeSpan(min, max);
    }

    /**
     * Creates a new {@link TimeSpan} instance from the specified seconds.
     *
     * @param seconds The time span in seconds.
     * @return A new {@link TimeSpan} instance.
     */
    public static TimeSpan fromSeconds(long seconds) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(0);

        Calendar max = Calendar.getInstance();
        max.setTimeInMillis(seconds * 1000);
        return new TimeSpan(min, max);
    }

    /**
     * Creates a new {@link TimeSpan} instance from the specified minutes.
     *
     * @param minutes The time span in minutes.
     * @return A new {@link TimeSpan} instance.
     */
    public static TimeSpan fromMinutes(long minutes) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(0);

        Calendar max = Calendar.getInstance();
        max.setTimeInMillis(minutes * 60 * 1000);

        return new TimeSpan(min, max);
    }

    /**
     * Creates a new {@link TimeSpan} instance from the specified hours.
     *
     * @param hours The time span in hours.
     * @return A new {@link TimeSpan} instance.
     */
    public static TimeSpan fromHours(long hours) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(0);

        Calendar max = Calendar.getInstance();
        max.setTimeInMillis(hours * 60 * 60 * 1000);

        return new TimeSpan(min, max);
    }

    /**
     * Creates a time span from the given milliseconds.
     *
     * @param millis The milliseconds.
     * @return A new {@link TimeSpan}
     */
    public static TimeSpan fromMilliseconds(long millis) {
        Calendar min = Calendar.getInstance();
        min.setTimeInMillis(0);

        Calendar max = Calendar.getInstance();
        max.setTimeInMillis(millis);

        return new TimeSpan(min, max);
    }

    /**
     * Creates a {@link TimeSpan} with the specified min and max values.
     *
     * @param min The start of the time span.
     * @param max The end of the time span.
     */
    public TimeSpan(Calendar min, Calendar max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Creates an instance of the {@link TimeSpan} class with zero for minimum and maximum values.
     */
    public TimeSpan() {
        this.min = Calendar.getInstance();
        this.min.setTimeInMillis(0);

        this.max = Calendar.getInstance();
        this.max.setTimeInMillis(0);
    }

    /**
     * Creates an empty {@link TimeSpan} instance.
     *
     * @return An empty {@link TimeSpan} instance.
     */
    public static TimeSpan getZero() {
        return new TimeSpan();
    }

    /**
     * Gets the total days contained in this {@link TimeSpan}.
     *
     * @return The total days contained in this {@link TimeSpan}.
     */
    public int getTotalDays() {
        return this.max.get(Calendar.DAY_OF_YEAR) - this.min.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Gets the milliseconds that represent this {@link TimeSpan}.
     *
     * @return The milliseconds that represent this {@link TimeSpan}.
     */
    public long getMillis() {
        return this.max.getTimeInMillis() - this.min.getTimeInMillis();
    }

    @Override
    public int compareTo(Object param) {
        if (!(param instanceof TimeSpan)) {
            throw new InvalidParameterException("Argument is not a TimeSpan instance.");
        }

        TimeSpan timeSpanParam = (TimeSpan) param;

        return ((Long) this.getMillis()).compareTo(timeSpanParam.getMillis());
    }
}