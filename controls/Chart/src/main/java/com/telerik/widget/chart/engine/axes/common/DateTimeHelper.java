package com.telerik.widget.chart.engine.axes.common;

import com.telerik.widget.chart.engine.propertyStore.ValueExtractor;

import java.util.Calendar;

/**
 * Holds methods that are common for working with date times.
 */
public abstract class DateTimeHelper {

    /**
     * Tries to assign the passed value object as value of the date instance.
     *
     * @param value the value.
     * @param date  the date that the value will be assigned to.
     * @return <code>true</code> if the value was successfully assigned and <code>false</code> otherwise.
     * @see ValueExtractor
     */
    public static boolean tryGetDateTime(Object value, ValueExtractor<Calendar> date) {
        if (value instanceof Calendar) {
            date.value = (Calendar) value;
            return true;
        }

        date.value = Calendar.getInstance();
        return false;
    }
}
