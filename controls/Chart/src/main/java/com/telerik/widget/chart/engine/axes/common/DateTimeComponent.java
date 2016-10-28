package com.telerik.widget.chart.engine.axes.common;

/**
 * Represents a component of the {@link java.util.Calendar} structure.
 */
public enum DateTimeComponent {

    /**
     * The {@link java.util.Calendar#YEAR} component.
     */
    YEAR,

    /**
     * The QUARTER component.
     */
    QUARTER,

    /**
     * The {@link java.util.Calendar#MONTH} component.
     */
    MONTH,

    /**
     * The {@link java.util.Calendar#WEEK_OF_MONTH} component. // TODO: Make sure it is of month, not of year.
     */
    WEEK,

    /**
     * The {@link java.util.Calendar#HOUR} component.
     */
    HOUR,

    /**
     * The {@link java.util.Calendar#MINUTE} component.
     */
    MINUTE,

    /**
     * The {@link java.util.Calendar#SECOND} component.
     */
    SECOND,

    /**
     * The {@link java.util.Calendar#MILLISECOND} component.
     */
    MILLISECOND,

    /**
     * The {@link java.util.Calendar#DATE} component. // TODO: Make sure it is just date, not data of month, year etc.
     */
    DATE,

    /**
     * The TIME OF DAY component.
     */
    TIME_OF_DAY,

    /**
     * The {@link java.util.Calendar#DAY_OF_MONTH} component.
     */
    DAY,

    /**
     * The {@link java.util.Calendar#DAY_OF_WEEK} component.
     */
    DAY_OF_WEEK,

    /**
     * The {@link java.util.Calendar#DAY_OF_YEAR} component.
     */
    DAY_OF_YEAR,

    /**
     * The time milliseconds since January 1, 1970 00:00:00.000 GMT (Gregorian).
     */
    TIME_IN_MILLIS
}

