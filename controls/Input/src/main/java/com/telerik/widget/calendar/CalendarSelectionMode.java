package com.telerik.widget.calendar;

/**
 * An enumeration with the different selection modes
 * for {@link com.telerik.widget.calendar.RadCalendarView}.
 */
public enum CalendarSelectionMode {

    /**
     * When {@link com.telerik.widget.calendar.RadCalendarView}
     * is in None selection mode, no date can be selected.
     */
    None,
    /**
     * When {@link com.telerik.widget.calendar.RadCalendarView}
     * is in Single selection mode, only one date
     * can be selected at a time.
     */
    Single,
    /**
     * When {@link com.telerik.widget.calendar.RadCalendarView}
     * is in Range selection mode, the selected dates can be more than
     * one as long as they form a range of consecutive dates.
     */
    Range,
    /**
     * When {@link com.telerik.widget.calendar.RadCalendarView}
     * is in Multiple selection mode, the selected dates can be more than
     * one at the same time.
     */
    Multiple
}
