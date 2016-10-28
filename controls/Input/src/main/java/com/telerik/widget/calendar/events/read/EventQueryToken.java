package com.telerik.widget.calendar.events.read;

import com.telerik.widget.calendar.CalendarTools;

/**
 * This class holds the query tokens to be used in building the query for reading the events from the calendars on the current phone device.
 */
public class EventQueryToken {

    /**
     * The selection for filtering by calendar id.
     */
    public static final String CALENDAR_ID_SELECTION = "calendar_id=?";
    /**
     * The selection for filtering by owner account.
     */
    public static final String CALENDAR_OWNER_SELECTION = "ownerAccount=?";

    private long minDate;
    private long maxDate;

    /**
     * Gets the query tokens for accessing the default calendar of the phone device.
     *
     * @return the default query tokens.
     */
    public static EventQueryToken getDefaultCalendar() {
        return getCalendarById("1");
    }

    /**
     * Gets the query tokens for accessing a calendar by using its id.
     *
     * @param id the id of the calendar.
     * @return the query tokens for accessing the calendar with the specified id.
     */
    public static EventQueryToken getCalendarById(String id) {
        return new EventQueryToken(CALENDAR_ID_SELECTION, new String[]{id});
    }

    /**
     * Gets the query tokens for accessing calendars by providing their IDs.
     *
     * @param calendarIDs a collection of the IDs of the calendars.
     * @return the query token for accessing the calendars with the provided IDs.
     */
    public static EventQueryToken getCalendarsById(String[] calendarIDs) {
        return new EventQueryToken(getRepeatingWhereClause(CALENDAR_ID_SELECTION, calendarIDs.length), calendarIDs);
    }

    /**
     * Gets the query token for accessing a calendar belonging to a specific user.
     *
     * @param owner the name of the user owning the calendar.
     * @return the query token for accessing the calendar belonging to the specified user.
     */
    public static EventQueryToken getCalendarsByOwner(String owner) {
        return new EventQueryToken(CALENDAR_OWNER_SELECTION, new String[]{owner});
    }

    /**
     * Gets the query token for accessing a calendar belonging to a specific collection of users.
     *
     * @param owners the names of the users owning the calendars.
     * @return the query token for accessing the calendars belonging to the specified users.
     */
    public static EventQueryToken getCalendarsByOwners(String[] owners) {
        return new EventQueryToken(getRepeatingWhereClause(CALENDAR_OWNER_SELECTION, owners.length), owners);
    }

    /**
     * The selection of the query. Not to be used directly. Use {@link #buildSelection()} instead.
     */
    public String selection;
    /**
     * The arguments of the query.
     */
    public String[] selectionArgs;

    /**
     * Creates a new instance of the {@link EventQueryToken} class.
     *
     * @param selection     the base selection to be used.
     * @param selectionArgs the selection arguments to be used.
     */
    public EventQueryToken(String selection, String[] selectionArgs) {
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }

    /**
     * Gets the min date for the query. The events before this date will not be included in the query.
     * Default value is 0, which means all events before the max date, if such is present will be included.
     *
     * @return the current min date.
     */
    public long getMinDate() {
        return minDate;
    }

    /**
     * Sets the min date for the query. The events before this date will not be included in the query.
     * Default value is 0, which means all events before the max date, if such is present will be included.
     *
     * @param minDate the new min date.
     */
    public void setMinDate(long minDate) {
        this.minDate = CalendarTools.getDateStart(minDate);
        if (maxDate != 0 && minDate > maxDate)
            throw new IllegalArgumentException("minDate must be lesser than maxDate!");
    }

    /**
     * Gets the max date for the query. The events after this date will not be included in the query.
     * Default value is 0, which means all events after the min date, if such is present will be included.
     *
     * @return the current max date.
     */
    public long getMaxDate() {
        return maxDate;
    }

    /**
     * Sets the max date for the query. The events after this date will not be included in the query.
     * Default value is 0, which means all events after the min date, if such is present will be included.
     *
     * @param maxDate the new max date.
     */
    public void setMaxDate(long maxDate) {
        this.maxDate = CalendarTools.getDateEnd(maxDate);
        if (minDate != 0 && maxDate < minDate)
            throw new IllegalArgumentException("maxDate must be greater than minDate!");
    }

    /**
     * Sets the min and max dates for the query. The events before the min date and the events after the max date will not be included in the query.
     * Default value is 0, which means all events after the min date and before the max date will be included.
     *
     * @param minDate the new minDate.
     * @param maxDate the new max date.
     */
    public void setRange(long minDate, long maxDate) {
        setMinDate(minDate);
        setMaxDate(maxDate);
    }

    /**
     * Used to add the min and max date in the current selection
     *
     * @return the final version of the selection.
     */
    protected String buildSelection() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(((");
        stringBuilder.append(this.selection);
        stringBuilder.append("))");

        if (this.minDate != 0) {
            stringBuilder.append("AND(dtstart>=");
            stringBuilder.append(this.minDate);
            stringBuilder.append(")");
        }
        if (this.maxDate != 0) {
            stringBuilder.append("AND(dtstart<=");
            stringBuilder.append(this.maxDate);
            stringBuilder.append(")");
        }
        stringBuilder.append(')');

        return stringBuilder.toString();
    }

    private static String getRepeatingWhereClause(String clause, int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(clause);
        for (int i = 0; i < count - 1; i++) {
            stringBuilder.append(")OR(");
            stringBuilder.append(clause);
        }
        return stringBuilder.toString();
    }
}
