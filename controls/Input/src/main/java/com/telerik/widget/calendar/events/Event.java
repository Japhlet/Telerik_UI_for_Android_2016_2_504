package com.telerik.widget.calendar.events;

import android.graphics.Color;

/**
 * Data model for events displayed by {@link com.telerik.widget.calendar.RadCalendarView}.
 */
public class Event {
    private static final int DEFAULT_EVENT_COLOR = Color.parseColor("#B233B5E5");

    private int calendarId;
    private int eventColor;
    private String title;
    private long startDate;
    private long endDate;
    private boolean allDay;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.events.Event} class.
     *
     * @param title     the event's title
     * @param startDate the event's start date
     * @param endDate   the event's end date
     * @throws java.lang.IllegalArgumentException If the start date is after the end date or if they are the same
     */
    public Event(String title, long startDate, long endDate) {
        if (startDate > endDate) {
            throw new IllegalArgumentException("endDate should be after startDate.");
        }

        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventColor = DEFAULT_EVENT_COLOR;
    }

    /**
     * Returns the id of the calendar that contains this event.
     *
     * @return the calendar id
     */
    public int getCalendarId() {
        return calendarId;
    }

    /**
     * Sets the id of the calendar that contains this event.
     *
     * @param calendarId the calendar id
     */
    public void setCalendarId(int calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * Returns the color that should be used for representation of this event.
     *
     * @return the event color
     */
    public int getEventColor() {
        return eventColor;
    }

    /**
     * Sets the color that will be used for representation of this event.
     *
     * @param eventColor the event color
     */
    public void setEventColor(int eventColor) {
        this.eventColor = eventColor;
    }

    /**
     * Returns the title of this event.
     *
     * @return the title of the event
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this event.
     *
     * @param title the title of the event
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the start date of this event.
     *
     * @return the start date of the event
     */
    public long getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of this event.
     *
     * @param startDate the start date of the event
     * @throws java.lang.IllegalArgumentException If the start date is after the end date or if they are the same
     */
    public void setStartDate(long startDate) {
        if (startDate > endDate) {
            throw new IllegalArgumentException("endDate should be after startDate.");
        }

        this.startDate = startDate;
    }

    /**
     * Returns the end date of this event.
     *
     * @return the end date of the event
     */
    public long getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of this event.
     *
     * @param endDate the end date of the event
     * @throws java.lang.IllegalArgumentException If the start date is after the end date or if they are the same
     */
    public void setEndDate(long endDate) {
        if (startDate > endDate) {
            throw new IllegalArgumentException("endDate should be after startDate.");
        }

        this.endDate = endDate;
    }

    /**
     * Returns a value which indicates whether this is an all day event.
     *
     * @return whether this is an all day event
     */
    public boolean isAllDay() {
        return allDay;
    }

    /**
     * Sets a value which indicates whether this is an all day event.
     *
     * @param allDay whether this is an all day event
     */
    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }
}
